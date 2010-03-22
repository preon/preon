/**
 * Copyright (C) 2009-2010 Wilfred Springer
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
 * Preon; see the file COPYING. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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
package nl.flotsam.preon.codec;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import nl.flotsam.limbo.BindingException;
import nl.flotsam.limbo.Document;
import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Expressions;
import nl.flotsam.limbo.Reference;
import nl.flotsam.limbo.ReferenceContext;
import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import nl.flotsam.preon.Builder;
import nl.flotsam.preon.Codec;
import nl.flotsam.preon.CodecConstructionException;
import nl.flotsam.preon.CodecDescriptor;
import nl.flotsam.preon.CodecFactory;
import nl.flotsam.preon.Codecs;
import nl.flotsam.preon.DecodingException;
import nl.flotsam.preon.Resolver;
import nl.flotsam.preon.ResolverContext;
import nl.flotsam.preon.annotation.BoundList;
import nl.flotsam.preon.annotation.BoundObject;
import nl.flotsam.preon.annotation.Choices;
import nl.flotsam.preon.buffer.BitBuffer;
import nl.flotsam.preon.buffer.BitBufferUnderflowException;
import nl.flotsam.preon.buffer.SlicedBitBuffer;
import nl.flotsam.preon.channel.BitChannel;
import nl.flotsam.preon.descriptor.Documenters;
import nl.flotsam.preon.descriptor.NullCodecDescriptor2;
import nl.flotsam.preon.limbo.ContextReplacingReference;
import nl.flotsam.preon.util.AnnotationWrapper;
import nl.flotsam.preon.util.CodecDescriptorHolder;
import nl.flotsam.preon.util.EvenlyDistributedLazyList;
import nl.flotsam.preon.util.ParaContentsDocument;

/**
 * A {@link CodecFactory} capable of supporting Lists. <p/> <p> There are a couple of cases that we need to clarify.
 * First of all, the {@link ListCodecFactory} is triggered by the {@link BoundList} annotation. The specific way of
 * decoding the List is determined by a couple of factors. </p> <p/> <ul> <li>The size of the list (in numbers of
 * items)</li> <li>The size of the list (in bytes)</li> <li>The size of the individual items in the list</li> <li>If
 * this is a constant or not</li> </ul> <p/> <p> Things are further complicated by the fact that sometimes these
 * questions can be answered at Codec construction time and in other cases at decoding time. In both cases, we might not
 * be able to determine all of these answers or even any of them at all. </p>
 *
 * @author Wilfred Springer
 */
public class ListCodecFactory implements CodecFactory {

    /**
     * The {@link CodecFactory} that will be used for constructing the
     * {@link Codecs} to construct elements in the List.
     */
    private CodecFactory delegate;

    /**
     * Constructs a new instance, accepting the {@link CodecFactory} creating
     * the {@link Codec Codecs} that will reconstruct elements in the List.
     *
     * @param delegate
     *            The {@link CodecFactory} creating the {@link Codec Codecs}
     *            that will reconstruct elements in the List.
     */
    public ListCodecFactory(CodecFactory delegate) {
        this.delegate = delegate;
    }

