/**
 * Copyright (C) 2009-2010 Wilfred Springer
 *
 * This file is part of Preon.
 *
 * Preon is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * Preon is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Preon; see the file COPYING. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
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
