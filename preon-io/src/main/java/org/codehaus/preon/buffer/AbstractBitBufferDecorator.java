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

/**
 * An abstract base class for classes wrapping {@link BitBuffer}s to support additional behavior. Implementations need
 * to implement <em>at least</em> {@link #getDelegate()}.
 *
 * @author Wilfred Springer
 */
public abstract class AbstractBitBufferDecorator implements BitBuffer {

    /**
     * Returns the {@link BitBuffer} to which all requests will be delegated.
     *
     * @return The {@link BitBuffer} to which all requests will be delegated.
     */
    public abstract BitBuffer getDelegate();

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#getBitBufBitSize()
     */

    public long getBitBufBitSize() {
        return getDelegate().getBitBufBitSize();
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#getBitPos()
     */

    public long getBitPos() {
        return getDelegate().getBitPos();
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readAsBoolean()
     */

    public boolean readAsBoolean() {
        return getDelegate().readAsBoolean();
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readAsBoolean(long)
     */

    public boolean readAsBoolean(long bitPos) {
        return getDelegate().readAsBoolean(bitPos);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readAsBoolean(org.codehaus.preon.buffer.ByteOrder)
     */

    public boolean readAsBoolean(ByteOrder endian) {
        return getDelegate().readAsBoolean(endian);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readAsBoolean(long, org.codehaus.preon.buffer.ByteOrder)
     */

    public boolean readAsBoolean(long bitPos, ByteOrder endian) {
        return getDelegate().readAsBoolean(bitPos, endian);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readAsByte(int)
     */

    public byte readAsByte(int nrBits) {
        return getDelegate().readAsByte(nrBits);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readAsByte(int, org.codehaus.preon.buffer.ByteOrder)
     */

    public byte readAsByte(int nrBits, ByteOrder endian) {
        return getDelegate().readAsByte(nrBits, endian);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readAsByte(int, long)
     */

    public byte readAsByte(int nrBits, long bitPos) {
        return getDelegate().readAsByte(nrBits, bitPos);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readAsByte(long, int, org.codehaus.preon.buffer.ByteOrder)
     */

    public byte readAsByte(long bitPos, int nrBits, ByteOrder endian) {
        return getDelegate().readAsByte(bitPos, nrBits, endian);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readAsInt(int)
     */

    public int readAsInt(int nrBits) {
        return getDelegate().readAsInt(nrBits);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readAsInt(long, int)
     */

    public int readAsInt(long bitPos, int nrBits) {
        return getDelegate().readAsInt(bitPos, nrBits);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readAsInt(int, org.codehaus.preon.buffer.ByteOrder)
     */

    public int readAsInt(int nrBits, ByteOrder endian) {
        return getDelegate().readAsInt(nrBits, endian);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readAsInt(long, int, org.codehaus.preon.buffer.ByteOrder)
     */

    public int readAsInt(long bitPos, int nrBits, ByteOrder endian) {
        return getDelegate().readAsInt(bitPos, nrBits, endian);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readAsLong(int)
     */

    public long readAsLong(int nrBits) {
        return getDelegate().readAsLong(nrBits);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readAsLong(long, int)
     */

    public long readAsLong(long bitPos, int nrBits) {
        return getDelegate().readAsLong(bitPos, nrBits);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readAsLong(int, org.codehaus.preon.buffer.ByteOrder)
     */

    public long readAsLong(int nrBits, ByteOrder endian) {
        return getDelegate().readAsLong(nrBits, endian);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readAsLong(long, int, org.codehaus.preon.buffer.ByteOrder)
     */

    public long readAsLong(long bitPos, int nrBits, ByteOrder endian) {
        return getDelegate().readAsLong(bitPos, nrBits, endian);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readAsShort(int)
     */

    public short readAsShort(int nrBits) {
        return getDelegate().readAsShort(nrBits);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readAsShort(long, int)
     */

    public short readAsShort(long bitPos, int nrBits) {
        return getDelegate().readAsShort(bitPos, nrBits);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readAsShort(int, org.codehaus.preon.buffer.ByteOrder)
     */

    public short readAsShort(int nrBits, ByteOrder endian) {
        return getDelegate().readAsShort(nrBits, endian);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readAsShort(long, int, org.codehaus.preon.buffer.ByteOrder)
     */

    public short readAsShort(long bitPos, int nrBits, ByteOrder endian) {
        return getDelegate().readAsShort(bitPos, nrBits, endian);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readBits(int)
     */

    public long readBits(int nrBits) {
        return getDelegate().readBits(nrBits);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readBits(long, int)
     */

    public long readBits(long bitPos, int nrBits) {
        return getDelegate().readBits(bitPos, nrBits);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readBits(int, org.codehaus.preon.buffer.ByteOrder)
     */

    public long readBits(int nrBits, ByteOrder endian) {
        return getDelegate().readBits(nrBits, endian);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#readBits(long, int, org.codehaus.preon.buffer.ByteOrder)
     */

    public long readBits(long bitPos, int nrBits, ByteOrder endian) {
        return getDelegate().readBits(bitPos, nrBits, endian);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#setBitPos(long)
     */

    public void setBitPos(long bitPos) {
        getDelegate().setBitPos(bitPos);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.buffer.BitBuffer#slice(long)
     */

    public BitBuffer slice(long length) {
        return getDelegate().slice(length);
    }

    /*
    * (non-Javadoc)
    * @see org.codehaus.preon.buffer.BitBuffer#duplicate()
    */

    public BitBuffer duplicate() {
        return getDelegate().duplicate();
    }

}
