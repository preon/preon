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
