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

import junit.framework.TestCase;

public class ByteOrderingTest extends TestCase {

    public void testRightToLeftBigEndian() {
        byte[] buffer = {0x12 // 0001 0010
                , 0x34 // 0011 0100
                , 0x45 // 0100 0101
        };
        assertEquals(0x12, readAsLongBigEndianRL(0, 0, buffer, 5));
        assertEquals(0x09, readAsLongBigEndianRL(0, 1, buffer, 5));
        assertEquals(0x41, readAsLongBigEndianRL(0, 4, buffer, 8));
        assertEquals(0x4d0, readAsLongBigEndianRL(0, 6, buffer, 11));
        buffer = new byte[]{0x20 // 0010 0000
                , (byte) 0x88 // 1000 1000
                , 0x41 // 0100 0001
                , (byte) 0x8a // 1000 1001
        };
        assertEquals(0, readAsLongBigEndianRL(0, 0, buffer, 5));
        assertEquals(1, readAsLongBigEndianRL(0, 5, buffer, 5));
        assertEquals(2, readAsLongBigEndianRL(1, 2, buffer, 5));
        assertEquals(3, readAsLongBigEndianRL(1, 7, buffer, 5));
    }

    private long readAsLongBigEndianRL(int bytePos, int bitPos, byte[] buffer,
                                       int nrBits) {
        // Long implies less than 64 bits
        assert nrBits < 64;
        // The number of bits available must be higher than requested
        assert (buffer.length - bytePos) * 8 - bitPos >= 0;

        // Byte index
        int index = 0;

        // Process first chunk
        long result = (0xFF & buffer[bytePos]) >> bitPos;
        if (bitPos + nrBits < 8) {
            result = result & (0xFF >> (8 - nrBits));
        }
        nrBits -= 8 - bitPos;

        // Process middle bytes
        while (nrBits > 8) {
            index++;
            result |= (0xFF & buffer[bytePos + index]) << (index * 8 - bitPos);
            nrBits -= 8;
        }

        // Process last byte
        index++;
        if (nrBits > 0) {
            result |= ((0xFF >> (8 - nrBits)) & buffer[bytePos + index]) << (index * 8 - bitPos);
        }
        return result;
    }

    private static int bytesRequired(long bitPos, int nrBits) {
        return (int) (((bitPos % 8) + nrBits + 7) / 8);
    }

}
