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

import org.codehaus.preon.el.Expression;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;

import java.io.IOException;

/**
 * The interface to be implemented by objects that are able to decode/encode objects.
 *
 * @author Wilfred Springer
 * @param <T> The type of object the {@link Codec} is able to decode/encode.
 */
public interface Codec<T> {

    /**
     * Decodes a value from the {@link BitBuffer}.
     *
     * @param buffer   The {@link BitBuffer} containing the data from which a value will be decoded.
     * @param resolver The object capable of resolving variable references, when required.
     * @param builder  The object responsible for creating default instances of objects, when needed. (In reality, this
     *                 is most likely going to be important to {@link org.codehaus.preon.codec.ObjectCodecFactory
     *                 ObjectCodecFactories} only, but in order to make sure the {@link Builder} arrives there, we need
     *                 to have the ability to pass it in.
     * @return The decoded value.
     * @throws DecodingException If the {@link Codec} fails to decode the value.
     */
    T decode(BitBuffer buffer, Resolver resolver, Builder builder)
            throws DecodingException;

    /**
     * Encodes the object to the {@link org.codehaus.preon.channel.BitChannel}.
     *
     * @param value    The object to encode.
     * @param channel  The channel to receive the encoded representation.
     * @param resolver The object providing access to the context.
     */
    void encode(T value, BitChannel channel, Resolver resolver) throws IOException;

    /**
     * Returns an expression that is expected to return the number of bits occupied by objects created by this Codec, as
     * a function of the context to which variables will be resolved.
     * <p/>
     * <p> This method may return null, indicating that it is impossible to state anything at all on the expected number
     * of bits. Note that if this method <em>does</em> return an {@link Expression}, then it will require a {@link
     * Resolver} to resolve variables inside this expression, <em>unless {@link Expression#isParameterized()} returns
     * <code>false</code></em> . </p>
     *
     * @return A Limbo {@link Expression}, expressing the number of bits occupied by instance loaded and stored by this
     *         Codec.
     */
    Expression<Integer, Resolver> getSize();

    /**
     * Returns an object that is capable of rendering a description of the data structure encoded/decoded by this
     * Codec.
     *
     * @return An object capable of describing the {@link Codec}.
     */
    CodecDescriptor getCodecDescriptor();

    /**
     * Returns an array of types constructed potentially by this Codec.
     *
     * @return An array of types constructed potentially by this codec.
     */
    Class<?>[] getTypes();

    /**
     * Returns the (common super) type of object constructed by this {@link Codec}.
     *
     * @return The (common super-) type of object constructed by this {@link Codec}.
     */
    Class<?> getType();

}
