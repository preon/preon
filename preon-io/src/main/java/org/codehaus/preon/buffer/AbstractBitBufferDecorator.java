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
