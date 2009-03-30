/*
 * Copyright (C) 2008 Wilfred Springer
 * 
 * This file is part of Preon.
 * 
 * Preon is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 * 
 * Preon is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Preon; see the file COPYING. If not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 * 
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 */

package nl.flotsam.preon;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.flotsam.limbo.Expression;
import nl.flotsam.pecia.AnnotatedSection;
import nl.flotsam.pecia.Contents;
import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import nl.flotsam.preon.binding.BindingFactory;
import nl.flotsam.preon.binding.ConditionalBindingFactory;
import nl.flotsam.preon.binding.StandardBindingFactory;
import nl.flotsam.preon.buffer.BitBuffer;
import nl.flotsam.preon.codec.ArrayCodecFactory;
import nl.flotsam.preon.codec.BooleanCodecFactory;
import nl.flotsam.preon.codec.ByteAligningDecorator;
import nl.flotsam.preon.codec.CachingCodecFactory;
import nl.flotsam.preon.codec.CompoundCodecFactory;
import nl.flotsam.preon.codec.EnumCodecFactory;
import nl.flotsam.preon.codec.ExplicitCodecFactory;
import nl.flotsam.preon.codec.InitCodecDecorator;
import nl.flotsam.preon.codec.LazyLoadingCodecDecorator;
import nl.flotsam.preon.codec.ListCodecFactory;
import nl.flotsam.preon.codec.NumberCodecFactory;
import nl.flotsam.preon.codec.ObjectCodecFactory;
import nl.flotsam.preon.codec.SlicingCodecDecorator;
import nl.flotsam.preon.codec.StringCodecFactory;

/**
 * The default {@link CodecFactory} implementation, constructing {@link Codecs}
 * based on all {@link CodecFactory CodecFactories} available by default. The
 * {@link Codec Codecs} constructed by this factory will most likely wrap an
 * {@link ObjectCodecFactory}. The main difference between that {@link Codec}
 * and the one created by this factory is that this one actually remembers all
 * of the {@link Codec Codecs} that were constructed, making it a better
 * candidate for building documentation.
 * 
 * @author Wilfred Springer
 * 
 */
public class DefaultCodecFactory implements CodecFactory {

    public <T> Codec<T> create(Class<T> type) {
        return create(null, type, null);
    }

    public <T> Codec<T> create(AnnotatedElement metadata, Class<T> type,
            ResolverContext context) {
        return create(metadata, type, new CodecFactory[0],
                new CodecDecorator[0]);
    }

    public <T> Codec<T> create(AnnotatedElement metadata, Class<T> type,
            CodecFactory[] addOnFactories, CodecDecorator[] addOnDecorators) {

        // The actual cache of Codecs.
        final List<Codec<?>> created = new ArrayList<Codec<?>>();

        // Create the default BindingFactory.
        BindingFactory bindingFactory = new StandardBindingFactory();
        bindingFactory = new ConditionalBindingFactory(bindingFactory);

        // Eventually, *every* request for a Codec will be processed by this
        // CodecFactory.
        CompoundCodecFactory codecFactory = new CompoundCodecFactory();

        // We need a decorating CodecFactory for all decorators.
        List<CodecDecorator> decorators = new ArrayList<CodecDecorator>();
        decorators.add(new LazyLoadingCodecDecorator());
        decorators.add(new SlicingCodecDecorator());
        decorators.add(new ByteAligningDecorator());
        decorators.add(new InitCodecDecorator());
        decorators.addAll(Arrays.asList(addOnDecorators));

        DecoratingCodecFactory top = new DecoratingCodecFactory(codecFactory,
                decorators);

        // Add additional CodecFactories passed in.
        for (CodecFactory factory : addOnFactories) {
            codecFactory.add(factory);
        }

        // Add other default CodecFactories.
        codecFactory.add(new ExplicitCodecFactory());
        codecFactory.add(new NumberCodecFactory());
        codecFactory.add(new StringCodecFactory());
        codecFactory.add(new BooleanCodecFactory());
        codecFactory.add(new EnumCodecFactory());

        // Create an ObjectCodecFactory that delegates to the
        // CompoundCodecFactory for each of its members.
        ObjectCodecFactory objectCodecFactory = new ObjectCodecFactory(top,
                bindingFactory);

        // Make sure that Codecs created by the ObjectCodecFactory can be
        // cached.
        CachingCodecFactory cache = new CachingCodecFactory(objectCodecFactory,
                new CodecConstructionListener() {

                    public void constructed(Codec<?> codec) {
                        created.add(codec);
                    }

                });

        // Not when you are constructing Lists or arrays of objects.
        codecFactory.add(new ListCodecFactory(codecFactory));
        codecFactory.add(new ArrayCodecFactory(codecFactory));

        // Add the (cached) ObjectCodecFactory as a last resort.
        codecFactory.add(cache);
        return new DefaultCodec<T>(top.create(metadata, type, null), created);
    }

