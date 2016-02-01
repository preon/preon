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

import java.lang.reflect.AnnotatedElement;


/**
 * A factory for {@link Codec} decorators.
 *
 * @author Wilfred Springer
 */
public interface CodecDecorator {

    /**
     * Attempts to wrap the {@link Codec} passed in with new decorator. Note that if the factory fails to create the
     * decorator, it is expected to return the original {@link Codec}.
     *
     * @param <T>      The type of {@link Codec} to be decorated.
     * @param codec    The object that needs to be decorated.
     * @param metadata Metadata for the type.
     * @param type     The type of {@link Codec} to be be decorated.
     * @param context  TODO
     * @return A decorated {@link Codec} or the original {@link Codec}.
     */
    <T> Codec<T> decorate(Codec<T> codec, AnnotatedElement metadata,
                          Class<T> type, ResolverContext context);

}
