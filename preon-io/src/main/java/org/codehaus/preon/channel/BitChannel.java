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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;

/**
 * A {@link Channel} type of abstraction for writing bits. Note that in Preon, a {@link BitChannel} is currently for
 * output only.
 */
public interface BitChannel {

    /**
     * Writes the boolean value as a bit to the channel. (Writes an 1 in case of <code>true</code> value, and 0
     * otherwise.)
     */
    void write(boolean value) throws IOException;

    /** Writes <code>nrbits</code> bits of the byte to the channel. */
    void write(int nrbits, byte value) throws IOException;

    /**
     * Writes <code>nrbits</code> bits of the int value to the channel, based on the {@link ByteOrder} passed in, in
     * case the number of bits exceeds 8.
     */
    void write(int nrbits, int value, ByteOrder byteOrder) throws IOException;

    /**
     * Writes <code>nrbits</code> bits of the long value to the channel, based on the {@link ByteOrder} passed in, in
     * case the number of bits exceeds 8.
     */
    void write(int nrbits, long value, ByteOrder byteOrder) throws IOException;

    /**
     * Writes <code>nrbits</code> bits of the short value to the channel, based on the {@link ByteOrder} passed in, in
     * case the number of bits exceeds 8.
     */
    void write(int nrbits, short value, ByteOrder byteOrder) throws IOException;

    /**
     * Writes <code>length</code> bytes from the <code>src</code> array of bytes to the channel, starting at the
     * position indicated by <code>offset</code>
     */
    void write(byte[] src, int offset, int length) throws IOException;

    /** Writes the contents of the {@link java.nio.ByteBuffer} to the channel. */
    long write(ByteBuffer buffer) throws IOException;

    /** Returns the position of the bit pointer in the current byte. */
    int getRelativeBitPos();

    /** Closes the channel. */
    void close() throws IOException;

}
