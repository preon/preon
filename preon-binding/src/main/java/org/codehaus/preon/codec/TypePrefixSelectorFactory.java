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
package org.codehaus.preon.codec;

import org.codehaus.preon.el.Document;
import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Expressions;
import nl.flotsam.pecia.ParaContents;
import org.codehaus.preon.*;
import org.codehaus.preon.CodecDescriptor.Adjective;
import org.codehaus.preon.annotation.TypePrefix;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.buffer.ByteOrder;
import org.codehaus.preon.channel.BitChannel;

import java.io.IOException;
import java.util.*;

/**
 * A {@link CodecSelectorFactory} that will create a {@link CodecSelector} that will look for leading bits, matching a
 * certain value expressed with an {@link TypePrefix} annotation on the Codecs classes.
 *
 * @author Wilfred Springer
 */
public class TypePrefixSelectorFactory implements CodecSelectorFactory {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.codehaus.preon.CodecSelectorFactory#create(org.codehaus.preon.ResolverContext
     * , java.util.List)
     */

    public CodecSelector create(ResolverContext context,
                                List<Codec<?>> allCodecs) {
        int size = -1;
        List<Expression<Integer, Resolver>> expressions = new ArrayList<Expression<Integer, Resolver>>();
        List<Codec<?>> codecs = new ArrayList<Codec<?>>();
        ByteOrder byteOrder = null;
        for (Codec<?> codec : allCodecs) {
            for (Class<?> valueType : codec.getTypes()) {
                TypePrefix prefix = (TypePrefix) valueType
                        .getAnnotation(TypePrefix.class);
                if (prefix == null) {
                    throw new CodecConstructionException(
                            "To little context to decide between codecs.");
                } else {
                    if (byteOrder == null) {
                        byteOrder = prefix.byteOrder();
                    } else {
                        if (byteOrder != prefix.byteOrder()) {
                            throw new CodecConstructionException("Two distinct types of byte orders are not supported: "
                            + "expected " + byteOrder.asText() + ", got "
                            + prefix.byteOrder().asText() + " for " + codec);
                        }
                    }
                    if (size != -1) {
                        if (size != prefix.size()) {
                            throw new CodecConstructionException(
                                    "Two distinct prefix sizes are not supported: "
                                            + "expected " + size + ", got "
                                            + prefix.size() + " for " + codec);
                        } else {
                            size = prefix.size();
                        }
                    }
                    if (size == -1)
                        size = prefix.size();
                    Expression<Integer, Resolver> value = Expressions
                            .createInteger(context, prefix.value());
                    expressions.add(value);
                    codecs.add(codec);
                }
            }
        }
        return new TypePrefixSelector(expressions, codecs, size, byteOrder);
    }

    /**
     * A {@link CodecSelector} that determines its choice on a couple of leading bits. The correspondence between {@link
     * Codec} and leading bits is based on the {@link TypePrefix} annotation.
     */
    private static class TypePrefixSelector implements CodecSelector {

        private List<Codec<?>> codecs;

        private Set<Codec<?>> uniqueCodecs;

        private List<Expression<Integer, Resolver>> expressions;

        private final ByteOrder byteOrder;

        private int size;

        public TypePrefixSelector(
                List<Expression<Integer, Resolver>> expressions,
                List<Codec<?>> codecs, int size, ByteOrder byteOrder) {
            this.uniqueCodecs = new HashSet<Codec<?>>();
            this.codecs = codecs;
            this.expressions = expressions;
            this.size = size;
            this.byteOrder = byteOrder;
            this.uniqueCodecs.addAll(codecs);
        }

        public Collection<Codec<?>> getChoices() {
            return uniqueCodecs;
        }

        public Codec<?> select(BitBuffer buffer, Resolver resolver)
                throws DecodingException {
            long index = buffer.readAsLong(size, byteOrder);
            for (int i = 0; i < codecs.size(); i++) {
                if (index == expressions.get(i).eval(resolver)) {
                    return codecs.get(i);
                }
            }
            throw new DecodingException("No matching Codec found for value "
                    + index);
        }

        public <T> Codec<?> select(Class<T> type, BitChannel channel, Resolver resolver) throws IOException {
            for (int i = 0; i < codecs.size(); i++) {
                Codec<?> codec = codecs.get(i);
                if (type.isAssignableFrom(codec.getType())) {
                    // So we found the Codec. Now to make sure that same Codec is picked up again while decoding:
                    channel.write(size, expressions.get(i).eval(resolver), ByteOrder.BigEndian);
                    return codec;
                }
            }
            return null;
        }

        public void document(final ParaContents<?> para) {
            para.text(" The particular choice is based on a " + size + "-bit ");
            para.text(" value preceeding the actual encoded value.");
            for (int i = 0; i < codecs.size(); i++) {
                Codec<?> codec = codecs.get(i);
                Expression<Integer, Resolver> expression = expressions.get(i);
                para.text(" If ");
                expression.document(new Document() {

                    public Document detail(String text) {
                        para.text(text);
                        return this;
                    }

                    public void link(Object object, String text) {
                        para.text(text);
                    }

                    public void text(String text) {
                        para.text(text);
                    }

                });
                para
                        .text(", then ").document(
                        codec.getCodecDescriptor().reference(Adjective.THE, false));
                para.text(" will be choosen.");
            }
        }

        public Expression<Integer, Resolver> getSize() {
            return Expressions.createInteger(size, Resolver.class);
        }

    }

}
