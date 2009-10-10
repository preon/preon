package nl.flotsam.preon.channel;

import java.io.IOException;
import java.nio.ByteOrder;
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
    void write(ByteBuffer buffer) throws IOException;

    /** Returns the position of the bit pointer in the current byte. */
    int getRelativeBitPos();

    /** Closes the channel. */
    void close() throws IOException;

}
