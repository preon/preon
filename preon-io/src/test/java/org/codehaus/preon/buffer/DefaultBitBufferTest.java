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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;


public class DefaultBitBufferTest extends TestCase {

    private DefaultBitBuffer bitBuffer;

    private ByteBuffer byteBuffer;

    private int fileSize;

    @Override
    protected void setUp() throws Exception {
        byteBuffer = getByteBuffer("testFile");
        bitBuffer = new DefaultBitBuffer(byteBuffer);
        fileSize = byteBuffer.capacity();
    }

    public void testReadBits() {
        // Ascii - dec - bin
        // File: 4 4 - 52 52 - 0100 0011 0100 0011

        // 4 - 52 - 0011 0100
        assertEquals(67, bitBuffer.readBits(0, 8));

        // 4 - 52 - 0011 0100
        assertEquals(52, bitBuffer.readBits(4, 8));

        // 4 - 52 - 11 0100
        assertEquals(52, bitBuffer.readBits(6, 6));

        // ? - 2 - 10
        assertEquals(2, bitBuffer.readBits(1, 2));

        // read the whole file, byte by bytes
        byte[] bytesReadByApp = new byte[fileSize];
        for (int i = 0; i < fileSize; i++)
            bytesReadByApp[i] = (byte) bitBuffer.readBits(i * 8, 8);

        byte[] bytesReadDirectly = new byte[fileSize];
        byteBuffer.asReadOnlyBuffer().get(bytesReadDirectly);

        assertTrue(java.util.Arrays.equals(bytesReadDirectly, bytesReadByApp));

        // read long
        assertEquals(byteBuffer.getLong(), bitBuffer.readBits(0, 64));
    }

    public void testLittleEndianBitOffset() {
        bitBuffer = new DefaultBitBuffer(ByteBuffer.wrap(new byte[]{3, 0, 0,
                0}));

        assertEquals(1, bitBuffer.readAsInt(1, ByteOrder.LittleEndian));
        assertEquals(1, bitBuffer.readAsInt(1, ByteOrder.LittleEndian));
        assertEquals(0, bitBuffer.readAsInt(1, ByteOrder.LittleEndian));

        assertEquals(1, bitBuffer.readAsInt(0, 1, ByteOrder.LittleEndian));
        assertEquals(3, bitBuffer.readAsInt(0, 2, ByteOrder.LittleEndian));
        assertEquals(3, bitBuffer.readAsInt(0, 10, ByteOrder.LittleEndian));

    }

    public void testBigEndianBitOffset() {

        // 11001010
        // 10010110
        bitBuffer = new DefaultBitBuffer(ByteBuffer.wrap(new byte[]{
                (byte) 0xCA, (byte) 0x96, 0, 0}));

        assertEquals(1, bitBuffer.readAsInt(1, ByteOrder.BigEndian));
        assertEquals(1, bitBuffer.readAsInt(1, ByteOrder.BigEndian));
        assertEquals(0, bitBuffer.readAsInt(1, ByteOrder.BigEndian));

        assertEquals(1, bitBuffer.readAsInt(0, 1, ByteOrder.BigEndian));
        assertEquals(3, bitBuffer.readAsInt(0, 2, ByteOrder.BigEndian));
        assertEquals(810, bitBuffer.readAsInt(0, 10, ByteOrder.BigEndian));

    }

    public void testReadBitsSequentially() {
        byte[] bytesReadByApp = new byte[fileSize];
        for (int i = 0; i < fileSize; i++)
            bytesReadByApp[i] = (byte) bitBuffer.readBits(8);

        byte[] bytesReadDirectly = new byte[fileSize];
        byteBuffer.asReadOnlyBuffer().get(bytesReadDirectly);

        assertTrue(java.util.Arrays
                .equals(bytesReadDirectly, bytesReadDirectly));

    }

    public void testBitOneAfterAnother() {
        assertEquals(0, bitBuffer.readBits(1));
        assertEquals(1, bitBuffer.readBits(1));
        assertEquals(0, bitBuffer.readBits(1));
        assertEquals(0, bitBuffer.readBits(1));
        assertEquals(0, bitBuffer.readBits(1));
        assertEquals(0, bitBuffer.readBits(1));
        assertEquals(true, bitBuffer.readAsBoolean());
        assertEquals(true, bitBuffer.readAsBoolean());
    }

