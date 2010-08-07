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
import org.codehaus.preon.*;
import org.codehaus.preon.annotation.ByteAlign;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;

/**
 * A {@link CodecDecorator} that will make sure that reading stops at a byte-aligned position.
 *
 * @author Wilfred Springer
 */
public class ByteAligningDecorator implements CodecDecorator {

    public <T> Codec<T> decorate(Codec<T> decorated, AnnotatedElement metadata,
                                 Class<T> type, ResolverContext context) {
        if (type.isAnnotationPresent(ByteAlign.class)
                || (metadata != null && metadata
                .isAnnotationPresent(ByteAlign.class))) {
            return new ByteAligningCodec<T>(decorated);
        } else {
            return decorated;
        }
    }

    private static class ByteAligningCodec<T> implements Codec<T> {

        private Codec<T> decorated;

        public ByteAligningCodec(Codec<T> decorated) {
            this.decorated = decorated;
        }

        public T decode(BitBuffer buffer, Resolver resolver, Builder builder)
                throws DecodingException {
            T result = decorated.decode(buffer, resolver, builder);
            long pos = buffer.getBitPos() % 8;
            if (pos > 0) {
                buffer.setBitPos(buffer.getBitPos() + 8 - pos);
            }
            return result;
        }

        public void encode(T object, BitChannel channel, Resolver resolver) throws IOException {
            int bits = 8 - channel.getRelativeBitPos();
            if (bits != 8) {
                channel.write(bits, (byte) 0);
            }
            decorated.encode(object, channel, resolver);
        }

        public Class<?>[] getTypes() {
            return decorated.getTypes();
        }

        public Expression<Integer, Resolver> getSize() {
            return null;
        }

        public Class<?> getType() {
            return decorated.getType();
        }

        public CodecDescriptor getCodecDescriptor() {
            return new CodecDescriptor() {

                public <C extends SimpleContents<?>> Documenter<C> details(
                        String bufferReference) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target
                                    .para()
                                    .text("If - after reading ")
                                    .document(
                                            decorated.getCodecDescriptor()
                                                    .reference(Adjective.THE, false))
                                    .text(" - the pointer is ")
                                    .emphasis("not")
                                    .text(
                                            " at the end of the byte, then the last couple of bits will be skipped.")
                                    .end();
                        }
                    };
                }

                public String getTitle() {
                    return null;
                }

                public <C extends ParaContents<?>> Documenter<C> reference(
                        Adjective adjective, boolean startWithCapital) {
                    return decorated.getCodecDescriptor().reference(adjective, false);
                }

                public boolean requiresDedicatedSection() {
                    return false;
                }

                public <C extends ParaContents<?>> Documenter<C> summary() {
                    return decorated.getCodecDescriptor().summary();
                }

            };
        }

    }

}
