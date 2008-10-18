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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.flotsam.limbo.Document;
import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Expressions;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.preon.Codec;
import nl.flotsam.preon.CodecConstructionException;
import nl.flotsam.preon.CodecSelector;
import nl.flotsam.preon.CodecSelectorFactory;
import nl.flotsam.preon.DecodingException;
import nl.flotsam.preon.Resolver;
import nl.flotsam.preon.ResolverContext;
import nl.flotsam.preon.annotation.TypePrefix;
import nl.flotsam.preon.buffer.BitBuffer;


/**
 * A {@link CodecSelectorFactory} that will create a {@link CodecSelector} that
 * will look for leading bits, matching a certain value expressed with an
 * {@link TypePrefix} annotation on the Codecs classes.
 * 
 * @author Wilfred Springer
 * 
 */
public class TypePrefixSelectorFactory implements CodecSelectorFactory {

    public CodecSelector create(ResolverContext context, List<Codec<?>> allCodecs) {
        int size = -1;
        List<Expression<Integer, Resolver>> expressions = new ArrayList<Expression<Integer, Resolver>>();
        List<Codec<?>> codecs = new ArrayList<Codec<?>>();
        for (Codec<?> codec : allCodecs) {
            for (Class<?> valueType : codec.getTypes()) {
                TypePrefix prefix = (TypePrefix) valueType.getAnnotation(TypePrefix.class);
                if (prefix == null) {
                    throw new CodecConstructionException(
                            "To little context to decide between codecs.");
                } else {
                    if (size != -1) {
                        if (size != prefix.size()) {
                            throw new CodecConstructionException(
                                    "Two distinct prefix sizes are not supported.");
                        } else {
                            size = prefix.size();
                        }
                    }
                    if (size == -1)
                        size = prefix.size();
                    Expression<Integer, Resolver> value = Expressions.createInteger(context, prefix
                            .value());
                    expressions.add(value);
                    codecs.add(codec);
                }
            }
        }
        return new TypePrefixSelector(expressions, codecs, size);
    }

    private static class TypePrefixSelector implements CodecSelector {

        private List<Codec<?>> codecs;

        private Set<Codec<?>> uniqueCodecs;

        private List<Expression<Integer, Resolver>> expressions;

        private int size;

        public TypePrefixSelector(List<Expression<Integer, Resolver>> expressions,
                List<Codec<?>> codecs, int size) {
            uniqueCodecs = new HashSet<Codec<?>>();
            this.codecs = codecs;
            this.expressions = expressions;
            this.size = size;
            uniqueCodecs.addAll(codecs);
        }

        public Collection<Codec<?>> getChoices() {
            return uniqueCodecs;
        }

        public Codec<?> select(BitBuffer buffer, Resolver resolver) throws DecodingException {
            long index = buffer.readAsLong(size);
            for (int i = 0; i < codecs.size(); i++) {
                if (index == expressions.get(i).eval(resolver)) {
                    return codecs.get(i);
                }
            }
            System.out.println("No matching Codec found for value " + index);
            throw new DecodingException("No matching Codec found for value " + index);
        }

        public int getSize(Resolver resolver) {
            int result = -1;
            for (Codec<?> codec : codecs) {
                int size = codec.getSize(resolver);
                if (size < 0)
                    return -1;
                if (result < 0) {
                    result = size;
                } else {
                    if (size != result) {
                        return -1;
                    }
                }
            }
            return result + this.size;
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
                para.text(", then the " + codec.getCodecDescriptor().getLabel());
                para.text(" will be choosen.");
            }
        }

    }

}
