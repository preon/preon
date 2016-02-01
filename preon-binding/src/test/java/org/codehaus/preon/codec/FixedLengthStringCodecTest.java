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
import org.codehaus.preon.Builder;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.annotation.BoundString;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.buffer.DefaultBitBuffer;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.channel.OutputStreamBitChannel;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class FixedLengthStringCodecTest {

    @Mock
    private Resolver resolver;

    @Mock
    private Expression<Integer, Resolver> sizeExpr;
    
    @Mock
    private Builder builder;

    @Test
    public void shouldEncodeCorrectly() throws IOException, NullPointerException {
		Charset charset = Charset.availableCharsets().get(BoundString.Encoding.ASCII);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BitChannel channel = new OutputStreamBitChannel(out);
        when(sizeExpr.eval(Matchers.any(Resolver.class))).thenReturn(4);
        FixedLengthStringCodec codec =
                new FixedLengthStringCodec(charset, sizeExpr, null, new BoundString.NullConverter());
        codec.encode("Whatever", channel, resolver);
        out.flush();
        byte[] result = out.toByteArray();
        assertThat(result.length, is(4));
        assertThat(new String(result, "US-ASCII"), is("What"));
    }

    @Test
    public void shouldPadStringsShorterThanDeclared() throws DecodingException,
            IOException {
        int size = 16;
        String original = "short\u0100";
        when(sizeExpr.eval(Matchers.any(Resolver.class))).thenReturn(size);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FixedLengthStringCodec codec = new FixedLengthStringCodec(
                Charset.forName("UTF-16BE"), sizeExpr, "",
                new BoundString.NullConverter());

        codec.encode(original, new OutputStreamBitChannel(out), resolver);
        byte[] encoded = out.toByteArray();
        assertThat(encoded.length, is(size));

        BitBuffer buffer = new DefaultBitBuffer(ByteBuffer.wrap(encoded));
        String result = codec.decode(buffer, resolver, builder);
        assertThat(result, is(original));
    }
}
