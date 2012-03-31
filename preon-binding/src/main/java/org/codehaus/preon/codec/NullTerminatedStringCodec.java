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

import org.codehaus.preon.*;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.annotation.BoundString;
import org.codehaus.preon.el.Expression;
import nl.flotsam.pecia.SimpleContents;
import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import org.codehaus.preon.util.InputStreamReaderFactory;

import java.io.*;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.BufferUnderflowException;

/**
 * A {@link Codec} that reads null-terminated Strings. Basically, it will read bytes until it
 * encounters a '\0' character, in which case it considers itself to be done, and construct a String from the bytes
 * read.
 *
 * @author Wilfred Springer (wis)
 */
public class NullTerminatedStringCodec implements Codec<String> {

    private final Charset charset;
    private final String match;
    private final InputStreamReaderFactory factory;

    public NullTerminatedStringCodec(final Charset charset, final InputStreamReaderFactory factory, final String match) {
        this.charset = charset;
        this.match = match;
        this.factory = factory;
    }

    public String decode(final BitBuffer buffer, final Resolver resolver, final Builder builder) throws DecodingException {
        final InputStreamReader reader = factory.createBitBufferInputStreamReader(buffer, charset);
        final StringBuilder strBuilder = new StringBuilder();

        try {
            for (int charRead = reader.read(); !isNullTerminationCharacter(charRead); charRead = reader.read()) {
                if (isEndOfStreamValue(charRead)) {
                    throw new DecodingException("NullTerminatedStringCodec.decode(): Read past end of BitBuffer");
                }

                strBuilder.append((char)charRead);
            }
        } catch (IOException e) {
            throw new DecodingException("NullTerminatedStringCodec.decode() threw IOException", e);
        }

        return strBuilder.toString();
    }

    private static boolean isEndOfStreamValue(final int value) {
        return value == -1;
    }

    private static boolean isNullTerminationCharacter(final int character) {
        return character == '\0';
    }

    public void encode(String value, BitChannel channel, Resolver resolver) throws IOException {
		/* This is a crude first attempt
		 * */
		ByteBuffer bytebuffer = charset.encode(value + "\u0000");
        byte[] bytes = bytebuffer.array();
        channel.write(bytes, 0, bytes.length);
    }

    public Class<?>[] getTypes() {
        return new Class[]{String.class};
    }

    public Expression<Integer, Resolver> getSize() {
        return null;
    }

    public Class<?> getType() {
        return String.class;
    }

    public CodecDescriptor getCodecDescriptor() {
        return new CodecDescriptor() {

            public <C extends SimpleContents<?>> Documenter<C> details(
                    String bufferReference) {
                return new Documenter<C>() {
                    public void document(C target) {
                        if (match != null && match.length() > 0) {
                            target.para().text(
                                    "The string is expected to match \"")
                                    .text(match).text("\".").end();
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
                        target.text(adjective.asTextPreferA(false)).text(
                                "string of characters");
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
                                .text("A null-terminated sequence of characters, encoded in "
                                        + charset + ".");
                    }
                };
            }

        };
    }
}
