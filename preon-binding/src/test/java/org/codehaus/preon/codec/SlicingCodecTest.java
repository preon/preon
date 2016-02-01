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
import org.codehaus.preon.Codec;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.channel.BoundedBitChannel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SlicingCodecTest {

    @Mock
    private Codec<String> wrapped;

    @Mock
    private Expression<Integer, Resolver> sizeExpr;

    @Mock
    private Builder builder;

    @Mock
    private BitBuffer buffer;

    @Mock
    private BitBuffer slice;

    @Mock
    private Resolver resolver;

    @Mock
    private BitChannel channel;

    @Test
    public void testDecoding() throws DecodingException {
        SlicingCodec<String> codec = new SlicingCodec(wrapped, sizeExpr);
        when(sizeExpr.eval(resolver)).thenReturn(13);
        when(buffer.slice(Mockito.anyInt())).thenReturn(slice);
        when(wrapped.decode(any(BitBuffer.class), any(Resolver.class), any(Builder.class))).thenReturn("DONE");
        assertThat(codec.decode(buffer, resolver, builder), is("DONE"));
        verify(sizeExpr).eval(resolver);
        verify(buffer).slice(13);
        verify(wrapped).decode(slice, resolver, builder);
        verifyNoMoreInteractions(wrapped, sizeExpr, builder, buffer, slice, resolver);
    }

    @Test
    public void testEncoding() throws IOException {
        SlicingCodec<String> codec = new SlicingCodec(wrapped, sizeExpr);
        when(sizeExpr.eval(resolver)).thenReturn(3);
        codec.encode("DONE", channel, resolver);
        ArgumentCaptor<BitChannel> bitChannelCaptor = ArgumentCaptor.forClass(BitChannel.class);
        verify(wrapped).encode(eq("DONE"), bitChannelCaptor.capture(), eq(resolver));
        verify(sizeExpr).eval(resolver);
        assertThat(bitChannelCaptor.getValue(), instanceOf(BoundedBitChannel.class));
        verifyNoMoreInteractions(wrapped, sizeExpr, resolver);
    }

//    @Test
//    public void testIntegration() throws DecodingException {
//        byte[] values = { 1, 2, 3, 4 };
//        Codec<Test1> codec = Codecs.create(Test1.class);
//        Test1 value = Codecs.decode(codec, values);
//        assertThat(value.value.length, is(4));
//    }
//
//    public static class Test1 {
//
//        @BoundList
//        @Slice(size = "4")
//        public byte[] value;
//
//    }

}
