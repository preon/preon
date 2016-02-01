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
package org.codehaus.preon.channel;

import org.codehaus.preon.buffer.ByteOrder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.OutputStream;

@RunWith(MockitoJUnitRunner.class)
public class OutputStreamBitChannelTest {

    @Mock
    private OutputStream out;

    @Test
    public void shouldAcceptBooleans() throws IOException {
        OutputStreamBitChannel channel = new OutputStreamBitChannel(out);
        channel.write(true);
        channel.write(true);
        channel.write(true);
        channel.write(true);
        channel.write(false);
        channel.write(false);
        channel.write(false);
        channel.write(false);
        channel.write(false);
        verify(out).write((byte) 0xf0);
        verifyNoMoreInteractions(out);
    }

    @Test
    public void shouldAcceptFullBytes() throws IOException {
        OutputStreamBitChannel channel = new OutputStreamBitChannel(out);
        channel.write(8, (byte) 32);
        verify(out).write((byte) 32);
        verifyNoMoreInteractions(out);
    }

    @Test
    public void shouldAcceptPartialBytes() throws IOException {
        OutputStreamBitChannel channel = new OutputStreamBitChannel(out);
        channel.write(4, (byte) 0xff); // 1111
        channel.write(4, (byte) 0x00); // 0000
        verify(out).write((byte) 0xf0);
        verifyNoMoreInteractions(out);
    }

    @Test
    public void shouldDealWithNonAlignedBytes() throws IOException {
        OutputStreamBitChannel channel = new OutputStreamBitChannel(out);
        channel.write(3, (byte) 0xff); // 111
        channel.write(7, (byte) 0x00); // 0000000
        verify(out).write((byte) Integer.parseInt("11100000", 2));
        verifyNoMoreInteractions(out);
    }

    @Test
    public void shouldDealWithNonAlignedMultipleBytes() throws IOException {
        OutputStreamBitChannel channel = new OutputStreamBitChannel(out);
        channel.write(3, (byte) 0xff); // 111
        channel.write(7, (byte) 0x00); // 0000000
        channel.write(8, (byte) 0xff); // 11111111
        channel.write(6, (byte) 0x00); // 000000
        verify(out).write((byte) Integer.parseInt("11100000", 2));
        verify(out).write((byte) Integer.parseInt("00111111", 2));
        verify(out).write((byte) Integer.parseInt("11000000", 2));
        verifyNoMoreInteractions(out);
    }

    @Test
    public void shouldAcceptInts() throws IOException {
        OutputStreamBitChannel channel = new OutputStreamBitChannel(out);
        channel.write(12, (int) 0xfff, ByteOrder.BigEndian); // 1111 1111 1111
        channel.write(4, (int) 0x0, ByteOrder.BigEndian); // 0000
        verify(out).write((byte) Integer.parseInt("11111111", 2));
        verify(out).write((byte) Integer.parseInt("11110000", 2));
        verifyNoMoreInteractions(out);
    }

    @Test
    public void shouldAcceptIntsAndBytes() throws IOException {
        OutputStreamBitChannel channel = new OutputStreamBitChannel(out);
        channel.write(12, (int) 0xfff, ByteOrder.BigEndian); // 1111 1111 1111
        channel.write(5, (byte) 0x0); // 0000 0
        verify(out).write((byte) Integer.parseInt("11111111", 2));
        verify(out).write((byte) Integer.parseInt("11110000", 2));
        verifyNoMoreInteractions(out);
    }

    @Test
    public void shouldAcceptLittleEndian() throws IOException {
        OutputStreamBitChannel channel = new OutputStreamBitChannel(out);
        channel.write(12, (int) 0xf00, ByteOrder.LittleEndian); // 1111 0000 0000 
        channel.write(4, (int) 0x0, ByteOrder.LittleEndian); // 0000
        // What I expect:
        // 0000 0000 1111 0000
        verify(out).write((byte) Integer.parseInt("00000000", 2));
        verify(out).write((byte) Integer.parseInt("11110000", 2));
        verifyNoMoreInteractions(out);
    }

    @Test
    public void shouldAcceptLongs() throws IOException {
        OutputStreamBitChannel channel = new OutputStreamBitChannel(out);
        channel.write(12, Long.MAX_VALUE / 2, ByteOrder.BigEndian); // 1111 1111 1111
        channel.write(5, (byte) 0x0); // 0000 0
        verify(out).write((byte) Integer.parseInt("11111111", 2));
        verify(out).write((byte) Integer.parseInt("11110000", 2));
        verifyNoMoreInteractions(out);
    }

    @Test
    public void shouldTellPositionCorrectly() throws IOException {
        OutputStreamBitChannel channel = new OutputStreamBitChannel(out);
        channel.write(4, (byte) 12);
        assertThat(channel.getRelativeBitPos(), is(4));
        channel.write(4, (byte) 0);
        assertThat(channel.getRelativeBitPos(), is(0));
    }

}
