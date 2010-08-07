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
import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import org.codehaus.preon.Builder;
import org.codehaus.preon.Codec;
import org.codehaus.preon.CodecDescriptor;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.channel.BoundedBitChannel;
import org.codehaus.preon.descriptor.Documenters;

import java.io.IOException;

/**
 * {@link Codec} decoration, preventing the underlying {@link Codec} from being able to read beyond a certain section of
 * the {@link org.codehaus.preon.buffer.BitBuffer} passed in.
 *
 * @param <T> The type of object expected to be returned by this {@link Codec}.
 */
class SlicingCodec<T> implements Codec<T> {

    private final Expression<Integer, Resolver> sizeExpr;

    private final Codec<T> wrapped;

    /**
     * Constructs a new instance.
     *
     * @param wrapped  The {@link Codec} to be wrapped.
     * @param sizeExpr The size of the slice, expressed in bits, as a Limbo expression.
     */
    public SlicingCodec(Codec<T> wrapped, Expression<Integer, Resolver> sizeExpr) {
        this.sizeExpr = sizeExpr;
        this.wrapped = wrapped;
    }

    public T decode(BitBuffer buffer, Resolver resolver, Builder builder)
            throws DecodingException {
        BitBuffer slice = buffer
                .slice(sizeExpr.eval(resolver));
        return wrapped.decode(slice, resolver, builder);
    }

    public void encode(T value, BitChannel channel, Resolver resolver) throws IOException {
        wrapped.encode(value, new BoundedBitChannel(channel, sizeExpr.eval(resolver)), resolver);
    }

    public Class<?>[] getTypes() {
        return wrapped.getTypes();
    }

    public Expression<Integer, Resolver> getSize() {
        return sizeExpr;
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
                                                .forExpression(sizeExpr))
                                .text(" bits for ")
                                .document(
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
