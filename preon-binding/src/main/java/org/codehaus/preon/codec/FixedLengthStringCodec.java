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

import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Expressions;
import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import org.codehaus.preon.*;
import org.codehaus.preon.annotation.BoundString;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.io.StringWriter;
import java.nio.BufferUnderflowException;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.descriptor.Documenters;

import java.io.IOException;

/**
 * A {@link org.codehaus.preon.Codec} decoding Strings based on a fixed number of <em>bytes</em>. (Note that it says
 * <i>bytes</i>, not <i>characters</i>.)
 */
public class FixedLengthStringCodec implements Codec<String> {

    private final Charset encoding;
    
    private final CharsetEncoder encoder;

    private final Expression<Integer, Resolver> sizeExpr;

    private final String match;

    private final BoundString.ByteConverter byteConverter;

    public FixedLengthStringCodec(Charset encoding,
                                  Expression<Integer, Resolver> sizeExpr, String match,
                                  BoundString.ByteConverter byteConverter) {
        this.encoding = encoding;
        this.sizeExpr = sizeExpr;
        this.match = match;
        this.byteConverter = byteConverter;
        this.encoder = encoding.newEncoder();
    }

    public String decode(BitBuffer buffer, Resolver resolver,
                         Builder builder) throws DecodingException {
		/* This takes a slice of the BitBuffer as a ByteBuffer,
		 * and feeds it into encoding.decode.
		 * */
        int size = sizeExpr.eval(resolver);
        ByteBuffer bytebuffer = ByteBuffer.allocate(size);
		byte readbyte;
		for (int i = 0; i < size; i++) {
			readbyte = byteConverter.convert(buffer.readAsByte(8));
            bytebuffer.put(readbyte);
        }
        bytebuffer.rewind();
        String result;
        result = encoding.decode(bytebuffer).toString();
        result = result.trim(); // remove padding characters
        if (match.length() > 0) {
            if (!match.equals(result)) {
                throw new DecodingException(new IllegalStateException(
                        "Expected \"" + match + "\", but got \"" + result
                                + "\"."));
            }
        }
        return result;
    }

    public void encode(String value, BitChannel channel, Resolver resolver) throws IOException {
        int size = sizeExpr.eval(resolver);
        ByteBuffer bytebuffer = ByteBuffer.allocate(size);
        encoder.encode(CharBuffer.wrap(value), bytebuffer, true);

        if (bytebuffer.position() < size) { // pad with 0's
            bytebuffer.put(new byte[size - bytebuffer.position()]);
        }
        bytebuffer.flip(); // switch to reading
        
        byte[] bytes = new byte[size];
        bytebuffer.get(bytes);
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = byteConverter.revert(bytes[i]);
        }
        //assert (size <= bytes.length); //No longer needed
        channel.write(bytes, 0, size);
    }

    public Class<?>[] getTypes() {
        return new Class[]{String.class};
    }

    public Expression<Integer, Resolver> getSize() {
        return Expressions.multiply(Expressions.createInteger(8,
                Resolver.class), sizeExpr);
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
                        target
                                .para()
                                .text("The number of characters of the string is ")
                                .document(
                                        Documenters.forExpression(sizeExpr))
                                .text(".").end();
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
                                .text("A sequence of characters, encoded in "
                                        + encoding + ".");
                    }
                };
            }

        };
    }
}
