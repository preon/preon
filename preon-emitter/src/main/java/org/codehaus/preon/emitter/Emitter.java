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
package org.codehaus.preon.emitter;

import org.codehaus.preon.Codec;
import org.codehaus.preon.buffer.BitBuffer;

/** The object that will generate the messages. Receives events for anything of interest. */
public interface Emitter {

    /**
     * The operation called whenever a {@link org.codehaus.preon.Codec} kicks in.
     *
     * @param codec    The {@link org.codehaus.preon.Codec} called.
     * @param position The position in the {@link org.codehaus.preon.buffer.BitBuffer}.
     * @param buffer
     */
    void markStart(Codec<?> codec, long position, BitBuffer buffer);

    /**
     * The operation called whenever a {@link org.codehaus.preon.Codec} is done.
     *
     * @param codec    The {@link org.codehaus.preon.Codec} called.
     * @param position The position in the {@link org.codehaus.preon.buffer.BitBuffer}.
     * @param read     The number of bits that actually have been read.
     * @param result   The value decoded by the {@link org.codehaus.preon.Codec}.
     */
    void markEnd(Codec<?> codec, long position, long read,
                         Object result);

    /** The operation called when the {@link org.codehaus.preon.Codec} failed to decode a value. */
    void markFailure();

    void markStartLoad(String name, Object object);

    void markEndLoad();
}
