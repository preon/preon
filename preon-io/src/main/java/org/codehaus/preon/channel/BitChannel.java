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
