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
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A {@link BitChannel} wrapping around another {@link BitChannel}, preventing writing more than a certain maximum
 * number of bits. In case anything like that happens, it will throw a {@link java.io.IOException}.
 */
public class BoundedBitChannel implements BitChannel {

    private final BitChannel channel;
    private final long maxBits;
    private long written;
    private static final String OVERRUN_MESSAGE = "Attempt to write beyond maximum number of bits allowed.";

    /**
     * Constructs a new instance.
     *
     * @param channel The {@link BitChannel} to wrap.
     * @param maxBits The maximum number of bits accepted.
     */
    public BoundedBitChannel(@Nonnull BitChannel channel, @Nonnegative long maxBits) {
        assert channel != null;
        assert maxBits >= 0;
        this.channel = channel;
        this.maxBits = maxBits;
    }

    public void write(boolean value) throws IOException {
        if (written + 1 <= maxBits) {
            channel.write(value);
            written += 1;
        } else {
            throw new IOException(OVERRUN_MESSAGE);
        }
    }

    public void write(int nrbits, byte value) throws IOException {
        if (written + nrbits <= maxBits) {
            channel.write(nrbits, value);
            written += nrbits;
        } else {
            throw new IOException(OVERRUN_MESSAGE);
        }
    }

    public void write(int nrbits, int value, ByteOrder byteOrder) throws IOException {
        if (written + nrbits <= maxBits) {
            channel.write(nrbits, value, byteOrder);
            written += nrbits;
        } else {
            throw new IOException(OVERRUN_MESSAGE);
        }
    }

    public void write(int nrbits, long value, ByteOrder byteOrder) throws IOException {
        if (written + nrbits <= maxBits) {
            channel.write(nrbits, value, byteOrder);
            written += nrbits;
        } else {
            throw new IOException(OVERRUN_MESSAGE);
        }
    }

    public void write(int nrbits, short value, ByteOrder byteOrder) throws IOException {
        if (written + nrbits <= maxBits) {
            channel.write(nrbits, value, byteOrder);
            written += nrbits;
        } else {
            throw new IOException(OVERRUN_MESSAGE);
        }
    }

    public void write(byte[] src, int offset, int length) throws IOException {
        if (written + length <= maxBits) {
            channel.write(src, offset, length);
            written += length;
        } else {
            throw new IOException(OVERRUN_MESSAGE);
        }
    }

    public long write(ByteBuffer buffer) throws IOException {
        long written = channel.write(buffer);
        if (written > maxBits - this.written) {
            throw new IOException(OVERRUN_MESSAGE);
        } else {
            this.written += written;
        }
        return written;
    }

    public int getRelativeBitPos() {
        return channel.getRelativeBitPos();
    }

    public void close() throws IOException {
        channel.close();
    }
}
