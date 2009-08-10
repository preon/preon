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

import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Expressions;
import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ItemizedList;
import nl.flotsam.pecia.Para;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import nl.flotsam.preon.Builder;
import nl.flotsam.preon.Codec;
import nl.flotsam.preon.CodecDescriptor;
import nl.flotsam.preon.CodecFactory;
import nl.flotsam.preon.DecodingException;
import nl.flotsam.preon.Resolver;
import nl.flotsam.preon.ResolverContext;
import nl.flotsam.preon.annotation.BoundNumber;
import nl.flotsam.preon.buffer.BitBuffer;
import nl.flotsam.preon.buffer.ByteOrder;
import nl.flotsam.preon.descriptor.Documenters;
import nl.flotsam.preon.util.EnumUtils;

/**
 * A {@link CodecFactory} creating {@link Codec Codecs} capable of decoding enum
 * values. At this state, it will be triggered by enum type of fields with a
 * {@link BoundNumber} annotation, to pass metadata on the number of bits (and
 * endianness) from which the enum's value needs to get constructed.
 * 
 * @author Wilfred Springer
 */
public class EnumCodecFactory implements CodecFactory {

    public <T> Codec<T> create(AnnotatedElement metadata, Class<T> type,
            ResolverContext context) {
        if (type.isEnum() && metadata.isAnnotationPresent(BoundNumber.class)) {
            Map<Long, T> mapping = EnumUtils.getBoundEnumOptionIndex(type);
            BoundNumber settings = metadata.getAnnotation(BoundNumber.class);
            Expression<Integer, Resolver> sizeExpr = Expressions.createInteger(
                    context, settings.size());
            return new EnumCodec<T>(type, mapping, sizeExpr, settings
                    .byteOrder());

        } else {
            return null;
        }
    }

    
    private static class EnumCodec<T> implements Codec<T> {

        private Class<T> type;
        private Map<Long, T> mapping;
        private Expression<Integer, Resolver> size;
        private ByteOrder byteOrder;

        public EnumCodec(Class<T> type, Map<Long, T> mapping,
                Expression<Integer, Resolver> sizeExpr, ByteOrder endian) {
            this.type = type;
            this.mapping = mapping;
            this.size = sizeExpr;
            this.byteOrder = endian;
        }

        public T decode(BitBuffer buffer, Resolver resolver, Builder builder)
                throws DecodingException {
            long value = buffer.readAsLong(size.eval(resolver), byteOrder);
            T result = mapping.get(value);
            if (result == null) {
            	result = mapping.get(null);
            }
            return result;
        }

        public Class<?>[] getTypes() {
            return new Class[] { type };
        }

        public Expression<Integer, Resolver> getSize() {
            return size;
        }

        public Class<?> getType() {
            return type;
        }

        public CodecDescriptor getCodecDescriptor() {
            return new CodecDescriptor() {

                public <C extends SimpleContents<?>> Documenter<C> details(
                        String bufferReference) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            Para<?> para = target.para();
                            if (!size.isParameterized()) {
                                para.text("The symbol is represented as a ")
                                        .document(
                                                Documenters.forNumericValue(
                                                        size.eval(null),
                                                        byteOrder)).text(".");
                            } else {
                                para
                                        .text(
                                                "The symbol is represented as a numeric value (")
                                        .document(
                                                Documenters
                                                        .forByteOrder(byteOrder))
                                        .text(". The number of bits is ")
                                        .document(
                                                Documenters.forExpression(size))
                                        .text(".");
                            }
                            para
                                    .text(
                                            " The numeric value corresponds to the following symbols:")
                                    .end();
                            ItemizedList<?> itemizedList = target
                                    .itemizedList();
                            for (Entry<Long, T> entry : mapping.entrySet()) {
                            	if (entry.getKey() != null) {
                            	itemizedList
                            		.item()
                            			.para()
                            				.text(Long.toString(entry.getKey()))
                            				.text(": ")
                            				.text(entry.getValue().toString())
                            			.end()
                            		.end();
                            	}
                            }
                            itemizedList.end();
                            T defaultValue = mapping.get(null);
                            if (defaultValue != null) {
                            	target.para("The default value is " + defaultValue.toString() + ".");
                            }
                        }
                    };
                }

                public String getTitle() {
                    return null;
                }

                public <C extends ParaContents<?>> Documenter<C> reference(
                        final Adjective adjective, boolean startWithCapital) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target.text(adjective.asTextPreferAn(false)).text(
                                    "index of an enumeration");
                        }
                    };
                }

                public boolean requiresDedicatedSection() {
                    return false;
                }

                public <C extends ParaContents<?>> Documenter<C> summary() {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target
                                    .text("A value from a set of symbols, represented by a numeric value.");
                        }
                    };
                }

            };
        }
    }

}
