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
package org.codehaus.preon;

/**
 * The interface to be implemented by objects with the capability to create default instances of arbitrary types being
 * passed in. This interface was introduced to allow {@link Codec Codecs} to create instances of a certain type without
 * having to worry on how these instances need to be constructed. <p/> <p> This is needed in particular when the {@link
 * Codec Codecs} need to create instances of (non-static) inner classes. In those cases, the new instances need to be
 * constructed by passing a reference to the outer instance. Since {@link Codec Codecs} are not aware of the outer
 * instance, they would never be able to create instances of the inner class. By introducing the {@link Builder} we are
 * able to pass in a context from the owning {@link Codec} with the capability to create instances of inner classes.
 * </p>
 *
 * @author Wilfred Springer
 */
public interface Builder {

    /**
     * Creates a default instance of T.
     *
     * @param type
     *            The type of instance desired.
     * @param <T>
     *            The type of instance we need.
     * @return An instance of T.
     * @throws InstantiationException
     *             If we can't instantiate the given type.
     * @throws IllegalAccessException
     *             If we are not allowed to instantiate the given type.
     */
    <T> T create(Class<T> type) throws InstantiationException, IllegalAccessException;

}
