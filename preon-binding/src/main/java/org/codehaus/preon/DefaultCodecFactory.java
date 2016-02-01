/**
 * Copyright (c) 2009-2016 Wilfred Springer
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package org.codehaus.preon;

import org.codehaus.preon.binding.*;
import org.codehaus.preon.el.Expression;
import nl.flotsam.pecia.*;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.codec.*;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The default {@link CodecFactory} implementation, constructing {@link Codecs} based on all {@link CodecFactory
 * CodecFactories} available by default. The {@link Codec Codecs} constructed by this factory will most likely wrap an
 * {@link org.codehaus.preon.codec.ObjectCodecFactory}. The main difference between that {@link Codec} and the one
 * created by this factory is that this one actually remembers all of the {@link Codec Codecs} that were constructed,
 * making it a better candidate for building documentation.
 *
 * @author Wilfred Springer
 */
public class DefaultCodecFactory implements CodecFactory {

    public <T> Codec<T> create(Class<T> type) {
        return create(null, type, null);
    }

    public <T> Codec<T> create(AnnotatedElement metadata, Class<T> type,
                               ResolverContext context) {
        return create(metadata, type, new CodecFactory[0],
                new CodecDecorator[0], new BindingDecorator[0]);
    }

    public <T> Codec<T> create(AnnotatedElement metadata,
                               Class<T> type,
                               CodecFactory[] addOnFactories,
                               CodecDecorator[] addOnDecorators,
                               BindingDecorator[] bindingDecorators)
    {

        // The actual cache of Codecs.
        final List<Codec<?>> created = new ArrayList<Codec<?>>();

        // Create the default BindingFactory.
        BindingFactory bindingFactory = new StandardBindingFactory();
        bindingFactory = new ConditionalBindingFactory(bindingFactory);
        if (bindingDecorators.length != 0) {
            bindingFactory = new DecoratingBindingFactory(bindingFactory, bindingDecorators);
        }

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
        codecFactory.add(new BoundBufferCodecFactory());
        codecFactory.add(new NumericCodec.Factory());
        codecFactory.add(new StringCodecFactory());
        codecFactory.add(new BooleanCodecFactory());
        codecFactory.add(new EnumCodec.Factory());

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
        codecFactory.add(new ListCodecFactory(top));
        codecFactory.add(new ArrayCodecFactory(top));
        codecFactory.add(new MapCodecFactory(top));

        // Add the (cached) ObjectCodecFactory as a last resort.
        codecFactory.add(cache);
        return new DefaultCodec<T>(top.create(metadata, type, null), created);
    }

    /**
     * The default {@link Codec}.
     *
     * @author Wilfred Springer
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

        public void encode(T value, BitChannel channel, Resolver resolver) throws IOException {
            delegate.encode(value, channel, resolver);
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

        public CodecDescriptor getCodecDescriptor() {
            return new CodecDescriptor() {

                public <C extends SimpleContents<?>> Documenter<C> details(
                        final String bufferReference) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            created.remove(delegate);
                            target.document(delegate.getCodecDescriptor()
                                    .details(bufferReference));
                            for (Codec<?> codec : created) {
                                assert codec != null;
                                CodecDescriptor descriptor = codec
                                        .getCodecDescriptor();
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
                    return delegate.getCodecDescriptor().getTitle();
                }

                public <C extends ParaContents<?>> Documenter<C> reference(
                        Adjective adjective, boolean startWithCapital) {
                    return delegate.getCodecDescriptor().reference(adjective, false);
                }

                public boolean requiresDedicatedSection() {
                    return delegate.getCodecDescriptor()
                            .requiresDedicatedSection();
                }

                public <C extends ParaContents<?>> Documenter<C> summary() {
                    return delegate.getCodecDescriptor().summary();
                }

            };
        }

    }

    /**
     * A {@link CodecFactory}, decorating the {@link Codec}s constructed.
     *
     * @author Wilfred Springer
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
         * @param delegate   The {@link CodecFactory} to which we need to delegate.
         * @param decorators The {@link CodecDecorator}s that will get a chance to decorate the {@link Codec}
         *                   constructed.
         */
        public DecoratingCodecFactory(CodecFactory delegate,
                                      List<CodecDecorator> decorators) {
            this.delegate = delegate;
            this.decorators = decorators;
        }

        /**
         * {@inheritDoc} Every Codec constructed will be decorated by the {@link #decorators}.
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
