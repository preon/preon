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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharsetDecoder;
import java.nio.BufferUnderflowException;
import java.io.StringWriter;
import java.io.IOException;

/**
 * A {@link org.codehaus.preon.Codec} that reads null-terminated Strings. Basically, it will read bytes until it
 * encounters a '\0' character, in which case it considers itself to be done, and construct a String from the bytes
 * read.
 *
 * @author Wilfred Springer (wis)
 */
public class NullTerminatedStringCodec implements Codec<String> {

	private static int BUFFER_SIZE = 32; //32 Bytes is probably overkill, but these days it hardly matters

    private Charset encoding;

    private String match;

    public NullTerminatedStringCodec(Charset encoding, String match) {
        this.encoding = encoding;
        this.match = match;
    }

    public String decode(BitBuffer buffer, Resolver resolver,
                         Builder builder) throws DecodingException {
		/* This has been gutted, and now uses Charsets to do decoding.
		 * It opens the bitbuffer as a bytebuffer (taking care to note
		 * and preserve positions), creates a CharBuffer with space for
		 * one character, and decodes the ByteBuffer one character at a
		 * time. If the character decoded is NULL, it finishes up (it has
		 * to use the decoded character, not the byte, as multibyte
		 * encodings can include null bytes in non-null characters).
		 * 
		 * I used a StringWriter for the string, as it's more memory
		 * efficient, for what it's worth.
		 * 
		 * I wasn't able to find a way to include the byteConverter in
		 * the decoding process. I'm guessing the main use for byteConverter
		 * was encoding conversion anyway, but if it's needed, it might
		 * be possible to subclass ByteBuffer.
		 * */
        CharsetDecoder decoder = encoding.newDecoder();
        ByteBuffer bytebuffer = ByteBuffer.allocate(BUFFER_SIZE); //Allocate a bytebuffer. We'll need this for multibyte encodings
		CharBuffer charbuffer = CharBuffer.allocate(1); //Decode one character at a time
        StringWriter sw = new StringWriter(); //This will eventually hold our string
        byte bytevalue;
        char charvalue;
        boolean readOK = true;
		do {
			bytevalue = buffer.readAsByte(8); //Convert our byte
			bytebuffer.put(bytevalue); // and add it to the bytebuffer
			bytebuffer.flip(); // Flip the buffer, so we can read it
			decoder.decode(bytebuffer,charbuffer,false); // Decode up to one char from bytebuffer
			if (charbuffer.position() == 1) {
				charbuffer.rewind();
				charvalue = charbuffer.get();
				charbuffer.rewind();
				if (charvalue == 0) { //If character is null, we're finished
					readOK = false;
				}
				else {
					sw.append(charvalue); //Write character to StringWriter
				}
			}
			bytebuffer.compact(); //Compact the buffer, so we can write to it
		}
		while(readOK);
		return sw.toString();
    }

    public void encode(String value, BitChannel channel, Resolver resolver) throws IOException {
		/* This is a crude first attempt
		 * */
		ByteBuffer bytebuffer = encoding.encode(value+"\u0000");
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
                                        + encoding + ".");
                    }
                };
            }

        };
    }
}
