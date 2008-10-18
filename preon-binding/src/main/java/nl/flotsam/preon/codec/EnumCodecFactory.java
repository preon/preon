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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Expressions;
import nl.flotsam.limbo.util.StringBuilderDocument;
import nl.flotsam.pecia.Contents;
import nl.flotsam.pecia.ParaContents;
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


/**
 * A {@link CodecFactory} creating {@link Codec Codecs} capable of decoding enum
 * values. At this state, it will be triggered by enum type of fields with a
 * {@link BoundNumber} annotation, to pass metadata on the number of bits (and
 * endianness) from which the enum's value needs to get constructed.
 * 
 * @author Wilfred Springer
 */
public class EnumCodecFactory implements CodecFactory {

    public <T> Codec<T> create(AnnotatedElement metadata, Class<T> type, ResolverContext context) {
        Map<Integer, T> mapping = new HashMap<Integer, T>();
        if (type.isEnum() && metadata.isAnnotationPresent(BoundNumber.class)) {
            T[] values = type.getEnumConstants();
            for (int i = 0; i < values.length; i++) {
                mapping.put(i, values[i]);
            }
            BoundNumber settings = metadata.getAnnotation(BoundNumber.class);
            Expression<Integer, Resolver> sizeExpr = Expressions.createInteger(context, settings
                    .size());
            return new EnumCodec<T>(type, mapping, sizeExpr, settings.endian());

        } else {
            return null;
        }
    }

    private static class EnumCodec<T> implements Codec<T> {

        private Class<T> type;
        private Map<Integer, T> mapping;
        private Expression<Integer, Resolver> size;
        private ByteOrder endian;

        public EnumCodec(Class<T> type, Map<Integer, T> mapping,
                Expression<Integer, Resolver> sizeExpr, ByteOrder endian) {
            this.type = type;
            this.mapping = mapping;
            this.size = sizeExpr;
            this.endian = endian;
        }

        public T decode(BitBuffer buffer, Resolver resolver, Builder builder)
                throws DecodingException {
            int value = buffer.readAsInt(size.eval(resolver), endian);
            return mapping.get(value);
        }

        public CodecDescriptor getCodecDescriptor() {
            return new CodecDescriptor() {

                public String getLabel() {
                    StringBuilder builder = new StringBuilder();
                    builder.append(size);
                    builder.append(" bits, evaluating to either ");
                    List<Integer> keys = new ArrayList<Integer>(mapping.keySet());
                    for (int i = 0; i < keys.size(); i++) {
                        if (i != 0) {
                            if (i != keys.size() - 1) {
                                builder.append(", ");
                            } else {
                                builder.append(" or ");
                            }
                        }
                        builder.append(keys.get(i));
                        builder.append(" (");
                        builder.append(mapping.get(keys.get(i)).toString());
                        builder.append(")");
                    }
                    return builder.toString();
                }

                public String getSize() {
                    StringBuilder builder = new StringBuilder();
                    size.document(new StringBuilderDocument(builder));
                    return builder.toString();
                }

                public boolean hasFullDescription() {
                    return false;
                }

                public <U> Contents<U> putFullDescription(Contents<U> contents) {
                    return contents;
                }

                public <U, V extends ParaContents<U>> V putOneLiner(V para) {
                    para.text(getLabel());
                    return para;
                }

                public <U> void writeReference(ParaContents<U> contents) {
                    contents.text(getLabel());
                }

            };
        }

        public int getSize(Resolver resolver) {
            return size.eval(resolver);
        }

        public Class<?>[] getTypes() {
            return new Class[] { type };
        }

        public Expression<Integer, Resolver> getSize() {
            return size;
        }

    }

}
