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


import edu.umd.cs.mtc.MultithreadedTest;
import edu.umd.cs.mtc.TestFramework;
import junit.framework.TestCase;

/**
 * A test suite testing the threadsafe implementation of the {@link BitBuffer}.
 *
 * @author Wilfred Springer
 */
public class ConcurrentBitBufferTest extends TestCase {

    /**
     * Tests that we can safely call the methods of the {@link BitBuffer} concurrently.
     *
     * @throws Throwable If the {@link MultithreadedTest} is throwing exceptions.
     */
    public void testConcurrentAccess() throws Throwable {
        TestFramework.runManyTimes(new Test(), 10);
    }

    /** Tests concurrent access of the {@link ConcurrentBitBuffer} using two threads. */
    private static class Test extends MultithreadedTest {

        /** The {@link ConcurrentBitBuffer} that will be tested. */
        private ConcurrentBitBuffer bitBuffer;

        public void initialize() {
            ByteBuffer byteBuffer = ByteBuffer.allocate(20);
            byte[] data = byteBuffer.array();
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) i;
            }
            bitBuffer = new ConcurrentBitBuffer(
                    new DefaultBitBuffer(byteBuffer));
            bitBuffer.setBitPos(0);
        }

        public void thread1() {
            assertEquals(0, bitBuffer.readAsByte(8));

            // Wait for thread1 having read the first byte.
            waitForTick(2);

            assertEquals(1, bitBuffer.readAsByte(8));
        }

        public void thread2() {

            // Waiting for thread1 having read the first byte
            waitForTick(1);

            // For this thread, the underlying BitBuffer and ByteBuffer pointers
            // shouldn't have moved, and move to position 4.
            assertEquals(0, bitBuffer.readAsByte(8));
            bitBuffer.setBitPos(32);

            // Wait for thread1 having read the next byte.
            waitForTick(3);

            // Verify that we are still reading the right byte, even though
            // thread1 moved the BitBuffer and ByteBuffer pointers.
            assertEquals(4, bitBuffer.readAsByte(8));
        }

    }

}
