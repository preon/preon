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
package org.codehaus.preon.util;

import java.util.concurrent.ExecutionException;

import org.codehaus.preon.util.LazyLoadingReference.Loader;


import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;
import edu.umd.cs.mtc.MultithreadedTest;
import edu.umd.cs.mtc.TestFramework;
import junit.framework.TestCase;

public class LazyLoadingReferenceTest extends TestCase {

    public void testLoading() throws InterruptedException, ExecutionException {
        LazyLoadingReference<String> reference = new LazyLoadingReference<String>(
                new LazyLoadingReference.Loader<String>() {

                    public String load() {
                        return "abc";
                    }

                });
        assertEquals("abc", reference.get());
    }

    public void testConcurrentAccess() throws Throwable {
        TestFramework.runManyTimes(new ConcurrentTest(), 100);
    }

    public static class ConcurrentTest extends MultithreadedTest {

        private LazyLoadingReference<Integer> reference;

        private AtomicInteger counter = new AtomicInteger();

        private int i = 0;

        public void initialize() {
            reference = new LazyLoadingReference<Integer>(new IntegerLoader());
            counter.set(0);
        }

        public void thread1() throws InterruptedException, ExecutionException {
            assertEquals(1, reference.get().intValue());
        }

        public void thread2() throws InterruptedException, ExecutionException {
            assertEquals(1, reference.get().intValue());
        }

        public void thread3() throws InterruptedException, ExecutionException {
            assertEquals(1, reference.get().intValue());
        }

        private class IntegerLoader implements Loader<Integer> {

            public Integer load() {
                return counter.addAndGet(1);
            }

        }


    }

}
