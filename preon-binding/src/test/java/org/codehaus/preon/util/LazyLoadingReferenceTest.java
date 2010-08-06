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
