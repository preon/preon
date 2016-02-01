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
