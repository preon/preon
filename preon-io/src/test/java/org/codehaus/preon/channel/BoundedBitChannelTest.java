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
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


public class BoundedBitChannelTest {

    private BitChannel channel;

    private BoundedBitChannel boundedChannel;

    @Before
    public void configureBoundedChannel() {
        channel = mock(BitChannel.class);
        boundedChannel = new BoundedBitChannel(channel, 9);
    }

    @Test
    public void shouldAccept9BitsAsInt() throws IOException {
        boundedChannel.write(9, Integer.MAX_VALUE, ByteOrder.BigEndian);
        verify(channel).write(9, Integer.MAX_VALUE, ByteOrder.BigEndian);
        verifyNoMoreInteractions(channel);
    }

    @Test(expected = IOException.class)
    public void shouldReject10BitsAsInt() throws IOException {
        boundedChannel.write(10, Integer.MAX_VALUE, ByteOrder.BigEndian);
    }

    @Test
    public void shouldAccept9BitsAsLong() throws IOException {
        boundedChannel.write(9, Long.MAX_VALUE, ByteOrder.BigEndian);
        verify(channel).write(9, Long.MAX_VALUE, ByteOrder.BigEndian);
        verifyNoMoreInteractions(channel);
    }

    @Test(expected = IOException.class)
    public void shouldReject10BitsAsLong() throws IOException {
        boundedChannel.write(10, Long.MAX_VALUE, ByteOrder.BigEndian);
    }

    @Test
    public void shouldAccept9BitsAsShort() throws IOException {
        boundedChannel.write(9, Short.MAX_VALUE, ByteOrder.BigEndian);
        verify(channel).write(9, Short.MAX_VALUE, ByteOrder.BigEndian);
        verifyNoMoreInteractions(channel);
    }

    @Test(expected = IOException.class)
    public void shouldReject10BitsAsShort() throws IOException {
        boundedChannel.write(10, Short.MAX_VALUE, ByteOrder.BigEndian);
    }

    @Test
    public void shouldAccept9BitsAsBytesAccummulated() throws IOException {
        boundedChannel.write(8, Byte.MAX_VALUE, ByteOrder.BigEndian);
        boundedChannel.write(1, Byte.MAX_VALUE, ByteOrder.BigEndian);
        verify(channel).write(8, Byte.MAX_VALUE, ByteOrder.BigEndian);
        verify(channel).write(1, Byte.MAX_VALUE, ByteOrder.BigEndian);
        verifyNoMoreInteractions(channel);
    }

    @Test(expected = IOException.class)
    public void shouldReject10BitsAsBytesAccummulated() throws IOException {
        boundedChannel.write(8, Byte.MAX_VALUE, ByteOrder.BigEndian);
        boundedChannel.write(2, Byte.MAX_VALUE, ByteOrder.BigEndian);
    }

    @Test
    public void shouldCloseCorrectly() throws IOException {
        boundedChannel.close();
        verify(channel).close();
    }

    @Test
    public void shouldReportRelativeBitPosCorrectly() throws IOException {
        when(channel.getRelativeBitPos()).thenReturn(4);
        assertThat(boundedChannel.getRelativeBitPos(), is(4));
        verify(channel).getRelativeBitPos();
    }

}
