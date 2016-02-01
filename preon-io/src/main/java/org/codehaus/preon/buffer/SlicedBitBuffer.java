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
 * A {@link BitBuffer} that acts upon a slice of the underyling {@link BitBuffer}.
 *
 * @author Wilfred Springer
 */
public class SlicedBitBuffer implements BitBuffer {

    /** The {@link BitBuffer} from which a slice is 'taken'. */
    private BitBuffer delegate;

    /** The start position of the slice (counted in bits from the start of delegate BitBuffer). */
    private long startPos;

    /** The end position of the slice (counted in bits from the start of the delegate BitBuffer). */
    private long endPos;

    /**
     * Constructs a new slice.
     *
     * @param delegate The {@link BitBuffer} to slice.
     * @param length   The lengthof the slize, in bits.
     */
    public SlicedBitBuffer(BitBuffer delegate, long length) {
        this.delegate = delegate;
        this.startPos = delegate.getBitPos();
        this.endPos = startPos + length;
    }
	
	public SlicedBitBuffer(BitBuffer delegate, long length, long startPos) {
        this.delegate = delegate;
        this.startPos = startPos;
        this.endPos = startPos + length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.buffer.BitBuffer#getBitBufBitSize()
     */

    public long getBitBufBitSize() {
        return endPos - startPos;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.buffer.BitBuffer#getBitPos()
     */

    public long getBitPos() {
        return delegate.getBitPos() - startPos;
    }

    /**
     * Validates if it is possible to read the <code>nrBits</code> passed in.
     *
     * @param nrBits The number of bits to read.
     */
    private void assureValidRead(int nrBits) {
        assureValidRead(delegate.getBitPos(), nrBits);
    }

    /**
     * Validates if it is possible to read the <code>nrBits</code>
     *
     * @param bitPos
     * @param nrBits
     */
    private void assureValidRead(long bitPos, int nrBits) {
        if (bitPos > endPos - nrBits) {
            throw new BitBufferUnderflowException(delegate.getBitPos() - startPos, nrBits);
        }
    }

    public boolean readAsBoolean() {
        assureValidRead(1);
        return delegate.readAsBoolean();
    }

    public boolean readAsBoolean(long bitPos) {
        assureValidRead(bitPos + startPos, 1);
        return delegate.readAsBoolean(bitPos + startPos);
    }

    public boolean readAsBoolean(ByteOrder endian) {
        assureValidRead(1);
        return delegate.readAsBoolean(endian);
    }

    public boolean readAsBoolean(long bitPos, ByteOrder endian) {
        assureValidRead(bitPos + startPos, 1);
        return delegate.readAsBoolean(bitPos + startPos, endian);
    }

    public byte readAsByte(int nrBits) {
        assureValidRead(nrBits);
        return delegate.readAsByte(nrBits);
    }

    public byte readAsByte(int nrBits, ByteOrder endian) {
        assureValidRead(nrBits);
        return delegate.readAsByte(nrBits, endian);
    }

    public byte readAsByte(int nrBits, long bitPos) {
        assureValidRead(bitPos + startPos, nrBits);
        return delegate.readAsByte(nrBits, bitPos + startPos);
    }

    public byte readAsByte(long bitPos, int nrBits, ByteOrder endian) {
        assureValidRead(bitPos + startPos, nrBits);
        return delegate.readAsByte(bitPos + startPos, nrBits, endian);
    }

    public int readAsInt(int nrBits) {
        assureValidRead(nrBits);
        return delegate.readAsInt(nrBits);
    }

    public int readAsInt(long bitPos, int nrBits) {
        assureValidRead(bitPos + startPos, nrBits);
        return delegate.readAsInt(bitPos + startPos, nrBits);
    }

    public int readAsInt(int nrBits, ByteOrder endian) {
        assureValidRead(nrBits);
        return delegate.readAsInt(nrBits, endian);
    }

    public int readAsInt(long bitPos, int nrBits, ByteOrder endian) {
        assureValidRead(bitPos + startPos, nrBits);
        return delegate.readAsInt(bitPos + startPos, nrBits, endian);
    }

    public long readAsLong(int nrBits) {
        assureValidRead(nrBits);
        return delegate.readAsLong(nrBits);
    }

    public long readAsLong(long bitPos, int nrBits) {
        assureValidRead(bitPos + startPos, nrBits);
        return delegate.readAsLong(bitPos + startPos, nrBits);
    }

    public long readAsLong(int nrBits, ByteOrder endian) {
        assureValidRead(nrBits);
        return delegate.readAsLong(nrBits, endian);
    }

    public long readAsLong(long bitPos, int nrBits, ByteOrder endian) {
        assureValidRead(bitPos + startPos, nrBits);
        return delegate.readAsLong(bitPos, nrBits, endian);
    }

    public short readAsShort(int nrBits) {
        assureValidRead(nrBits);
        return delegate.readAsShort(nrBits);
    }

    public short readAsShort(long bitPos, int nrBits) {
        assureValidRead(bitPos + startPos, nrBits);
        return delegate.readAsShort(bitPos + startPos, nrBits);
    }

    public short readAsShort(int nrBits, ByteOrder endian) {
        assureValidRead(nrBits);
        return delegate.readAsShort(nrBits, endian);
    }

    public short readAsShort(long bitPos, int nrBits, ByteOrder endian) {
        assureValidRead(bitPos + startPos, nrBits);
        return delegate.readAsShort(bitPos + startPos, nrBits, endian);
    }

    public long readBits(int nrBits) {
        assureValidRead(nrBits);
        return delegate.readBits(nrBits);
    }

    public long readBits(long bitPos, int nrBits) {
        assureValidRead(bitPos + startPos, nrBits);
        return delegate.readBits(bitPos + startPos, nrBits);
    }

    public long readBits(int nrBits, ByteOrder endian) {
        assureValidRead(nrBits);
        return delegate.readBits(nrBits, endian);
    }

    public long readBits(long bitPos, int nrBits, ByteOrder endian) {
        assureValidRead(bitPos + startPos, nrBits);
        return delegate.readBits(bitPos + startPos, nrBits);
    }

    public void setBitPos(long bitPos) {
        if (bitPos > endPos - startPos) {
            throw new BitBufferException("Moving pointer outside of BitBuffer boundaries.");
        } else {
            delegate.setBitPos(bitPos + startPos);
        }

    }

    // JavaDoc inherited

    public BitBuffer slice(long length) {
        return delegate.slice(length);
    }

    // JavaDoc inherited

    public BitBuffer duplicate() {
        return new SlicedBitBuffer(delegate.duplicate(), endPos - startPos, startPos);
    }

    public ByteBuffer readAsByteBuffer(int length) throws BitBufferUnderflowException {
        if (delegate.getBitPos() + length * 8 > endPos) {
            throw new BitBufferUnderflowException(delegate.getBitPos(), length * 8);
        } else {
            return delegate.readAsByteBuffer(length);
        }
    }

    public ByteBuffer readAsByteBuffer() {
        return delegate.readAsByteBuffer();
    }

    public long getActualBitPos() {
        return delegate.getActualBitPos();
    }

}
