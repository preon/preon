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

import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import org.codehaus.preon.*;
import org.codehaus.preon.annotation.Purpose;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.el.Expression;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;

public class PurposeDecorator implements CodecDecorator {

    public <T> Codec<T> decorate(final Codec<T> codec, AnnotatedElement metadata, Class<T> type, ResolverContext context) {
        final Purpose purpose =
                metadata != null ? metadata.getAnnotation(Purpose.class) : null;
        if (purpose != null) {
            
            return new Codec<T>() {
                public T decode(BitBuffer buffer, Resolver resolver, Builder builder) throws DecodingException {
                    return codec.decode(buffer, resolver, builder);
                }

                public void encode(T value, BitChannel channel, Resolver resolver) throws IOException {
                    codec.encode(value, channel, resolver);
                }

                public Expression<Integer, Resolver> getSize() {
                    return codec.getSize();
                }

                public CodecDescriptor getCodecDescriptor() {
                    return new CodecDescriptor() {
                        public <C extends ParaContents<?>> Documenter<C> summary() {
                            return new Documenter<C>() {
                                public void document(C c) {
                                    c.text(purpose.value() + " ").document(codec.getCodecDescriptor().summary());
                                }
                            };
                        }

                        public <C extends ParaContents<?>> Documenter<C> reference(Adjective adjective, boolean startWithCapital) {
                            return codec.getCodecDescriptor().reference(adjective, startWithCapital);
                        }

                        public <C extends SimpleContents<?>> Documenter<C> details(final String bufferReference) {
                            return codec.getCodecDescriptor().details(bufferReference);
                        }

                        public boolean requiresDedicatedSection() {
                            return codec.getCodecDescriptor().requiresDedicatedSection();
                        }

                        public String getTitle() {
                            return codec.getCodecDescriptor().getTitle();
                        }
                    };
                }

                public Class<?>[] getTypes() {
                    return codec.getTypes();
                }

                public Class<?> getType() {
                    return codec.getType();
                }
            };
        } else {
            return codec;
        }

    }

}
