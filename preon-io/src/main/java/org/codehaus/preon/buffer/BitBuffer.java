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
package org.codehaus.preon.buffer;

import java.nio.ByteBuffer;

/**
 * The interface providing methods reading a given number of bits from the byte buffer and conveting them to the byte,
 * short, int, long (all unsigned) and also to the boolean type values. It is possible to start reading from any
 * location in the byte buffer by explicitly giving the bit position of the first bit to read. The API also supports
 * reading values one by one by setting the position of the first bit to read to the internal bit pointer position. Both
 * little and big endian reading formats are supported.
 * <p/>
 * In case of byte base reading i.e. given bits fit completely all bits in bytes, the big endian and little endian
 * implementations differ only in the reversed bytes order. Otherwise, the implementation is more complicated - in order
 * to read the given bits, the buffering bytes containing these bits are stored in internal buffer. For the little
 * endian implementation the offset to the closest given bit is calucated from the least significant bit in that
 * internal buffer, but for the big endian from the most significant bit (e.g. for the byte: 11010011 and bit position =
 * 1 and number of bits to read = 3, the given bits would be 101 for big endian and 001 for little endian, what gives
 * the values 5 and 1 respectively.
 *
 * @author Wilfred Springer
 */
public interface BitBuffer {

    /**
     * Set bit pointer position in the bit buffer.
     *
     * @param bitPos bit pointer position to set
     */
    void setBitPos(long bitPos);

    /**
     * Retun bit pointer position
     *
     * @return bit pointer position
     */
    long getBitPos();

    /**
     * Return the size (in bits) of the bit buffer
     *
     * @return bit size of the bit buffer
     */
    long getBitBufBitSize();

    /**
     * Read specified number of bits (max 64) starting from the current bit pointer position in the big endian order,
     * return the value as <b>long</b> (signed) and move the bit pointer the given number of bits ahead. After reading
     * the pointer will indicate the position of the first bit that has not been read yet.
     *
     * @param nrBits number of bits to read
     * @return value represented by specified bits
     */
    long readBits(int nrBits);

    /**
     * Read specified number of bits (max 64) starting from the given bit position in the big endian order, return the
     * value as <b>long</b> (signed) and move the bit pointer the given number of bits ahead. After reading the pointer
     * will indicate the position of the first bit that has not been read yet.
     *
     * @param bitPos position of the first bit to read in the bit buffer
     * @param nrBits number of bits to read
     * @return value represented by specified bits
     */
    long readBits(long bitPos, int nrBits);

    /**
     * Read specified number of bits (max 64) starting from the current bit pointer position in either the little- or
     * big-endian order, return the value as <b>long</b> (signed) and move the bit pointer the given number of bits
     * ahead. After reading the pointer will indicate the position of the first bit that has not been read yet.
     *
     * @param nrBits number of bits to read
     * @param endian order of reading bytes (either Endian.Big or Endian.Little)
     * @return value represented by specified bits
     */
    long readBits(int nrBits, ByteOrder endian);

    /**
     * Read specified number of bits (max 64) starting from the given bit position in either the little- or big-endian
     * order, return the value as <b>long</b> (signed) and move the bit pointer the given number of bits ahead. After
     * reading the pointer will indicate the position of the first bit that has not been read yet.
     *
     * @param bitPos position of the first bit to read in the bit buffer
     * @param nrBits number of bits to read
     * @param endian order of reading bytes (either Endian.Big or Endian.Little)
     * @return value represented by specified bits
     */
    long readBits(long bitPos, int nrBits, ByteOrder endian);

    // boolean

    /**
     * Read one specified bit starting from the current bit pointer position in the big endian order, return the value
     * as <b>boolean</b> and move the bit pointer one bit ahead. After reading the pointer will indicate the position of
     * the first bit that has not been read yet.
     *
     * @return value represented by specified bit
     */
    boolean readAsBoolean();

    /**
     * Read one specified bit starting from the given bit position in the big endian order, return the value as
     * <b>boolean</b> and move the bit pointer one bit ahead. After reading the pointer will indicate the position of
     * the position of the first bit that has not been read yet.
     *
     * @param bitPos position of the bit to read in the bit buffer
     * @return value represented by specified bit
     */
    boolean readAsBoolean(long bitPos);

    /**
     * Read one specified bit starting from the current bit pointer position in either the little- or big-endian order,
     * move the value as <b>boolean</b> and move the bit pointer one bit ahead. After reading the pointer will indicate
     * the position of the first bit that has not been read yet.
     *
     * @param endian order of reading bytes (either Endian.Big or Endian.Little)
     * @return value represented by specified bit
     */
    boolean readAsBoolean(ByteOrder endian);

    /**
     * Read specified bit starting from the given bit position in either the little- or big-endian order, return the
     * value as <b>boolean</b> and one bit ahead. After reading the pointer will indicate the position of the first bit
     * that has not been read yet.
     *
     * @param bitPos position of the bit to read in the bit buffer
     * @param endian order of reading bytes (either Endian.Big or Endian.Little)
     * @return value represented by specified bit
     */
    boolean readAsBoolean(long bitPos, ByteOrder endian);

