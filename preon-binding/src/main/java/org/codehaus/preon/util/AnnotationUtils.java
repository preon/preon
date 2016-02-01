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
package org.codehaus.preon.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class AnnotationUtils {

    /**
     * Compares two AnnotatedElement instances to see if they basically define the same data. Currently ignores array
     * type of elements.
     *
     * @param first  The first {@link AnnotatedElement}.
     * @param second The second {@link AnnotatedElement}
     * @return A boolean indicating whether or not these are considered to be equivalent.
     */
    public static boolean equivalent(AnnotatedElement first,
                                     AnnotatedElement second) {
        if (first == null || second == null || first.getAnnotations() == null
                || second.getAnnotations() == null) {
            return false;
        } else {
            if (first.getAnnotations().length != second.getAnnotations().length) {
                // If the number of annotations doesn't add up, there is nothing left to compare. 
                return false;
            } else {
                for (Annotation annotation : first.getAnnotations()) {
                    Annotation other = second.getAnnotation(annotation
                            .annotationType());
                    if (!annotation.equals(other)) {
                        return false;
                    }
                }
                return true;
            }
        }
    }

    public static int calculateHashCode(AnnotatedElement metadata) {
        if (metadata == null) {
            int result = 0;
            for (Annotation annotation : metadata.getAnnotations()) {
                result += annotation.hashCode();
            }
            return result;
        } else {
            return 0;
        }
    }

}