    public void testCompleteBytes() {
        DefaultBitBuffer bitBuffer;
        bitBuffer = new DefaultBitBuffer(ByteBuffer.wrap(new byte[]{0x01,
                0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD,
                (byte) 0xEF}));
        assertEquals(0x0123456789ABCDEFL, bitBuffer.readBits(64));

        bitBuffer = new DefaultBitBuffer(ByteBuffer.wrap(new byte[]{
                (byte) 0xEF, (byte) 0xCD, (byte) 0xAB, (byte) 0x89, 0x67, 0x45,
                0x23, 0x01}));
        assertEquals(0x0123456789ABCDEFL, bitBuffer.readBits(64, ByteOrder.LittleEndian));
    }

    public void testReadBeyondEnd() {
        DefaultBitBuffer buffer = new DefaultBitBuffer(ByteBuffer
                .wrap(new byte[]{1, 2, 3}));
        buffer.readAsByte(8);
        buffer.readAsByte(8);
        buffer.readAsByte(8);
        try {
            buffer.readAsByte(8);
            fail("Expecting exception while reading beyond end of buffer.");
        } catch (BitBufferUnderflowException oore) {
            // Yep, we got the exception we expected.
        }
    }

    public void testReadAsByteBuffer() throws Exception {

        DefaultBitBuffer buffer = new DefaultBitBuffer(ByteBuffer
                .wrap(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}));

        assertEquals(1, buffer.readAsByte(8));
        assertEquals(2 * 256 + 3, buffer.readAsShort(16));

        ByteBuffer byteBuffer = buffer.readAsByteBuffer(5);
        byte[] bufferArray = new byte[5];

        byteBuffer.get(bufferArray);

        assertEquals(4, bufferArray[0]);
        assertEquals(8, bufferArray[4]);

        assertEquals(9, buffer.readAsByte(8));
        assertEquals(10, buffer.readAsByte(8));
    }

    public void testReadAsByteBufferWithBitAlignments() throws Exception {

        DefaultBitBuffer buffer = new DefaultBitBuffer(ByteBuffer
                .wrap(new byte[]{1, 2, 3, 4}));

        buffer.readAsByte(7);

        try {
            ByteBuffer byteBuffer = buffer.readAsByteBuffer(2);
            fail();
        } catch (BitBufferException bbe) {
            // Correct. The readAsByteBuffer method does not handle not 8-bit
            // alignment buffers
        }

        // Bit alignment compensation
        buffer = new DefaultBitBuffer(ByteBuffer
                .wrap(new byte[]{1, 2, 3, 4}));

        // 7 + 9 = 16
        buffer.readAsByte(7);
        buffer.readAsInt(9);

        buffer.readAsByteBuffer(2);
    }

    private ByteBuffer getByteBuffer(String resource) throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream in = classLoader.getResourceAsStream(resource);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(in, out);
        return ByteBuffer.wrap(out.toByteArray());
    }

    public void testReadingIntegers() {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{0x01 // 0000 0001
                , 0x02 // 0000 00010
                , 0x03 // 0000 00011
                , 0x04 // 0000 00100
                , 0x05 // 0000 00101
        });
        BitBuffer bitBuffer = new DefaultBitBuffer(buffer);
        bitBuffer.setBitPos(0);
        assertEquals(1, bitBuffer.readAsInt(8));
        bitBuffer.setBitPos(0);
        assertEquals(0, bitBuffer.readAsInt(7));
        bitBuffer.setBitPos(1);
        assertEquals(2, bitBuffer.readAsInt(8));
        bitBuffer.setBitPos(0);
        assertEquals(0x0102, bitBuffer.readAsInt(16));
        bitBuffer.setBitPos(0);
        assertEquals(0x0102, bitBuffer.readAsInt(16, ByteOrder.BigEndian));
        bitBuffer.setBitPos(0);
        assertEquals(0x0201, bitBuffer.readAsInt(16, ByteOrder.LittleEndian));
        bitBuffer.setBitPos(1);
        assertEquals(0x0102 << 1, bitBuffer.readAsInt(16));
        bitBuffer.setBitPos(1);
        assertEquals(bitBuffer.readAsInt(1, 5, ByteOrder.BigEndian), bitBuffer
                .readAsInt(1, 5, ByteOrder.LittleEndian));
        assertEquals(bitBuffer.readAsShort(1, 9, ByteOrder.BigEndian), bitBuffer
                .readAsInt(1, 9, ByteOrder.BigEndian));
        assertEquals(bitBuffer.readAsByte(3, 7, ByteOrder.BigEndian), bitBuffer
                .readAsInt(3, 7, ByteOrder.BigEndian));
    }

    public void testReading1() {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{0x00, 0x00, 0x00, 0x01});
        BitBuffer bitBuffer = new DefaultBitBuffer(buffer);
        assertEquals(1, bitBuffer.readAsInt(32));
    }
}
