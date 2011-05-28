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
import org.codehaus.preon.annotation.BoundBuffer;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.descriptor.Documenters;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;

public class BoundBufferCodecFactory implements CodecFactory {

    private Class<?> BYTE_CLASS = (new byte[0]).getClass();

    public <T> Codec<T> create(AnnotatedElement metadata, Class<T> type,
                               ResolverContext context) {
        if (type.isArray() && BYTE_CLASS.equals(type)
                && metadata.isAnnotationPresent(BoundBuffer.class)) {
            return (Codec<T>) new BoundBufferCodec(metadata.getAnnotation(
                    BoundBuffer.class).match());
        } else {
            return null;
        }
    }

    private static class BoundBufferCodec implements Codec<Object> {

        private byte[] criterion;

        public BoundBufferCodec(byte[] matches) {
            this.criterion = matches;
        }

        public Object decode(BitBuffer buffer, Resolver resolver,
                             Builder builder) throws DecodingException {
            for (int i = 0; i < criterion.length; i++) {
                if (criterion[i] != buffer.readAsByte(8)) {
                    throw new DecodingException("First " + criterion.length
                            + " bytes do not match expected value.");
                }
            }
            return criterion;
        }

        public void encode(Object object, BitChannel channel, Resolver resolver) throws IOException {
            channel.write(criterion, 0, criterion.length);
        }

        public CodecDescriptor getCodecDescriptor() {
            return new CodecDescriptor() {

                public <C extends SimpleContents<?>> Documenter<C> details(
                        String bufferReference) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target
                                    .para()
                                    .text(
                                            "The sequence is expected to match this hexidecimal sequence: ")
                                    .document(
                                            Documenters
                                                    .forHexSequence(criterion))
                                    .text(".").end();
                        }
                    };
                }

                public String getTitle() {
                    return null;
                }

                public <C extends ParaContents<?>> Documenter<C> reference(
                        final Adjective adjective,
                        final boolean startWithCapital) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target.text(
                                    adjective.asTextPreferA(startWithCapital))
                                    .text(" sequence of bytes");
                        }
                    };
                }

                public boolean requiresDedicatedSection() {
                    return false;
                }

                public <C extends ParaContents<?>> Documenter<C> summary() {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target.document(reference(Adjective.A, true)).text(
                                    ".");
                        }
                    };
                }

            };
        }

        public Expression<Integer, Resolver> getSize() {
            return Expressions.createInteger(criterion.length * 8, Resolver.class);
        }

        public Class<?> getType() {
            return byte.class;
        }

        public Class<?>[] getTypes() {
            return new Class<?>[]{byte.class};
        }
    }

}
