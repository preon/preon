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
package org.codehaus.preon.codec;

import org.codehaus.preon.el.Expression;
import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import org.codehaus.preon.Builder;
import org.codehaus.preon.Codec;
import org.codehaus.preon.CodecDescriptor;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.channel.BoundedBitChannel;
import org.codehaus.preon.descriptor.Documenters;

import java.io.IOException;

/**
 * {@link Codec} decoration, preventing the underlying {@link Codec} from being able to read beyond a certain section of
 * the {@link org.codehaus.preon.buffer.BitBuffer} passed in.
 *
 * @param <T> The type of object expected to be returned by this {@link Codec}.
 */
class SlicingCodec<T> implements Codec<T> {

    private final Expression<Integer, Resolver> sizeExpr;

    private final Codec<T> wrapped;

    /**
     * Constructs a new instance.
     *
     * @param wrapped  The {@link Codec} to be wrapped.
     * @param sizeExpr The size of the slice, expressed in bits, as a Limbo expression.
     */
    public SlicingCodec(Codec<T> wrapped, Expression<Integer, Resolver> sizeExpr) {
        this.sizeExpr = sizeExpr;
        this.wrapped = wrapped;
    }

    public T decode(BitBuffer buffer, Resolver resolver, Builder builder)
            throws DecodingException {
        BitBuffer slice = buffer
                .slice(sizeExpr.eval(resolver));
        return wrapped.decode(slice, resolver, builder);
    }

    public void encode(T value, BitChannel channel, Resolver resolver) throws IOException {
        wrapped.encode(value, new BoundedBitChannel(channel, sizeExpr.eval(resolver)), resolver);
    }

    public Class<?>[] getTypes() {
        return wrapped.getTypes();
    }

    public Expression<Integer, Resolver> getSize() {
        return sizeExpr;
    }

    public Class<?> getType() {
        return wrapped.getType();
    }

    public CodecDescriptor getCodecDescriptor() {
        return new CodecDescriptor() {

            public <C extends SimpleContents<?>> Documenter<C> details(
                    final String bufferReference) {
                return new Documenter<C>() {
                    public void document(C target) {
                        target.para().text("The format reserves only ")
                                .document(
                                        Documenters
                                                .forExpression(sizeExpr))
                                .text(" bits for ")
                                .document(
                                        wrapped.getCodecDescriptor()
                                                .reference(Adjective.THE, false))
                                .end();
                        target.document(wrapped.getCodecDescriptor()
                                .details(bufferReference));
                    }
                };
            }

            public String getTitle() {
                return null;
            }

            public <C extends ParaContents<?>> Documenter<C> reference(
                    Adjective adjective, boolean startWithCapital) {
                return wrapped.getCodecDescriptor().reference(adjective, false);
            }

            public boolean requiresDedicatedSection() {
                return false;
            }

            public <C extends ParaContents<?>> Documenter<C> summary() {
                return wrapped.getCodecDescriptor().summary();
            }

        };
    }
}
