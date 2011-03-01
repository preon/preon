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
package org.codehaus.preon.sample.varlength;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.codehaus.preon.Builder;
import org.codehaus.preon.Codec;
import org.codehaus.preon.CodecDescriptor;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.el.Expression;

import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;

public class VariableLengthByteArrayCodec implements Codec<byte[]> {

    public byte[] decode(BitBuffer buffer, Resolver resolver, Builder builder) throws DecodingException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        boolean cont = true;
        while (cont) {
            byte b = buffer.readAsByte(8);
            bout.write(b);
            cont = (b & (1 << 7)) > 0;
        }
        return bout.toByteArray();
    }

    public void encode(byte[] value, BitChannel channel, Resolver resolver) throws IOException {
        channel.write(value, 0, value.length - 1);
    }

    public Expression<Integer, Resolver> getSize() {
        return null;
    }

    public CodecDescriptor getCodecDescriptor() {
        return new CodecDescriptor() {

            public <C extends ParaContents<?>> Documenter<C> summary() {
                return new Documenter<C>() {
                    public void document(C target) {
                        target.document(reference(Adjective.A, true));
                        target.text(".");
                    }
                };
            }

            public <C extends ParaContents<?>> Documenter<C> reference(final Adjective adjective, final boolean startWithCapital) {
                return new Documenter<C>() {
                    public void document(C target) {
                        target.text(adjective.asTextPreferA(startWithCapital))
                                .text("variable length encoded byte array.");
                    }
                };
            }

            public <C extends SimpleContents<?>> Documenter<C> details(String bufferReference) {
                return new Documenter<C>() {
                    public void document(C target) {
                        target.para()
                                .text("The number of bytes is determined by the ")
                                .text("leading bit of the individual bytes; ")
                                .text("if the first bit of a byte is 1, then ")
                                .text("more bytes are expted to follow.");
                    }
                };

            }

            public boolean requiresDedicatedSection() {
                return false;
            }

            public String getTitle() {
                assert requiresDedicatedSection();
                return null;
            }
        };
    }

    public Class<?>[] getTypes() {
        return new Class<?>[] { Byte[].class };
    }

    public Class<?> getType() {
        return Byte[].class;
    }
}
