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

import java.lang.ref.SoftReference;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A <i>reference</i> to a an instance of type {@link T}, to be dynamically loaded, on demand, whenever the reference is
 * resolved by calling {@link #get()}; once loaded, the reference will hold on to the instance of {@link T} until the
 * garbage collector collects it. After that, the instance of {@link T} will - again - be recreated, whenever required.
 *
 * @author Wilfred Springer
 * @param <T> The type of object referenced.
 */
public class LazyLoadingReference<T> {

    /**
     * An {@link AtomicReference} to a {@link SoftReference} to a {@link Future} producing an instance of {@link T}. The
     * {@link AtomicReference} makes sure the value can be updated atomically. The {@link SoftReference} makes sure the
     * garbage collector will release the reference if it needs more memory. The {@link Future} makes sure that - once
     * the value has been set, all calls to obtain the data will be blocked until it arrives.
     */
    private final AtomicReference<SoftReference<Future<T>>> reference = new AtomicReference<SoftReference<Future<T>>>();

    /** A {@link Loader} of {@link T}. */
    private final Loader<T> loader;

    /**
     * Constructs a new reference, accepting the {@link Loader} that will lazily load the data when required.
     *
     * @param loader The {@link Loader} loading the data.
     */
    public LazyLoadingReference(Loader<T> loader) {
        this.loader = loader;
    }

    /**
     * Returns an instance of {@link T}, lazily constructed on demand using the {@link #loader Loader}.
     *
     * @return The referenced instance of {@link T}.
     * @throws InterruptedException When blocking call is interrupted.
     * @throws ExcecutionException  When the {@link Loader} threw an exception trying to load the data.
     */
    public T get() throws InterruptedException, ExecutionException {

        // We may need to try this a couple of times
        while (true) {

            // Let's first get the current SoftReference
            SoftReference<Future<T>> softReference = reference.get();

            // And assume this SoftReference actually resolves in a Future
            boolean validSoftReference = true;

            // If the SoftReference is null
            if (softReference == null) {

                // Then we need to prepare loading the data:
                // (1) Define what needs to happen if we need to load the data
                Callable<T> eval = new Callable<T>() {

                    public T call() throws Exception {
                        return loader.load();
                    }

                };

                // (2) Create a FutureTask, that will eventually hold the result
                FutureTask<T> task = new FutureTask<T>(eval);

                // (3) Create a replacement SoftReference, pointing to the value
                // to be calculated.
                softReference = new SoftReference<Future<T>>(task);

                // Now try to set the AtomicReference, if it's still null
                if (validSoftReference = reference.compareAndSet(null,
                        softReference)) {

                    // We've set the AtomicReference, now let's make sure there
                    // is a value held by the SoftReference.
                    task.run();

                }

                // Otherwise, if the AtomicReference has been populated
                // meanwhile, we are just going to drop the FutureTask and
                // SoftReference created.
            }

            // In case we now have a valid SoftReference
            if (validSoftReference) {

                try {

                    // Obtain the Future
                    Future<T> future = softReference.get();

                    // ... and if it is not null
                    if (future != null) {

                        // ... we can return the value
                        return future.get();

                    } else {

                        // ... otherwise it's obviously a collected reference.
                        // In that case, we need to make sure it's getting
                        // replaced by a new SoftReference if we reenter the
                        // loop.
                        reference.compareAndSet(softReference, null);
                    }

                } catch (CancellationException e) {

                    // If Future.get throws a CancellationException, we need to
                    // set the AtomicReference to null.
                    reference.compareAndSet(softReference, null);

                }
            }
        }
    }

    /**
     * The interface to be implemented when loading instances of {@link T}.
     *
     * @param <T> The type of object to be loaded.
     */
    public interface Loader<T> {

        /**
         * Loads the instance of {@link T}.
         *
         * @return The instance of {@link T} loaded.
         * @throws Exception If the {@link Loader} fails to
         */
        T load() throws Exception;

    }

}