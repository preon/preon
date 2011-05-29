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
