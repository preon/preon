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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/** A {@link BitChannel} that wraps an {@link java.io.OutputStream}. */
@NotThreadSafe
public class OutputStreamBitChannel implements BitChannel, Closeable {

    private static final byte[] MASK_UPPER = new byte[]{
            (byte) Integer.parseInt("00000000", 2),
            (byte) Integer.parseInt("00000001", 2),
            (byte) Integer.parseInt("00000011", 2),
            (byte) Integer.parseInt("00000111", 2),
            (byte) Integer.parseInt("00001111", 2),
            (byte) Integer.parseInt("00011111", 2),
            (byte) Integer.parseInt("00111111", 2),
            (byte) Integer.parseInt("01111111", 2),
            (byte) Integer.parseInt("11111111", 2)};

    /** The {@link OutputStream} wrapped. */
    private final OutputStream out;

    /** A pointer to the current bit position, inside the {@link #buffer}. */
    private int bitPos;

    /** A buffer for the current po */
    private byte buffer;

    /** Constructs a new instance, accepting the {@link OutputStream} to wrap. */
    public OutputStreamBitChannel(@Nonnull OutputStream out) {
        this.out = out;
    }

    public void write(boolean value) throws IOException {
        if (value) {
            buffer = (byte) (0xff & ((buffer << 1) | 0x01));
        } else {
            buffer = (byte) (0xff & (buffer << 1));
        }
        if (++bitPos == 8) {
            bitPos = 0;
            out.write(buffer);
            buffer = 0;
        }
    }

    public void write(@Nonnegative int nrbits, byte value) throws IOException {
        assert nrbits > 0;
        assert nrbits <= 8;

        // The number of bits to copy into the buffer
        int length = Math.min(8 - bitPos, nrbits);

        // Create some space in the buffer
        buffer = (byte) (0xff & (buffer << length));

        // Chop of bits not required
        value = (byte) (0xff & MASK_UPPER[nrbits] & value);

        // Fill the buffer
        buffer = (byte) (buffer | (0xff & value) >> (nrbits - length));
        bitPos = bitPos + length;

        // Check if the buffer needs to be flushed
        if (bitPos > 7) {
            out.write(buffer);
            buffer = 0;
            bitPos = 0;
        }

        // Check if there is something else left to copy
        if (length < nrbits) {
            buffer = (byte) (MASK_UPPER[nrbits - length] & value);
            bitPos = nrbits - length;
        }
    }

    public void write(@Nonnegative int nrbits, int value, ByteOrder byteOrder)
            throws IOException {
        int steps = nrbits / 8;
        int remainder = nrbits % 8;
        if (byteOrder == ByteOrder.LittleEndian) {
            for (int i = 0; i < steps; i++) {
                write(8, (byte) (0xff & value));
                value = value >> 8;
            }
            if (remainder != 0) {
                write(remainder, (byte) (MASK_UPPER[remainder] & value));
            }
        } else {
            if (remainder != 0) {
                write(remainder, (byte) (MASK_UPPER[remainder] & (value >> (steps * 8))));
            }
            for (int i = steps - 1; i >= 0; i--) {
                write(8, (byte) (0xff & (value >> i * 8)));
            }
        }
    }

    public void write(@Nonnegative int nrbits, long value, ByteOrder byteOrder)
            throws IOException {
        int steps = nrbits / 8;
        int remainder = nrbits % 8;
        if (byteOrder == ByteOrder.LittleEndian) {
            for (int i = 0; i < steps; i++) {
                write(8, (byte) (0xff & value));
                value = value >> 8;
            }
            if (remainder != 0) {
                write(remainder, (byte) (MASK_UPPER[remainder] & value));
            }
        } else {
            if (remainder != 0) {
                write(remainder, (byte) (MASK_UPPER[remainder] & (value >> (steps * 8))));
            }
            for (int i = steps - 1; i >= 0; i--) {
                write(8, (byte) (0xff & (value >> i * 8)));
            }
        }
    }

    public void write(@Nonnegative int nrbits, short value, ByteOrder byteOrder)
            throws IOException {
        int steps = nrbits / 8;
        int remainder = nrbits % 8;
        if (byteOrder == ByteOrder.LittleEndian) {
            for (int i = 0; i < steps; i++) {
                write(8, (byte) (0xff & value));
                value = (short) (value >> 8);
            }
            if (remainder != 0) {
                write(remainder, (byte) (MASK_UPPER[remainder] & value));
            }
        } else {
            if (remainder != 0) {
                write(remainder, (byte) (MASK_UPPER[remainder] & (value >> (steps * 8))));
            }
            for (int i = steps - 1; i >= 0; i--) {
                write(8, (byte) (0xff & (value >> i * 8)));
            }
        }
    }

    public void write(@Nonnull byte[] src, int offset, int length) throws IOException {
        for (int i = 0; i < length; i++) {
            write(8, src[offset + i]);
        }
    }

    public long write(@Nonnull ByteBuffer buffer) throws IOException {
        WritableByteChannel channel = null;
        try {
            channel = Channels.newChannel(out);
            return channel.write(buffer) * 8;
        } finally {
            channel.close();
        }
    }

    public
    @Nonnegative
    int getRelativeBitPos() {
        return bitPos;
    }

    public void close() throws IOException {
        out.close();
    }

}
