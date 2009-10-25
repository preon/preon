/**
 * Copyright (C) 2009 Wilfred Springer
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

import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Expressions;
import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import nl.flotsam.preon.*;
import nl.flotsam.preon.annotation.LengthPrefix;
import nl.flotsam.preon.annotation.Slice;
import nl.flotsam.preon.buffer.BitBuffer;
import nl.flotsam.preon.buffer.ByteOrder;
import nl.flotsam.preon.channel.BitChannel;
import nl.flotsam.preon.descriptor.Documenters;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * A {@link CodecFactory} creating {@link Codec Codecs} slicing the
 * {@link BitBuffer} to limit the visibility of the remainder of the buffer (and
 * easily skip forward, if the data itself is not required). Triggered by the
 * {@link LengthPrefix} annotation.
 * 
 * @author Wilfred Springer
 * 
 */
public class SlicingCodecDecorator implements CodecDecorator {

    public <T> Codec<T> decorate(Codec<T> decorated, AnnotatedElement metadata,
            Class<T> type, ResolverContext context) {
        LengthPrefix prefix = getAnnotation(metadata, type, LengthPrefix.class);
        if (prefix != null) {
            return createCodecFromLengthPrefix(decorated, prefix, context);
        }
        Slice slice = getAnnotation(metadata, type, Slice.class);
        if (slice != null) {
            return createCodecFromSlice(decorated, slice, context);
        }
        return decorated;
    }

    private <T, V extends Annotation> V getAnnotation(
            AnnotatedElement metadata, Class<T> type, Class<V> annotation) {
        if (type.isAnnotationPresent(annotation)) {
            return type.getAnnotation(annotation);
        }
        if (metadata != null && metadata.isAnnotationPresent(annotation)) {
            return metadata.getAnnotation(annotation);
        }
        return null;
    }

    private <T> Codec<T> createCodecFromLengthPrefix(Codec<T> decorated,
            LengthPrefix prefix, ResolverContext context) {
        Expression<Integer, Resolver> sizeExpr;
        sizeExpr = Expressions.createInteger(context, prefix.size());
        return new SlicingCodec<T>(decorated, new PrefixSizeCalculator(
                sizeExpr, prefix.endian()));
    }

    private <T> Codec<T> createCodecFromSlice(Codec<T> decorated, Slice slice,
            ResolverContext context) {
        Expression<Integer, Resolver> sizeExpr;
        sizeExpr = Expressions.createInteger(context, slice.size());
        return new SlicingCodec<T>(decorated, new FixedSizeCalculator(sizeExpr));
    }

    private static class FixedSizeCalculator implements SizeCalculator {

        private Expression<Integer, Resolver> sizeExpr;

        public FixedSizeCalculator(Expression<Integer, Resolver> sizeExpr) {
            this.sizeExpr = sizeExpr;
        }

        public long getSize(BitBuffer buffer, Resolver resolver) {
            return getSize(resolver);
        }

        public long getSize(Resolver resolver) {
            return sizeExpr.eval(resolver);
        }

        public Expression<Integer, Resolver> getSize() {
            return sizeExpr;
        }

    }

    private static class PrefixSizeCalculator implements SizeCalculator {

        private Expression<Integer, Resolver> sizeExpr;

        private ByteOrder endian;

        public PrefixSizeCalculator(Expression<Integer, Resolver> sizeExpr,
                ByteOrder endian) {
            this.sizeExpr = sizeExpr;
            this.endian = endian;
        }

        public long getSize(BitBuffer buffer, Resolver resolver) {
            int size = sizeExpr.eval(resolver);
            return buffer.readAsLong(size, endian);
        }

        public long getSize(Resolver resolver) {
            return -1;
        }

        public Expression<Integer, Resolver> getSize() {
            return sizeExpr;
        }

    }

    private static class SlicingCodec<T> implements Codec<T> {

        private SizeCalculator calculator;

        private Codec<T> wrapped;

        public SlicingCodec(Codec<T> wrapped, SizeCalculator calculator) {
            this.calculator = calculator;
            this.wrapped = wrapped;
        }

        public T decode(BitBuffer buffer, Resolver resolver, Builder builder)
                throws DecodingException {
            BitBuffer slice = buffer
                    .slice(calculator.getSize(buffer, resolver));
            return wrapped.decode(slice, resolver, builder);
        }

        public void encode(T value, BitChannel channel, Resolver resolver) {
            throw new UnsupportedOperationException();
        }

        public Class<?>[] getTypes() {
            return wrapped.getTypes();
        }

        public Expression<Integer, Resolver> getSize() {
            return calculator.getSize();
        }

        public Class<?> getType() {
            return wrapped.getType();
        }

        public CodecDescriptor getCodecDescriptor() {
            return new CodecDescriptor() {

                public <C extends SimpleContents<?>> Documenter<C> details(
                        final String bufferReference) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target.para().text("The format reserves only ")
                                    .document(
                                            Documenters
                                                    .forExpression(calculator
                                                            .getSize())).text(
                                            " bits for ").document(
                                            wrapped.getCodecDescriptor()
                                                    .reference(Adjective.THE, false))
                                    .end();
                            target.document(wrapped.getCodecDescriptor()
                                    .details(bufferReference));
                        }
                    };
                }

                public String getTitle() {
                    return null;
                }

                public <C extends ParaContents<?>> Documenter<C> reference(
                        Adjective adjective, boolean startWithCapital) {
                    return wrapped.getCodecDescriptor().reference(adjective, false);
                }

                public boolean requiresDedicatedSection() {
                    return false;
                }

                public <C extends ParaContents<?>> Documenter<C> summary() {
                    return wrapped.getCodecDescriptor().summary();
                }

            };
        }
    }

    /**
     * The interface implemented by objects that configure the
     * {@link SlicingCodec} with a policy to determine the size of the slice.
     * 
     */
    private interface SizeCalculator {

        /**
         * Returns the size of the slice, without information in the BitBuffer
         * itself.
         * 
         * @param resolver
         *            The {@link Resolver}, used to evaluate size expressions.
         * @return The size of the slice in bits, or <code>-1</code> if we can't
         *         calculate that value.
         */
        long getSize(Resolver resolver);

        /**
         * Returns the size of the slice.
         * 
         * @param buffer
         *            The BitBuffer, if some information needs to be read from the
         *            buffer in order to calculate the size.
         * @return The size of the slice.
         */
        long getSize(BitBuffer buffer, Resolver resolver);

        /**
         * Returns an {@link Expression} reflecting the size, as a function of
         * parameters passed in through the resolver.
         * 
         * @return The {@link Expression} reflecting the size, as a function of
         *         parameters passed in through the resolver.
         */
        Expression<Integer, Resolver> getSize();

    }

}
