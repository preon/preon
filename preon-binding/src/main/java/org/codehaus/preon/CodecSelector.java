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
import nl.flotsam.pecia.ParaContents;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;

import java.io.IOException;
import java.util.Collection;

/**
 * The interface to be implemented by objects that have the capability to select a {@link Codec} based on data on the
 * buffer and a context for resolving references.
 *
 * @author Wilfred Springer
 */
public interface CodecSelector {

    /**
     * Selects the {@link Codec} to be used for decoding, based on the bits on the {@link BitBuffer} and the references
     * that can be resolved using the resolver.
     *
     * @param buffer   The buffer providing the bits.
     * @param resolver The resolver for resolving references.
     * @return The {@link Codec} that needs to be used.
     * @throws DecodingException If we fail to select a {@link Codec} for the data found in the {@link BitBuffer}.
     */
    Codec<?> select(BitBuffer buffer, Resolver resolver)
            throws DecodingException;

    <T> Codec<?> select(Class<T> type, BitChannel channel, Resolver resolver) throws IOException;

    /**
     * Returns the collection of all choices this selector will have to choose from.
     *
     * @return The <code>Collection</code> of all choices this selector will have to choose from.
     */
    Collection<Codec<?>> getChoices();

    /**
     * Documents the procedure for deciding among a couple of {@link Codec}s.
     *
     * @param para The context for generating the content.
     */
    void document(ParaContents<?> para);

    /** Returns an expression representing the number of bits inhabited by the actual selecting bit. */
    Expression<Integer, Resolver> getSize();

}
