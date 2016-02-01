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

/**
 * A threadsafe {@link BitBuffer}. The threadsafe implementation is wrapped around another {@link BitBuffer}.
 *
 * @author Wilfred Springer
 */
public class ConcurrentBitBuffer extends AbstractBitBufferDecorator {

    /** The current {@link BitBuffer}, indexed by thread. */
    private ThreadLocal<BitBuffer> current = new ThreadLocal<BitBuffer>();

    /** The source from which all other (thread-bound) {@link BitBuffer BitBuffers} will be created. */
    private BitBuffer source;

    /**
     * Constructs a new instance, accepting a source {@link BitBuffer}. Note that this instance is expected <em>not</em> to
     * change, in order to preserve the guarantee that all threads will get an (initially) identicial copy. If that's
     * something you can't guarantee, it might be better to create a {@link BitBuffer#duplicate() duplicate} first.
     *
     * @param source The {@link BitBuffer} from which all thread-bound {@link BitBuffer BitBuffers} will be created.
     */
    public ConcurrentBitBuffer(BitBuffer source) {
        this.source = source;
    }

    @Override
    public BitBuffer getDelegate() {
        BitBuffer result = current.get();
        if (result == null) {
            result = source.duplicate();
            current.set(result);
        }
        return result;
    }

    // JavaDoc inherited

    @Override
    public BitBuffer duplicate() {
        return new ConcurrentBitBuffer(getDelegate().duplicate());
    }

    // JavaDoc inherited

    @Override
    public BitBuffer slice(long length) {
        return new SlicedBitBuffer(duplicate(), length);
    }

    // JavaDoc inherited

    public ByteBuffer readAsByteBuffer(int length) {
        return getDelegate().readAsByteBuffer(length);
    }

    public ByteBuffer readAsByteBuffer() {
        return getDelegate().readAsByteBuffer();
    }

    public long getActualBitPos() {
        return getDelegate().getActualBitPos();
    }

}