    // signed byte

    /**
     * Read specified number of bits (max 8) starting from the current bit pointer position in the big endian order,
     * return the value as <b>byte</b> (signed) and move the bit pointer the given number of bits ahead. After reading
     * the pointer will indicate the position of the position of the first bit that has not been read yet.
     *
     * @param nrBits number of bits to read
     * @return value represented by specified bits
     */
    byte readAsByte(int nrBits);

    /**
     * Read specified number of bits (max 8) starting from the current bit pointer position in either the little- or
     * big-endian order, return the value as <b>byte</b> (signed) and move the bit pointer the given number of bits
     * ahead. After reading the pointer will indicate the position of the first bit that has not been read yet.
     *
     * @param nrBits number of bits to read
     * @param endian order of reading bytes (either Endian.Big or Endian.Little)
     * @return value represented by specified bits
     */
    byte readAsByte(int nrBits, ByteOrder endian);

    /**
     * Read specified number of bits (max 8) starting from the given bit position in the big endian order, return the
     * value as <b>byte</b> (signed) and move the bit pointer the given number of bits ahead from its previous position
     * in the bit buffer. After reading the pointer will indicate the position of the first bit that has not been read
     * yet.
     *
     * @param nrBits number of bits to read
     * @param bitPos position of the first bit to read in the bit buffer
     * @return value represented by specified bits
     */
    byte readAsByte(int nrBits, long bitPos);

    /**
     * Read specified number of bits (max 8) starting from the given bit position in either the little- or big-endian
     * order, return the value as <b>byte</b> (signed) and move the bit pointer the given number of bits ahead. After
     * reading the pointer will indicate the first bit that has not been read yet.
     *
     * @param bitPos position of the first bit to read in the bit buffer
     * @param nrBits number of bits to read
     * @param endian order of reading bytes (either Endian.Big or Endian.Little)
     * @return value represented by specified bits
     */
    byte readAsByte(long bitPos, int nrBits, ByteOrder endian);

    // signed short

    /**
     * Read specified number of bits (max 16) starting from the current bit pointer position in the big endian order,
     * return the value as <b>short</b> (signed) and move the bit pointer the given number of bits ahead. After reading
     * the pointer will indicate the position of the first bit that has not been read yet.
     *
     * @param nrBits number of bits to read
     * @return value represented by specified bits
     */
    short readAsShort(int nrBits);

    /**
     * Read specified number of bits (max 16) starting from the given bit position in the big endian order, return the
     * value as <b>short</b> (signed) and move the bit pointer the given number of bits ahead. After reading the pointer
     * will indicate the position of the first bit that has not been read yet.
     *
     * @param bitPos position of the first bit to read in the bit buffer
     * @param nrBits number of bits to read
     * @return value represented by specified bits
     */
    short readAsShort(long bitPos, int nrBits);

    /**
     * Read specified number of bits (max 16) starting from the current bit pointer position in either the little- or
     * big-endian order, return the value as <b>short</b> (signed) and move the bit pointer the given number of bits
     * ahead. After reading the pointer will indicate the first bit that has not been read yet.
     *
     * @param nrBits number of bits to read
     * @param endian order of reading bytes (either Endian.Big or Endian.Little)
     * @return value represented by specified bits
     */
    short readAsShort(int nrBits, ByteOrder endian);

    /**
     * Read specified number of bits (max 16) stating from the given bit position in either little- or big-endian order,
     * return the value as <b>short</b> (signed) and move the bit pointer the given number of bits ahead. After reading
     * the pointer will indicate the position of the first bit that has not been read yet.
     *
     * @param bitPos position of the first bit to read in the bit buffer
     * @param nrBits number of bits to read
     * @param endian order of reading bytes (either Endian.Big or Endian.Little)
     * @return value represented by specified bits
     */
    short readAsShort(long bitPos, int nrBits, ByteOrder endian);

    // signed int

    /**
     * Read specified number of bits (max 32) starting from the current bit pointer position in the big endian order,
     * return the value as <b>int</b> (signed) and move the bit pointer the given number of bits ahead. After reading
     * the pointer will indicate the position of the first bit that has not been read yet.
     *
     * @param nrBits number of bits to read
     * @return value represented by specified bits
     */
    int readAsInt(int nrBits);

    /**
     * Read specified number of bits (max 32) starting from the given bit position in the big endian order, return the
     * value as <b>int</b> (signed) and move the bit pointer the given number of bits ahead. After reading the pointer
     * will indicate the position of the first bit that has not been read yet.
     *
     * @param bitPos position of the first bit to read in the bit buffer
     * @param nrBits number of bits to read
     * @return value represented by specified bits
     */
    int readAsInt(long bitPos, int nrBits);

