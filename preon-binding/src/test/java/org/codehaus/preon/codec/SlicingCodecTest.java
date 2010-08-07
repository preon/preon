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
