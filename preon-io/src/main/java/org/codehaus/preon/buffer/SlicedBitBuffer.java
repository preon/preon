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
        return new SlicedBitBuffer(delegate.duplicate(), endPos - startPos);
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
