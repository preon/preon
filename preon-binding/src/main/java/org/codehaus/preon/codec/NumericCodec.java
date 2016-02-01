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
package org.codehaus.preon.codec;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.preon.el.Document;
import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Expressions;
import org.codehaus.preon.el.util.Converters;
import org.codehaus.preon.el.util.StringBuilderDocument;
import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import org.codehaus.preon.Builder;
import org.codehaus.preon.Codec;
import org.codehaus.preon.CodecDescriptor;
import org.codehaus.preon.CodecFactory;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.ResolverContext;
import org.codehaus.preon.annotation.Bound;
import org.codehaus.preon.annotation.BoundNumber;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.buffer.ByteOrder;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.descriptor.Documenters;
import org.codehaus.preon.descriptor.NullDocumenter;

/** The {@link org.codehaus.preon.Codec} capable of decoding numeric types in a sensible way. */
public class NumericCodec implements Codec<Object> {

    static Map<Class<?>, NumericType> NUMERIC_TYPES = new HashMap<Class<?>, NumericType>(
            8);

    static {
        NumericCodec.NUMERIC_TYPES.put(Integer.class, NumericCodec.NumericType.Integer);
        NumericCodec.NUMERIC_TYPES.put(Long.class, NumericCodec.NumericType.Long);
        NumericCodec.NUMERIC_TYPES.put(Short.class, NumericCodec.NumericType.Short);
        NumericCodec.NUMERIC_TYPES.put(Byte.class, NumericCodec.NumericType.Byte);
        NumericCodec.NUMERIC_TYPES.put(int.class, NumericCodec.NumericType.Integer);
        NumericCodec.NUMERIC_TYPES.put(long.class, NumericCodec.NumericType.Long);
        NumericCodec.NUMERIC_TYPES.put(short.class, NumericCodec.NumericType.Short);
        NumericCodec.NUMERIC_TYPES.put(byte.class, NumericCodec.NumericType.Byte);
        NumericCodec.NUMERIC_TYPES.put(float.class, NumericCodec.NumericType.Float);
        NumericCodec.NUMERIC_TYPES.put(Float.class, NumericCodec.NumericType.Float);
        NumericCodec.NUMERIC_TYPES.put(double.class, NumericCodec.NumericType.Double);
        NumericCodec.NUMERIC_TYPES.put(Double.class, NumericCodec.NumericType.Double);
    }

    protected Expression<Integer, Resolver> sizeExpr;

    protected ByteOrder byteOrder;

    protected NumericType type;

    private Expression<Integer, Resolver> matchExpr;

    public NumericCodec(Expression<Integer, Resolver> sizeExpr,
                        ByteOrder byteOrder, NumericType type,
                        Expression<Integer, Resolver> matchExpr) {
        this.sizeExpr = sizeExpr;
        this.byteOrder = byteOrder;
        this.type = type;
        this.matchExpr = matchExpr;
    }

    public Object decode(BitBuffer buffer, Resolver resolver,
                         Builder builder) throws DecodingException {
        int size = ((Number) (this.sizeExpr.eval(resolver))).intValue();
        Object result = type.decode(buffer, size, byteOrder);
        if (matchExpr != null) {
            if (!matchExpr.eval(resolver).equals(Converters.toInt(result))) {
                StringBuilder stringBuilder = new StringBuilder();
                Document document = new StringBuilderDocument(stringBuilder);
                if (matchExpr.isParameterized()) {
                    stringBuilder.append("Expected different value than "
                            + result);
                } else {
                    stringBuilder.append("Expected ");
                    matchExpr.document(document);
                    stringBuilder.append(" but got ");
                    stringBuilder.append(result);
                }
                throw new DecodingException(stringBuilder.toString());
            }
        }
        return result;
    }

    public void encode(Object value, BitChannel channel, Resolver resolver) throws IOException {
        type.encode(channel, sizeExpr.eval(resolver), byteOrder, value);
    }

    public Class<?>[] getTypes() {
        return new Class[]{type.getType()};
    }

    public Expression<Integer, Resolver> getSize() {
        return sizeExpr;
    }

    public Class<?> getType() {
        return type.getType();
    }

