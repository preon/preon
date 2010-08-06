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
