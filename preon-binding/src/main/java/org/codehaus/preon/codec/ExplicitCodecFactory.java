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

import java.lang.reflect.AnnotatedElement;

import org.codehaus.preon.Codec;
import org.codehaus.preon.CodecConstructionException;
import org.codehaus.preon.CodecFactory;
import org.codehaus.preon.ResolverContext;
import org.codehaus.preon.annotation.BoundExplicitly;

/**
 * A {@link CodecFactory} allowing you to explicitly set the {@link Codec} to use. (Triggered by the {@link
 * BoundExplicitly} annotation.
 *
 * @author Wilfred Springer (wis)
 */
public class ExplicitCodecFactory implements CodecFactory {

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.CodecFactory#create(java.lang.reflect.AnnotatedElement, java.lang.Class, org.codehaus.preon.ResolverContext)
     */

    public <T> Codec<T> create(AnnotatedElement metadata, Class<T> type,
                               ResolverContext context) {
        if (metadata != null
                && metadata.isAnnotationPresent(BoundExplicitly.class)) {
            BoundExplicitly settings = metadata
                    .getAnnotation(BoundExplicitly.class);
            try {
                CodecFactory factory = settings.factory().newInstance();
                return factory.create(metadata, type, null);
            } catch (InstantiationException e) {
                throw new CodecConstructionException(
                        "Failed to construct Codec using "
                                + settings.factory().getName());
            } catch (IllegalAccessException e) {
                throw new CodecConstructionException(
                        "No permission to construct an instance of "
                                + settings.factory().getName());
            }
        } else {
            return null;
        }
    }

}