    public CodecDescriptor getCodecDescriptor() {
        return new CodecDescriptor() {

            public <C extends SimpleContents<?>> Documenter<C> details(
                    String bufferReference) {
                if (sizeExpr.isParameterized()) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target
                                    .para()
                                    .text("The number of bits is ")
                                    .document(
                                            Documenters
                                                    .forExpression(sizeExpr))
                                    .text(".").end();
                        }
                    };
                } else {
                    return new NullDocumenter<C>();
                }
            }

            public String getTitle() {
                return null;
            }

            public <C extends ParaContents<?>> Documenter<C> reference(
                    final Adjective adjective, final boolean startWithCapital) {
                return new Documenter<C>() {
                    public void document(C target) {
                        if (sizeExpr.isParameterized()) {
                            target.text(adjective.asTextPreferAn(startWithCapital)).text(
                                    " integer value (").document(
                                    Documenters.forByteOrder(byteOrder)).text(
                                    ")");
                        } else {
                            target
                                    .text(adjective.asTextPreferA(startWithCapital))
                                    .text(" ")
                                    .document(
                                            Documenters
                                                    .forExpression(sizeExpr))
                                    .text("-bit integer value (").document(
                                    Documenters
                                            .forByteOrder(byteOrder))
                                    .text(")");
                        }
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

    public String toString() {
        return "Codec of " + byteOrder + " " + type;
    }

    public enum NumericType {

        Float {

            public int getDefaultSize() {
                return 32;
            }

            public Float decode(BitBuffer buffer, int size, ByteOrder endian) {
                int value = buffer.readAsInt(size, endian);
                return java.lang.Float.intBitsToFloat(value);
            }

            public void encode(BitChannel channel, int size, ByteOrder endian, Object value) {
                throw new UnsupportedOperationException("Encoding not supported for floats.");
            }

            public Class<?> getType() {
                return Float.class;
            }

        },

        Double {

            public int getDefaultSize() {
                return 64;
            }

            public Double decode(BitBuffer buffer, int size, ByteOrder endian) {
                return java.lang.Double.longBitsToDouble(buffer.readAsLong(
                        size, endian));
            }

            public void encode(BitChannel channel, int size, ByteOrder endian, Object value) {
                throw new UnsupportedOperationException("Encoding not supported for doubles.");
            }

            public Class<?> getType() {
                return Double.class;
            }

        },

        Integer {
            public int getDefaultSize() {
                return 32;
            }

            public Integer decode(BitBuffer buffer, int size, ByteOrder endian) {
                return buffer.readAsInt(size, endian);
            }

            public void encode(BitChannel channel, int size, ByteOrder endian, Object value) throws IOException {
                channel.write(size, (Integer) value, endian);
            }

            public Class<?> getType() {
                return Integer.class;
            }
        },

        Long {
            public int getDefaultSize() {
                return 64;
            }

            public Long decode(BitBuffer buffer, int size, ByteOrder endian) {
                return buffer.readAsLong(size, endian);
            }

            public void encode(BitChannel channel, int size, ByteOrder endian, Object value) throws IOException {
                channel.write(size, (Long) value, endian);
            }

            public Class<?> getType() {
                return Long.class;
            }
        },

        Short {
            public int getDefaultSize() {
                return 16;
            }

            public Short decode(BitBuffer buffer, int size, ByteOrder endian) {
                return buffer.readAsShort(size, endian);
            }

            public void encode(BitChannel channel, int size, ByteOrder endian, Object value) throws IOException {
                channel.write(size, (Short) value, endian);
            }

            public Class<?> getType() {
                return Short.class;
            }
        },

        Byte {
            public int getDefaultSize() {
                return 8;
            }

            public Byte decode(BitBuffer buffer, int size, ByteOrder endian) {
                return buffer.readAsByte(size, endian);
            }

            public void encode(BitChannel channel, int size, ByteOrder endian, Object value) throws IOException {
                channel.write(size, (Byte) value);
            }

            public Class<?> getType() {
                return Byte.class;
            }
        };

        public abstract int getDefaultSize();

        public abstract Object decode(BitBuffer buffer, int size,
                                      ByteOrder endian);

        public abstract void encode(BitChannel channel, int size, ByteOrder endian, Object value) throws IOException;

        public abstract Class<?> getType();

    }

    /**
     * A {@link org.codehaus.preon.CodecFactory} generating {@link org.codehaus.preon.Codec Codecs} capable of decoding
     * numbers from the {@link org.codehaus.preon.buffer.BitBuffer}. Note that the {@link org.codehaus.preon.Codec Codecs}
     * created by this class are capable to decode Longs, Integers, Shorts, Bytes, longs, ints, shorts and bytes.
     *
     * @author Wilfred Springer
     */
    public static class Factory implements CodecFactory {

        @SuppressWarnings({"unchecked"})
        public <T> Codec<T> create(AnnotatedElement overrides, Class<T> type,
                                   ResolverContext context) {

            Class<?> actualType = resolveActualType(overrides, type);
            if (NUMERIC_TYPES.keySet().contains(actualType)) {
                NumericType numericType = NUMERIC_TYPES.get(actualType);
                if (overrides == null || overrides.isAnnotationPresent(Bound.class)) {
                    ByteOrder endian = ByteOrder.LittleEndian;
                    int size = numericType.getDefaultSize();
                    Expression<Integer, Resolver> sizeExpr = Expressions
                            .createInteger(context, Integer.toString(size));
                    return (Codec<T>) new NumericCodec(sizeExpr, endian,
                            numericType, null);
                }
                if (overrides != null
                        && overrides.isAnnotationPresent(BoundNumber.class)) {
                    BoundNumber numericMetadata = overrides
                            .getAnnotation(BoundNumber.class);
                    ByteOrder endian = numericMetadata.byteOrder();
                    String size = numericMetadata.size();
//                    if(NUMERIC_TYPES.containsKey(numericMetadata.type())) {
//                    	numericType = NUMERIC_TYPES.get(numericMetadata.type());
//                    }

                    if (size.length() == 0) {
                        size = Integer.toString(numericType.getDefaultSize());
                    }
                    Expression<Integer, Resolver> sizeExpr = Expressions
                            .createInteger(context, size);
                    Expression<Integer, Resolver> matchExpr = null;
                    if (numericMetadata.match().trim().length() != 0) {
                        matchExpr = Expressions.createInteger(context,
                                numericMetadata.match());
                    }
                    return (Codec<T>) new NumericCodec(sizeExpr, endian,
                            numericType, matchExpr);
                }
            }
            return null;
        }

        public Class<?> resolveActualType(AnnotatedElement overrides, Class<?> type) {
            if (overrides != null && overrides.isAnnotationPresent(BoundNumber.class)) {
                BoundNumber numericMetadata = overrides.getAnnotation(BoundNumber.class);
                Class<?> typeOverride = numericMetadata.type();
                if (typeOverride != Number.class) {
                    return typeOverride;
                }
            }
            return type;
        }

    }
}