    /**
     * Read specified number of bits (max 32) starting from the current bit pointer position in either the little- or
     * big-endian order, return the value as <b>int</b> (signed) and move the bit pointer the given number of bits
     * ahead. After reading the pointer will indicate the position of the first bit that has not been read yet.
     *
     * @param nrBits number of bits to read
     * @param endian order of reading bytes (either Endian.Big or Endian.Little)
     * @return value represented by specified bits
     */
    int readAsInt(int nrBits, ByteOrder endian);

    /**
     * Read specified number of bits (max 32) stating from the given bit position in either little- or big-endian order,
     * return the value as <b>int</b> (signed) and move the bit pointer the given number of bits ahead. After reading
     * the pointer will indicate the position of the first bit that has not been read yet.
     *
     * @param bitPos position of the first bit to read in the bit buffer
     * @param nrBits number of bits to read
     * @param endian order of reading bytes (either Endian.Big or Endian.Little)
     * @return value represented by specified bits
     */
    int readAsInt(long bitPos, int nrBits, ByteOrder endian);

    // signed long

    /**
     * Read specified number of bits (max 64) starting from the current bit pointer position in the big endian order,
     * return the value as <b>long</b> (signed) and move the bit pointer the given number of bits ahead. After reading
     * the pointer will indicate the position of the first bit that has not been read yet.
     *
     * @param nrBits number of bits to read
     * @return value represented by specified bits
     */
    long readAsLong(int nrBits);

    /**
     * Read specified number of bits (max 64) starting from the given bit position in the big endian order, return the
     * value as <b>long</b> (signed) and move the bit pointer the given number of bits ahead. After reading the pointer
     * will indicate the position of the first bit that has not been read yet. The bytes are read in the big-endian
     * order.
     *
     * @param bitPos position of the first bit to read in the bit buffer
     * @param nrBits number of bits to read
     * @return value represented by specified bits
     */
    long readAsLong(long bitPos, int nrBits);

    /**
     * Read specified number of bits (max 64) starting from the current bit pointer position in either the little- or
     * big-endian order, return the value as <b>long</b> (signed) and move the bit pointer the given number of bits
     * ahead. After reading the pointer will indicate the position of the first bit that has not been read yet.
     *
     * @param nrBits number of bits to read
     * @param endian order of reading bytes (either Endian.Big or Endian.Little)
     * @return value represented by specified bits
     */
    long readAsLong(int nrBits, ByteOrder endian);

    /**
     * Read specified number of bits (max 64) stating from the given bit position in either little- or big-endian order,
     * return the value as <b>long</b> (signed) and move the bit pointer the given number of bits ahead. After reading
     * the pointer will indicate the first bit that has not been read yet.
     *
     * @param bitPos position of the first bit to read in the bit buffer
     * @param nrBits number of bits to read
     * @param endian order of reading bytes (either Endian.Big or Endian.Little)
     * @return value represented by specified bits
     */
    long readAsLong(long bitPos, int nrBits, ByteOrder endian);

    /**
     * Returns a new BitBuffer, starting at {@link #getBitPos()}, with the given <code>length</code>. Moves the current
     * position in this BitBuffer to {@link #getBitPos()} <code> + length</code>. Note that each {@link BitBuffer} is
     * assumed to be completely independent of the other, even though both are backed by the same data.
     *
     * @param length The number of bits to be included in the slice.
     * @throws BitBufferUnderflowException If the number of bits would be out of reach for the {@link BitBuffer}.
     */
    BitBuffer slice(long length) throws BitBufferUnderflowException;

    /**
     * Duplicates the current {@link BitBuffer}. The new {@link BitBuffer} is guaranteed to have its own index to the
     * current position. The only thing the copy shares in common with its origin from which it was duplicated is the
     * backing data structure. Apart from that, they are assumed to be completely independent.
     *
     * @return A duplicate of the {@link BitBuffer}.
     */
    BitBuffer duplicate();

    /**
     * Returns a {@link ByteBuffer} creating a view of the contents of the BitBuffer, representing the data as bytes. It
     * will move the current position in this {@link BitBuffer} to <code>length * 8</code>.
     *
     * @param length The number of <em>bytes</em> to be exposed throught the {@link ByteBuffer}.
     * @return A ByteBuffer providing a byte view on a slice of the data underneath this {@link BitBuffer}.
     * @throws BitBufferUnderflowException If the number bytes addresses a position out of reach for this {@link
     *                                     BitBuffer}.
     */
    ByteBuffer readAsByteBuffer(int length) throws BitBufferUnderflowException;

    /**
     * Returns the contents of the underlying collection of bytes a {@link java.nio.ByteBuffer}.
     *
     */
    ByteBuffer readAsByteBuffer();

    /**
     * Returns the actual position in the underlying data representation.
     *
     * @return The actual position in the underlying related to the underlying data representation.
     */
    long getActualBitPos();

}
