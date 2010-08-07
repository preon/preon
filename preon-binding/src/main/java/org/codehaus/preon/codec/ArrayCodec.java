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

import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Expressions;
import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import org.codehaus.preon.*;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.descriptor.Documenters;

import java.io.IOException;
import java.lang.reflect.Array;

/**
 * The {@link org.codehaus.preon.Codec} for reading the {@link java.util.List} and its members, on demand. Instances of
 * this class will <em>not</em> create a standard {@link java.util.List} implementation and populate all of its data
 * immediately. Instead it will create a {@link org.codehaus.preon.util.EvenlyDistributedLazyList}, constructing its
 * elements on the fly, only when it is required.
 */
class ArrayCodec implements Codec<Object> {

    /** The number of elements in the list. */
    private Expression<Integer, Resolver> size;

    /** The {@link org.codehaus.preon.Codec} that will construct elements from the {@link java.util.List}. */
    private Codec<Object> codec;

    /** The type of element to be constructed. */
    private Class<?> type;

    /**
     * Constructs a new instance.
     *
     * @param expr  An {@link org.codehaus.preon.el.Expression} representing the number of elements in the {@link
     *              java.util.List}.
     * @param codec The {@link org.codehaus.preon.Codec} constructing elements in the {@link java.util.List}.
     */
    public ArrayCodec(Expression<Integer, Resolver> expr, Codec<Object> codec,
                      Class<?> type) {
        this.size = expr;
        this.codec = codec;
        this.type = type;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codehaus.preon.Codec#decode(org.codehaus.preon.buffer.BitBuffer,
     * org.codehaus.preon.Resolver, org.codehaus.preon.Builder)
     */

    public Object decode(BitBuffer buffer, Resolver resolver,
                         Builder builder) throws DecodingException {
        int length = size.eval(resolver).intValue();
        Object result = Array.newInstance(type.getComponentType(), length);
        for (int i = 0; i < length; i++) {
            Object value = codec.decode(buffer, resolver, builder);
            Array.set(result, i, value);
        }
        return result;
    }

    public void encode(Object object, BitChannel channel, Resolver resolver) throws IOException {
        int numberOfElements = size.eval(resolver);
        for (int i = 0; i < numberOfElements; i++) {
            codec.encode((Object) Array.get(object, i), channel, resolver);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codehaus.preon.Codec#getTypes()
     */

    public Class<?>[] getTypes() {
        return codec.getTypes();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codehaus.preon.Codec#getSize()
     */

    public Expression<Integer, Resolver> getSize() {
        return Expressions.multiply(size, codec.getSize());
    }

    public Class<?> getType() {
        return type;
    }

    public CodecDescriptor getCodecDescriptor() {
        return new CodecDescriptor() {

            public <C extends SimpleContents<?>> Documenter<C> details(
                    final String bufferReference) {
                return new Documenter<C>() {
                    public void document(C target) {
                        if (size != null) {
                            target
                                    .para()
                                    .text(
                                            "The number of elements in the list ")
                                    .text("is ")
                                    .document(
                                            Documenters
                                                    .forExpression(ArrayCodec.this.size))
                                    .text(".").end();
                        }
                        if (!codec.getCodecDescriptor()
                                .requiresDedicatedSection()) {
                            target.document(codec.getCodecDescriptor()
                                    .details(bufferReference));
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
                                        Adjective.NONE, false)).text(" elements");
                    }
                };
            }

            public boolean requiresDedicatedSection() {
                return false;
            }

            public <C extends ParaContents<?>> Documenter<C> summary() {
                return new Documenter<C>() {
                    public void document(C target) {
                        target.document(reference(Adjective.A, true));
                        target.text(".");
                    }
                };
            }

        };
    }

    public String toString() {
        return "Codec of array, decoding elements using " + codec;
    }
}
