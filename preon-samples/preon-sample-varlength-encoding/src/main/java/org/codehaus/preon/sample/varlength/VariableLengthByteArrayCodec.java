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