    /**
     * The default {@link Codec}.
     * 
     * @author Wilfred Springer
     * 
     * @param <T>
     */
    private class DefaultCodec<T> implements Codec<T> {

        private Codec<T> delegate;

        private List<Codec<?>> created;

        public DefaultCodec(Codec<T> delegate, List<Codec<?>> created) {
            this.delegate = delegate;
            this.created = created;
        }

        public T decode(BitBuffer buffer, Resolver resolver, Builder builder)
                throws DecodingException {
            return delegate.decode(buffer, resolver, builder);
        }

        public CodecDescriptor getCodecDescriptor() {
            return new CodecDescriptor() {

                public String getLabel() {
                    return delegate.getCodecDescriptor().getLabel();
                }

                public boolean requiresDedicatedSection() {
                    return delegate.getCodecDescriptor()
                            .requiresDedicatedSection();
                }

                public <U> Contents<U> writeSection(Contents<U> contents) {
                    created.remove(delegate);
                    delegate.getCodecDescriptor().writeSection(contents);
                    for (Codec<?> codec : created) {
                        assert codec != null;
                        CodecDescriptor descriptor = codec.getCodecDescriptor();
                        assert descriptor != null;
                        if (descriptor.requiresDedicatedSection()) {
                            descriptor.writeSection(contents);
                        }
                    }
                    return contents;
                }

                public <U, V extends ParaContents<U>> V writePara(V para) {
                    return delegate.getCodecDescriptor().writePara(para);
                }

                public <U> void writeReference(ParaContents<U> contents) {
                    delegate.getCodecDescriptor().writeReference(contents);
                }

            };
        }

        public Class<?>[] getTypes() {
            return delegate.getTypes();
        }

        public Expression<Integer, Resolver> getSize() {
            return delegate.getSize();
        }

        public Class<?> getType() {
            return delegate.getType();
        }

        public CodecDescriptor2 getCodecDescriptor2() {
            return new CodecDescriptor2() {

                public <C extends SimpleContents<?>> Documenter<C> details(
                        final String bufferReference) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            created.remove(delegate);
                            target.document(delegate.getCodecDescriptor2()
                                    .details(bufferReference));
                            for (Codec<?> codec : created) {
                                assert codec != null;
                                CodecDescriptor2 descriptor = codec
                                        .getCodecDescriptor2();
                                assert descriptor != null;
                                if (descriptor.requiresDedicatedSection() && target instanceof Contents) {
                                    AnnotatedSection<?> section = ((Contents<?>) target)
                                            .section(descriptor.getTitle());
                                    section
                                            .mark(descriptor.getTitle())
                                            .document(
                                                    descriptor
                                                            .details(bufferReference));
                                    section.end();
                                }
                            }
                        }
                    };
                }

                public String getTitle() {
                    return delegate.getCodecDescriptor2().getTitle();
                }

                public <C extends ParaContents<?>> Documenter<C> reference(
                        Adjective adjective) {
                    return delegate.getCodecDescriptor2().reference(adjective);
                }

                public boolean requiresDedicatedSection() {
                    return delegate.getCodecDescriptor2()
                            .requiresDedicatedSection();
                }

                public <C extends ParaContents<?>> Documenter<C> summary() {
                    return delegate.getCodecDescriptor2().summary();
                }

            };
        }

    }

    /**
     * A {@link CodecFactory}, decorating the {@link Codec}s constructed.
     * 
     * @author Wilfred Springer
     * 
     */
    private static class DecoratingCodecFactory implements CodecFactory {

        /**
         * The {@link CodecFactory} performing the actual work.
         */
        private CodecFactory delegate;

        /**
         * The decorators for decorating the {@link Codec}s constructed.
         */
        private List<CodecDecorator> decorators;

        /**
         * Constructs a new instance.
         * 
         * @param delegate
         *            The {@link CodecFactory} to which we need to delegate.
         * @param decorators
         *            The {@link CodecDecorator}s that will get a chance to
         *            decorate the {@link Codec} constructed.
         */
        public DecoratingCodecFactory(CodecFactory delegate,
                List<CodecDecorator> decorators) {
            this.delegate = delegate;
            this.decorators = decorators;
        }

        /**
         * {@inheritDoc} Every Codec constructed will be decorated by the
         * {@link #decorators}.
         */
        public <T> Codec<T> create(AnnotatedElement metadata, Class<T> type,
                ResolverContext context) {
            Codec<T> codec = delegate.create(metadata, type, context);
            if (codec != null) {
                for (CodecDecorator decorator : decorators) {
                    codec = decorator.decorate(codec, metadata, type, context);
                }
            }
            return codec;
        }

    }

}
