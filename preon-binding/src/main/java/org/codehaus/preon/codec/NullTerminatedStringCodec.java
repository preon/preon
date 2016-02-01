/**
 * Copyright (c) 2009-2016 Wilfred Springer
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
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

    private BoundString.ByteConverter byteConverter;

    public NullTerminatedStringCodec(Charset encoding, String match,
                                     BoundString.ByteConverter byteConverter) {
        this.encoding = encoding;
        this.match = match;
        this.byteConverter = byteConverter;
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
			bytevalue = byteConverter.convert(buffer.readAsByte(8)); //Convert our byte
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
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = byteConverter.revert(bytes[i]);
        }
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
