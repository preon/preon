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
