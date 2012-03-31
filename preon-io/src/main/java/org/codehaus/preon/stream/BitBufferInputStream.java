package org.codehaus.preon.stream;

import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.buffer.BitBufferUnderflowException;

import java.io.InputStream;

/**
 * Wraps a {@link BitBuffer} as an {@link InputStream} that
 * reads (unsigned) bytes directly from the underlying BitBuffer
 * by calling its readAsByte(8) method.
 */
public class BitBufferInputStream extends InputStream {
    private final BitBuffer bitBuffer;

    public BitBufferInputStream(final BitBuffer bitBuffer) {
        this.bitBuffer = bitBuffer;
    }

    @Override
    public int read() {
        try {
            return bitBuffer.readAsByte(8) & 0xFF; // Converts to unsigned
        }
        catch (BitBufferUnderflowException ignored) {
            return -1;
        }
    }
}
