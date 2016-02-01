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
import org.codehaus.preon.el.Expressions;
import org.codehaus.preon.*;
import org.codehaus.preon.annotation.Slice;
import org.codehaus.preon.buffer.BitBuffer;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * A {@link CodecFactory} creating {@link Codec Codecs} slicing the {@link BitBuffer} to limit the visibility of the
 * remainder of the buffer (and easily skip forward, if the data itself is not required). Triggered by the {@link Slice}
 * annotation.
 */
public class SlicingCodecDecorator implements CodecDecorator {

    public <T> Codec<T> decorate(Codec<T> decorated, AnnotatedElement metadata,
                                 Class<T> type, ResolverContext context) {
        Slice slice = getAnnotation(metadata, type, Slice.class);
        if (slice != null) {
            return createCodecFromSlice(decorated, slice, context);
        }
        return decorated;
    }

    private <T, V extends Annotation> V getAnnotation(
            AnnotatedElement metadata, Class<T> type, Class<V> annotation) {
        if (type.isAnnotationPresent(annotation)) {
            return type.getAnnotation(annotation);
        }
        if (metadata != null && metadata.isAnnotationPresent(annotation)) {
            return metadata.getAnnotation(annotation);
        }
        return null;
    }

    private <T> Codec<T> createCodecFromSlice(Codec<T> decorated, Slice slice,
                                              ResolverContext context) {
        Expression<Integer, Resolver> sizeExpr;
        sizeExpr = Expressions.createInteger(context, slice.size());
        return new SlicingCodec<T>(decorated, sizeExpr);
    }

}