    // JavaDoc inherited.
    @SuppressWarnings("unchecked")
    public <T> Codec<T> create(AnnotatedElement metadata, Class<T> type,
                               ResolverContext context) {
        BoundList settings = null;
        if (metadata != null
                && (settings = metadata.getAnnotation(BoundList.class)) != null
                && java.util.List.class.equals(type)) {
            BoundObject objectSettings = getObjectSettings(settings);
            Codec<?> codec = delegate.create(new AnnotationWrapper(
                    objectSettings), objectSettings.type() == Void.class ? Object.class : objectSettings.type(), context);
            if (settings.size().length() == 0) {
                // So, we don't know the number of elements in this list.
                // This means we need to keep on reading elements until we get
                // an 'EOF' or a DecodingException. In case of a
                // DecodingException, the pointer is expected to be moved back
                // to the first position.
                return (Codec<T>) new DynamicListCodec(codec);
            } else if (settings.offset().length() != 0) {
                // So the size is known. If the offset attribute has been set,
                // it means we can calculate the position of the individual
                // elements based on the index of that element.
                Expression<Integer, Resolver> size = getSizeExpression(
                        settings, context);
                Expression<Integer, Resolver> offsets = null;
                CodecDescriptorHolder holder = new CodecDescriptorHolder();
                offsets = Expressions.createInteger(new IndexedResolverContext(
                        context, holder), settings.offset());
                Codec<T> result = (Codec<T>) new OffsetListCodec(offsets, size,
                        codec);
                // TODO:
                holder.setDescriptor(result.getCodecDescriptor());
                return result;
            } else {
                // In this case, there may be a size (number of elements) set,
                // but the size of the individual may not be constant. The size
                // of the individual elements may be determined by some
                // variables read upstream, so we won't know if the size is a
                // constant until we actually start decoding the List.
                Expression<Integer, Resolver> expr = getSizeExpression(
                        settings, context);
                Expression<Integer, Resolver> elementSize = codec.getSize();
                if (elementSize != null && !elementSize.isParameterized()) {
                    return new StaticListCodec(expr, codec);
                } else {
                    Codec<List<Object>> skipCodec = new StaticListCodec(expr,
                            codec);
                    // TODO: Make this more efficient by passing the size as
                    // well.
                    Codec<List<Object>> seqCodec = new DynamicListCodec(codec);
                    return new SwitchingListCodec(skipCodec, seqCodec);
                }
            }
        } else {
            return null;
        }

    }

    /**
     * Returns a {@link BoundObject} annotation containing the properties it
     * shares in common with the {@link BoundList} annotation.
     *
     * @param settings
     *            The {@link BoundList} settings.
     * @return A {@link BoundObject} annotation with settings copied from the
     *         {@link BoundList} annotation passed in.
     */
    private BoundObject getObjectSettings(final BoundList settings) {
        return new BoundObject() {

            public Class<?> type() {
                return settings.type();
            }

            public Class<?>[] types() {
                return settings.types();
            }

            public Class<? extends Annotation> annotationType() {
                return BoundObject.class;
            }

            public boolean ommitTypePrefix() {
                return settings.ommitTypePrefix();
            }

            public Choices selectFrom() {
                return settings.selectFrom();
            }

        };
    }

    /**
     * The {@link Codec} for reading the {@link List} and its members, on
     * demand. Instances of this class will <em>not</em> create a standard
     * {@link List} implementation and populate all of its data immediately.
     * Instead it will create a {@link nl.flotsam.preon.util.UnevenlyDistributedLazyList}, constructing its
     * elements on the fly, only when it is required.
     *
     * <p>
     * Note that this class is called <code><em>Static</em>ListCodec</code>
     * since it relies on the fact that the size of the List and the amount of
     * data required for every list member is known in advance. </p
     *
     */
    private static class StaticListCodec<T> implements Codec<List<T>> {

        /**
         * The number of elements in the list.
         */
        private Expression<Integer, Resolver> size;

        /**
         * The {@link Codec} that will construct elements from the {@link List}.
         */
        private Codec<T> codec;

        /**
         * Constructs a new instance.
         *
         * @param maxSize
         *            An {@link Expression} representing the number of elements
         *            in the {@link List}.
         * @param codec
         *            The {@link Codec} constructing elements in the
         *            {@link List}.
         */
        public StaticListCodec(Expression<Integer, Resolver> maxSize,
                               Codec<T> codec) {
            this.size = maxSize;
            this.codec = codec;
        }

        @SuppressWarnings("unchecked")
        public List<T> decode(BitBuffer buffer, Resolver resolver,
                              Builder builder) throws DecodingException {
            return new EvenlyDistributedLazyList(codec, buffer.getBitPos(),
                    buffer, size.eval(resolver), builder, resolver);
        }

        public void encode(List<T> value, BitChannel channel, Resolver resolver) {
            throw new UnsupportedOperationException();
        }

        public Class<?>[] getTypes() {
            return codec.getTypes();
        }

        public Expression<Integer, Resolver> getSize() {
            return Expressions.multiply(size, codec.getSize());
        }

        public Class<?> getType() {
            return List.class;
        }

