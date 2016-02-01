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

import org.codehaus.preon.*;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.descriptor.PassThroughCodecDescriptor2;
import org.codehaus.preon.el.Expression;

/**
 * The {@link org.codehaus.preon.Codec} constructed by the {@link org.codehaus.preon.emitter.EmittingCodecDecorator}.
 *
 * @author Wilfred Springer (wis)
 * @param <T>
 */
public class EmittingCodec<T> implements Codec<T> {

    /** The {@link org.codehaus.preon.Codec} wrapped. */
    private final Codec<T> codec;

    /** The {@link Emitter} to use. */
    private final Emitter emitter;

    /**
     * Constructs a new instance.
     *
     * @param codec  The {@link org.codehaus.preon.Codec} to wrap.
     * @param emitter The {@link Emitter} to use.
     */
    public EmittingCodec(Codec<T> codec, Emitter emitter) {
        assert codec != null;
        assert emitter != null;
        this.codec = codec;
        this.emitter = emitter;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codehaus.preon.Codec#decode(org.codehaus.preon.buffer.BitBuffer,
     * org.codehaus.preon.Resolver, org.codehaus.preon.Builder)
     */

    public T decode(BitBuffer buffer, Resolver resolver, Builder builder)
            throws DecodingException {
        T result = null;
        long pos = buffer.getActualBitPos();
        emitter.markStart(codec, pos, buffer);
        try {
            result = codec.decode(buffer, resolver, builder);
        } catch (DecodingException de) {
            emitter.markFailure();
            throw de;
        } finally {
            emitter.markEnd(codec, buffer.getActualBitPos(), buffer
                    .getActualBitPos()
                    - pos, result);
        }
        return result;
    }

    public void encode(T object, BitChannel channel, Resolver resolver) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codehaus.preon.Codec#getTypes()
     */

    public Class<?>[] getTypes() {
        return codec.getTypes();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codehaus.preon.Codec#getSize()
     */

    public Expression<Integer, Resolver> getSize() {
        return codec.getSize();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codehaus.preon.Codec#getType()
     */

    public Class<?> getType() {
        return codec.getType();
    }

    public CodecDescriptor getCodecDescriptor() {
        return new PassThroughCodecDescriptor2(codec.getCodecDescriptor(),
                false);
    }

}
