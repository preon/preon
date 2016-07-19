/**
 * Copyright (c) 2009-2016 Wilfred Springer
 * <p>
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * <p>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * <p>
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

import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import org.codehaus.preon.*;
import org.codehaus.preon.annotation.*;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.buffer.ByteOrder;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.descriptor.Documenters;
import org.codehaus.preon.descriptor.NullDocumenter;
import org.codehaus.preon.el.Document;
import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Expressions;
import org.codehaus.preon.el.util.Converters;
import org.codehaus.preon.el.util.StringBuilderDocument;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@link org.codehaus.preon.Codec} capable of decoding numeric types in a sensible way.
 */
public class NumericCodec implements Codec<Object> {

    static Map<Class<?>, INumericType> NUMERIC_TYPES = new HashMap<Class<?>, INumericType>(8);

    static Map<Class<?>, INumericType> UNSIGNED_TYPES = new HashMap<Class<?>, INumericType>(8);

    static {
        for (INumericType numType : NumericType.values()) {
            NUMERIC_TYPES.put(numType.getType(), numType);
            NUMERIC_TYPES.put(numType.getPrimitiveType(), numType);
        }

        for (INumericType numType : NumericUnsignedType.values()) {
            UNSIGNED_TYPES.put(numType.getType(), numType);
            UNSIGNED_TYPES.put(numType.getPrimitiveType(), numType);
        }
    }

    protected Expression<Integer, Resolver> sizeExpr;

    protected ByteOrder byteOrder;

    protected INumericType type;

    protected boolean unsigned;

    private Expression<Integer, Resolver> matchExpr;

    public NumericCodec(Expression<Integer, Resolver> sizeExpr, ByteOrder byteOrder, INumericType type,
                        Expression<Integer, Resolver> matchExpr, boolean unsigned) {
        this.sizeExpr = sizeExpr;
        this.byteOrder = byteOrder;
        this.type = type;
        this.matchExpr = matchExpr;
        this.unsigned = unsigned;
    }

    public Object decode(BitBuffer buffer, Resolver resolver, Builder builder) throws DecodingException {
        int size = ((Number) (this.sizeExpr.eval(resolver))).intValue();
        Object result = type.decode(buffer, size, byteOrder);
        if (matchExpr != null) {
            if (!matchExpr.eval(resolver).equals(Converters.toInt(result))) {
                StringBuilder stringBuilder = new StringBuilder();
                Document document = new StringBuilderDocument(stringBuilder);
                if (matchExpr.isParameterized()) {
                    stringBuilder.append("Expected different value than " + result);
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

            public <C extends SimpleContents<?>> Documenter<C> details(String bufferReference) {
                if (sizeExpr.isParameterized()) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target.para().text("The number of bits is ").document(Documenters.forExpression(sizeExpr)
                            ).text(".").end();
                        }
                    };
                } else {
                    return new NullDocumenter<C>();
                }
            }

            public String getTitle() {
                return null;
            }

            public <C extends ParaContents<?>> Documenter<C> reference(final Adjective adjective, final boolean
                    startWithCapital) {
                return new Documenter<C>() {
                    public void document(C target) {
                        String unsignedDesc = "";

                        if (unsigned)
                            unsignedDesc = "unsigned, ";

                        if (sizeExpr.isParameterized()) {
                            target.text(adjective.asTextPreferAn(startWithCapital)).text(unsignedDesc).text(" integer" +
                                    " value (").text(unsignedDesc).document(Documenters.forByteOrder(byteOrder)).text
                                    (")");
                        } else {
                            target.text(adjective.asTextPreferA(startWithCapital)).text(" ").document(Documenters
                                    .forExpression(sizeExpr)).text("-bit integer value (").text(unsignedDesc)
                                    .document(Documenters.forByteOrder(byteOrder)).text(")");
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

    /**
     * A {@link org.codehaus.preon.CodecFactory} generating {@link org.codehaus.preon.Codec Codecs} capable of decoding
     * numbers from the {@link org.codehaus.preon.buffer.BitBuffer}. Note that the
     * {@link org.codehaus.preon.Codec Codecs}
     * created by this class are capable to decode Longs, Integers, Shorts, Bytes, longs, ints, shorts and bytes.
     *
     * @author Wilfred Springer
     */
    public static class Factory implements CodecFactory {

        @SuppressWarnings({"unchecked"})
        public <T> Codec<T> create(AnnotatedElement overrides, Class<T> type, ResolverContext context) {

            boolean unsigned = isUnsigned(overrides, type);
            Class<?> actualType = resolveActualType(overrides, type);
            INumericType numericType = resolveNumericType(overrides, actualType, unsigned);

            if (numericType != null) {
                if (overrides == null || overrides.isAnnotationPresent(Bound.class)) {
                    ByteOrder endian = ByteOrder.LittleEndian;
                    int size = numericType.getDefaultSize();
                    Expression<Integer, Resolver> sizeExpr = Expressions.createInteger(context, Integer.toString(size));
                    return (Codec<T>) new NumericCodec(sizeExpr, endian, numericType, null, unsigned);
                }
                if (overrides != null && overrides.isAnnotationPresent(BoundNumber.class)) {
                    BoundNumber numericMetadata = overrides.getAnnotation(BoundNumber.class);
                    ByteOrder endian = numericMetadata.byteOrder();
                    String size = numericMetadata.size();
//                    if(NUMERIC_TYPES.containsKey(numericMetadata.type())) {
//                    	numericType = NUMERIC_TYPES.get(numericMetadata.type());
//                    }

                    if (size.length() == 0) {
                        size = Integer.toString(numericType.getDefaultSize());
                    }
                    Expression<Integer, Resolver> sizeExpr = Expressions.createInteger(context, size);
                    Expression<Integer, Resolver> matchExpr = null;
                    if (numericMetadata.match().trim().length() != 0) {
                        matchExpr = Expressions.createInteger(context, numericMetadata.match());
                    }
                    return (Codec<T>) new NumericCodec(sizeExpr, endian, numericType, matchExpr, unsigned);
                }
                if (overrides != null && (overrides.isAnnotationPresent(BEUnsigned.class) || overrides
                        .isAnnotationPresent(BESigned.class))) {
                    ByteOrder endian = ByteOrder.BigEndian;
                    int size = numericType.getDefaultSize();
                    Expression<Integer, Resolver> sizeExpr = Expressions.createInteger(context, Integer.toString(size));
                    return (Codec<T>) new NumericCodec(sizeExpr, endian, numericType, null, unsigned);
                }
            }
            return null;
        }

        private INumericType resolveNumericType(AnnotatedElement overrides, Class<?> actualType, boolean isUnsigned) {
            if (isUnsigned)
                return UNSIGNED_TYPES.get(actualType);
            return NUMERIC_TYPES.get(actualType);

        }

        public boolean isUnsigned(AnnotatedElement overrides, Class<?> type) {
            if (overrides != null && overrides.isAnnotationPresent(BoundNumber.class)) {
                BoundNumber numericMetadata = overrides.getAnnotation(BoundNumber.class);
                return numericMetadata.unsinged();
            }
            if (overrides != null && (overrides.isAnnotationPresent(BEUnsigned.class) || overrides
                    .isAnnotationPresent(LEUnsigned.class)))
                return true;
            return false;
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