        public CodecDescriptor getCodecDescriptor() {
            return new CodecDescriptor() {

                public <C extends SimpleContents<?>> Documenter<C> details(
                        final String bufferReference) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target.para().text("The number of elements in ")
                                    .document(reference(Adjective.THE, false)).text(
                                    " is ").document(
                                    Documenters.forExpression(size))
                                    .text(".")
                                    .end();
                            if (!codec.getCodecDescriptor().requiresDedicatedSection()) {
                                target.document(codec.getCodecDescriptor().details(bufferReference));
                            }
                        }
                    };
                }

                public String getTitle() {
                    return null;
                }

                public <C extends ParaContents<?>> Documenter<C> reference(
                        final Adjective adjective, final boolean startWithCapital) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target.text(adjective.asTextPreferA(startWithCapital)).text(
                                    "list of ").document(
                                    codec.getCodecDescriptor().reference(
                                            Adjective.NONE, false));
                        }
                    };
                }

                public boolean requiresDedicatedSection() {
                    return false;
                }

                public <C extends ParaContents<?>> Documenter<C> summary() {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target.document(reference(Adjective.A, true)).text(".");
                        }
                    };
                }

            };
        }

    }

    /**
     * Returns the {@link Expression} that will be evaluated to the {@link List}
     * 's size.
     *
     * @param listSettings
     *            The annotation, holding the expression.
     * @return An {@link Expression} instance, representing the expression.
     */
    private Expression<Integer, Resolver> getSizeExpression(
            BoundList listSettings, ResolverContext context)
            throws CodecConstructionException {
        return Expressions.createInteger(context, listSettings.size());
    }

    private static class DynamicListCodec<T> implements Codec<List<T>> {

        private Codec<T> codec;

        public DynamicListCodec(Codec<T> codec) {
            this.codec = codec;
        }

        public List<T> decode(BitBuffer buffer, Resolver resolver,
                              Builder builder) throws DecodingException {
            List<T> result = new LinkedList<T>();
            long mark = buffer.getBitPos();
            try {
                while (true) {
                    T value = codec.decode(buffer, resolver, builder);
                    result.add(value);
                    mark = buffer.getBitPos();
                }
            } catch (BitBufferUnderflowException oore) {
                // Trying to read beyond the end of the file.
                // TODO: Make a difference between failing half-way and failing
                // starting to read the next element.
            } catch (DecodingException de) {
                // So we can't decode the element. Maybe it's no longer an
                // element of this List. Let's consider this list to be
                // completed.
                buffer.setBitPos(mark);
            }
            return result;
        }

        public void encode(List<T> value, BitChannel channel, Resolver resolver) {
            throw new UnsupportedOperationException();
        }

        public Class<?>[] getTypes() {
            return codec.getTypes();
        }

        public Expression<Integer, Resolver> getSize() {
            return null;
        }

        public Class<?> getType() {
            return List.class;
        }

        public CodecDescriptor getCodecDescriptor() {
            return new CodecDescriptor() {

                public <C extends SimpleContents<?>> Documenter<C> details(
                        final String bufferReference) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target
                                    .para()
                                    .text(
                                            "The number of elements in the list is unknown at forehand. The codec will just decode as many elements as the buffer allows to decode.")
                                    .end();
                            if (!codec.getCodecDescriptor().requiresDedicatedSection()) {
                                target.document(codec.getCodecDescriptor().details(bufferReference));
                            }

                        }
                    };
                }

                public String getTitle() {
                    return null;
                }

                public <C extends ParaContents<?>> Documenter<C> reference(
                        final Adjective adjective, final boolean startWithCapital) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target.text(adjective.asTextPreferA(startWithCapital)).text(
                                    "list of ").document(
                                    codec.getCodecDescriptor().reference(
                                            Adjective.NONE, false));
                        }
                    };
                }

                public boolean requiresDedicatedSection() {
                    return false;
                }

                public <C extends ParaContents<?>> Documenter<C> summary() {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target.document(reference(Adjective.A, true)).text(".");
                        }
                    };
                }

            };
        }

    }

    /**
     * A {@link Codec} for Lists. The type of List that will be created is
     * determined at runtime, right before the actual List is decoded. The
     * {@link #skipListCodec} Codec will be used when the size of the individual
     * list item can be determined before the List is getting constructed.
     *
     */
    private static class SwitchingListCodec<T> implements Codec<List<T>> {

        private Codec<List<T>> skipListCodec;

        private Codec<List<T>> nonSkipListCodec;

        public SwitchingListCodec(Codec<List<T>> skipListCodec,
                                  Codec<List<T>> nonSkipListCodec) {
            this.skipListCodec = skipListCodec;
            this.nonSkipListCodec = nonSkipListCodec;
        }

        public List<T> decode(BitBuffer buffer, Resolver resolver,
                              Builder builder) throws DecodingException {
            Expression<Integer, Resolver> sizeExpr = skipListCodec.getSize();
            if (sizeExpr != null && sizeExpr.eval(resolver) >= 0) {
                return skipListCodec.decode(buffer, resolver, builder);
            } else {
                return nonSkipListCodec.decode(buffer, resolver, builder);
            }
        }

        public void encode(List<T> value, BitChannel channel, Resolver resolver) {
            throw new UnsupportedOperationException();
        }

        public int getSize(Resolver resolver) {
            int skipSize = skipListCodec.getSize().eval(resolver);
            if (skipSize >= 0) {
                return skipSize;
            } else {
                return nonSkipListCodec.getSize().eval(resolver);
            }
        }

        public Class<?>[] getTypes() {
            return skipListCodec.getTypes();
        }

        public Expression<Integer, Resolver> getSize() {
            return null;
        }

        public Class<?> getType() {
            return skipListCodec.getType();
        }

        public CodecDescriptor getCodecDescriptor() {
            // TODO Auto-generated method stub
            return new NullCodecDescriptor2();
        }

    }

    private static class IndexedResolverContext implements ResolverContext {

        private ResolverContext context;

        final public static String INDEX = "index";

        private CodecDescriptor descriptor;

        public IndexedResolverContext(ResolverContext context,
                                      CodecDescriptor descriptor) {
            this.context = context;
            this.descriptor = descriptor;
        }

        public Reference<Resolver> selectAttribute(String name) {
            if (INDEX.equals(name)) {
                return new IndexReference(context, descriptor);
            } else {
                return new ContextReplacingReference(this, context
                        .selectAttribute(name));
            }
        }

        public Reference<Resolver> selectItem(String index) {
            return new ContextReplacingReference(this, context
                    .selectItem(index));
        }

        public Reference<Resolver> selectItem(
                Expression<Integer, Resolver> index) {
            return new ContextReplacingReference(this, context
                    .selectItem(index));
        }

        public void document(Document target) {
            ParaContentsDocument doc = new ParaContentsDocument(target);
            doc.document(Documenters.forDescriptor(descriptor));
        }

        private static class IndexReference implements Reference<Resolver> {

            private ReferenceContext<Resolver> context;

            private CodecDescriptor descriptor;

            public IndexReference(ReferenceContext<Resolver> context,
                                  CodecDescriptor descriptor) {
                this.context = context;
                this.descriptor = descriptor;
            }

            public ReferenceContext<Resolver> getReferenceContext() {
                return context;
            }

            public boolean isAssignableTo(Class<?> type) {
                return Integer.class.isAssignableFrom(type);
            }

            public Object resolve(Resolver context) {
                return context.get(INDEX);
            }

            public Reference<Resolver> selectAttribute(String name) {
                throw new BindingException("No attribute selection allowed.");
            }

            public Reference<Resolver> selectItem(String index) {
                throw new BindingException("No item selection allowed.");
            }

            public Reference<Resolver> selectItem(
                    Expression<Integer, Resolver> index) {
                throw new BindingException("No item selection allowed.");
            }

            public void document(Document target) {
                target.text("the position of an element in ");
                ParaContentsDocument doc = new ParaContentsDocument(target);
                doc.document(Documenters.forDescriptor(descriptor));
            }

            public Class<?> getType() {
                return Integer.class;
            }

            public Reference<Resolver> narrow(Class<?> type) {
                if (type == Integer.class) {
                    return this;
                } else {
                    return null;
                }
            }

        }

    }

    private static class IndexResolver implements Resolver {

        private Resolver resolver;

        private int index;

        public IndexResolver(Resolver resolver) {
            this.resolver = resolver;
        }

        public Object get(String name) {
            if (IndexedResolverContext.INDEX.equals(name)) {
                return index;
            } else {
                return resolver.get(name);
            }
        }

        // public Resolver getOuter() {
        // // return resolver.getOuter();
        // }

        public void setIndex(int index) {
            this.index = index;
        }

        public Resolver getOriginalResolver() {
            return this;
        }

    }

    /**
     * A {@link Codec} for Lists, expecting the location of the elements to be
     * defined by some Limbo expression based on the index of the element.
     *
     * @param <T>
     */
    private static class OffsetListCodec<T> implements Codec<List<T>> {

        /**
         * The expression to calculate the offset. (Note that you can use the
         * 'index' variable to point to the position of this element in the
         * list.
         */
        private Expression<Integer, Resolver> offsets;

        /**
         * The size of the list.
         */
        private Expression<Integer, Resolver> size;

        /**
         * The Codec for decoding elements from the list.
         */
        private Codec<T> codec;

        /**
         * Constructs a new instance.
         *
         * @param offsets
         *            An expression for calculating the offset of the beginning
         *            of an element, as a function of the elements position in
         *            the list.
         * @param size
         *            An expression resolving into the size of the list.
         * @param codec
         *            The {@link Codec} to use for decoding individual list
         *            elements.
         */
        public OffsetListCodec(Expression<Integer, Resolver> offsets,
                               Expression<Integer, Resolver> size, Codec<T> codec) {
            this.offsets = offsets;
            this.size = size;
            this.codec = codec;
        }

        public List<T> decode(BitBuffer buffer, Resolver resolver,
                              Builder builder) throws DecodingException {
            int maxSize = size.eval(resolver);
            List<T> result = new ArrayList<T>(maxSize);
            long curPos = buffer.getBitPos();
            IndexResolver indexResolver = new IndexResolver(resolver);
            for (int i = 0; i < maxSize; i++) {
                indexResolver.setIndex(i);
                int offset = offsets.eval(indexResolver);
                if (i < maxSize - 1) {
                    indexResolver.setIndex(i + 1);
                    int nextOffset = offsets.eval(indexResolver); //- 1;
                    buffer.setBitPos(curPos + offset);
                    T value = codec.decode(new SlicedBitBuffer(buffer,
                            nextOffset - offset), resolver, builder);
                    result.add(value);
                } else {
                    buffer.setBitPos(curPos + offset);
                    result.add(codec.decode(buffer, resolver, builder));
                }
            }
            return result;
        }

        public void encode(List<T> value, BitChannel channel, Resolver resolver) {
            throw new UnsupportedOperationException();
        }

        public Class<?>[] getTypes() {
            return codec.getTypes();
        }

        public Expression<Integer, Resolver> getSize() {
            return size;
        }

        public Class<?> getType() {
            return List.class;
        }

        public CodecDescriptor getCodecDescriptor() {
            return new CodecDescriptor() {

                public <C extends SimpleContents<?>> Documenter<C> details(
                        final String bufferReference) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target
                                    .para()
                                    .text("The number of items in the list is ")
                                    .document(Documenters.forExpression(size))
                                    .text(
                                            ". The position of an item in the encoded representation is based on its index in the list.")
                                    .text(
                                            " Given an item's index, it's position is: ")
                                    .document(
                                            Documenters.forExpression(offsets))
                                    .text(".").end();
                            if (!codec.getCodecDescriptor().requiresDedicatedSection()) {
                                target.document(codec.getCodecDescriptor().details(bufferReference));
                            }

                        }
                    };
                }

                public String getTitle() {
                    return null;
                }

                public <C extends ParaContents<?>> Documenter<C> reference(
                        final Adjective adjective, final boolean startWithCapital) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target.text(adjective.asTextPreferA(startWithCapital)).text(
                                    "list of ").document(
                                    codec.getCodecDescriptor().reference(
                                            Adjective.NONE, false));
                        }
                    };
                }

                public boolean requiresDedicatedSection() {
                    return false;
                }

                public <C extends ParaContents<?>> Documenter<C> summary() {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target.document(reference(Adjective.A, true)).text(".");
                        }
                    };
                }

            };
        }
    }

}
