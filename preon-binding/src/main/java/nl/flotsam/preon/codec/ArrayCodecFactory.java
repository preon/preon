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

package nl.flotsam.preon.codec;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.util.List;

import nl.flotsam.limbo.BindingException;
import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Expressions;
import nl.flotsam.limbo.InvalidExpressionException;
import nl.flotsam.limbo.util.StringBuilderDocument;
import nl.flotsam.pecia.Contents;
import nl.flotsam.pecia.ParaContents;
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
import nl.flotsam.preon.buffer.BitBuffer;
import nl.flotsam.preon.util.AnnotationWrapper;

public class ArrayCodecFactory implements CodecFactory {

    /**
     * The {@link CodecFactory} that will be used for constructing the
     * {@link Codecs} to construct elements in the List.
     */
    private CodecFactory factory;

    /**
     * Constructs a new instance, accepting the {@link CodecFactory} creating
     * the {@link Codec Codecs} that will reconstruct elements in the List.
     * 
     * @param delegate
     *            The {@link CodecFactory} creating the {@link Codec Codecs}
     *            that will reconstruct elements in the List.
     */
    public ArrayCodecFactory(CodecFactory delegate) {
        this.factory = delegate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * nl.flotsam.preon.CodecFactory#create(java.lang.reflect.AnnotatedElement,
     * java.lang.Class, nl.flotsam.preon.ResolverContext)
     */
    @SuppressWarnings("unchecked")
    public <T> Codec<T> create(AnnotatedElement metadata, Class<T> type, ResolverContext context) {
        BoundList settings = null;
        if (metadata != null && (settings = metadata.getAnnotation(BoundList.class)) != null
                && type.isArray()) {
            Expression<Integer, Resolver> expr = getSizeExpression(settings, context);
            Codec<Object> elementCodec = null;
            if (type.getComponentType().isPrimitive()) {
                elementCodec = (Codec<Object>) factory.create(null, type.getComponentType(), null);
            } else {
                BoundObject objectSettings = getObjectSettings(settings);
                elementCodec = (Codec<Object>) factory.create(
                        new AnnotationWrapper(objectSettings), type.getComponentType(), null);
            }
            return (Codec<T>) new ArrayCodec(expr, elementCodec, type);
        } else {
            return null;
        }

    }

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

        };
    }

    /**
     * The {@link Codec} for reading the {@link List} and its members, on
     * demand. Instances of this class will <em>not</em> create a standard
     * {@link List} implementation and populate all of its data immediately.
     * Instead it will create a {@link EqualySizedLazyList}, constructing its
     * elements on the fly, only when it is required.
     * 
     */
    private static class ArrayCodec implements Codec<Object> {

        /**
         * The number of elements in the list.
         */
        private Expression<Integer, Resolver> size;

        /**
         * The {@link Codec} that will construct elements from the {@link List}.
         */
        private Codec<?> codec;

        /**
         * The type of element to be constructed.
         */
        private Class<?> type;

        /**
         * Constructs a new instance.
         * 
         * @param expr
         *            An {@link Expression} representing the number of elements
         *            in the {@link List}.
         * @param codec
         *            The {@link Codec} constructing elements in the
         *            {@link List}.
         */
        public ArrayCodec(Expression<Integer, Resolver> expr, Codec<?> codec, Class<?> type) {
            this.size = expr;
            this.codec = codec;
            this.type = type;
        }

        /*
         * (non-Javadoc)
         * 
         * @see nl.flotsam.preon.Codec#decode(nl.flotsam.preon.buffer.BitBuffer,
         * nl.flotsam.preon.Resolver, nl.flotsam.preon.Builder)
         */
        public Object decode(BitBuffer buffer, Resolver resolver, Builder builder)
                throws DecodingException {
            int length = size.eval(resolver).intValue();
            Object result = Array.newInstance(type.getComponentType(), length);
            for (int i = 0; i < length; i++) {
                Array.set(result, i, codec.decode(buffer, resolver, builder));
            }
            return result;
        }

        /*
         * (non-Javadoc)
         * 
         * @see nl.flotsam.preon.Codec#getSize(nl.flotsam.preon.Resolver)
         */
        public int getSize(Resolver resolver) {
            return size.eval(resolver) * codec.getSize(resolver);
        }

        /*
         * (non-Javadoc)
         * 
         * @see nl.flotsam.preon.Codec#getCodecDescriptor()
         */
        public CodecDescriptor getCodecDescriptor() {
            return new CodecDescriptor() {

                public String getLabel() {
                    return "a list of " + codec.getCodecDescriptor().getLabel() + " elements";
                }

                public boolean hasFullDescription() {
                    // TODO Auto-generated method stub
                    return false;
                }

                public <T> Contents<T> putFullDescription(Contents<T> contents) {
                    // TODO Auto-generated method stub
                    return null;
                }

                public <T, V extends ParaContents<T>> V putOneLiner(V para) {
                    writeReference(para);
                    return para;
                }

                public String getSize() {
                    StringBuilder builder = new StringBuilder();
                    size.document(new StringBuilderDocument(builder));
                    builder.append(" times " + codec.getCodecDescriptor().getSize());
                    return builder.toString();
                }

                public <T> void writeReference(ParaContents<T> contents) {
                    contents.text("a list of ");
                    codec.getCodecDescriptor().writeReference(contents);
                    contents.text(" elements");
                }

            };
        }

        /*
         * (non-Javadoc)
         * 
         * @see nl.flotsam.preon.Codec#getTypes()
         */
        public Class<?>[] getTypes() {
            return codec.getTypes();
        }

        /*
         * (non-Javadoc)
         * 
         * @see nl.flotsam.preon.Codec#getSize()
         */
        public Expression<Integer, Resolver> getSize() {
            return Expressions.multiply(size, codec.getSize());
        }

        public Class<?> getType() {
            return type;
        }

    }

    /**
     * Returns the {@link Expression} that will be evaluated to the array's
     * size.
     * 
     * @param listSettings
     *            The annotation, holding the expression.
     * @return An {@link Expression} instance, representing the expression.
     */
    private Expression<Integer, Resolver> getSizeExpression(BoundList listSettings,
            ResolverContext context) throws CodecConstructionException {
        try {
            return Expressions.createInteger(context, listSettings.size());
        } catch (InvalidExpressionException ece) {
            throw new CodecConstructionException(ece);
        } catch (BindingException be) {
            throw new CodecConstructionException(be);
        }
    }
}
