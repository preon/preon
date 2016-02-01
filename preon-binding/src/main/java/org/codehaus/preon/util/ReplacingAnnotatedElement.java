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

/**
 * An {@link AnnotatedElement} wrapper, replacing one of the annotations with another one.
 *
 * @author Wilfred Springer (wis)
 */
public class ReplacingAnnotatedElement implements AnnotatedElement {

    /** The replacement annotation. */
    private Annotation replacement;

    /** The other annotations. */
    private AnnotatedElement wrapped;

    public ReplacingAnnotatedElement(AnnotatedElement wrapped, Annotation replacement) {
        this.wrapped = wrapped;
        this.replacement = replacement;
    }

    /*
    * (non-Javadoc)
    * @see java.lang.reflect.AnnotatedElement#getAnnotation(java.lang.Class)
    */

    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        if (annotationType.isAssignableFrom(replacement.getClass())) {
            return (T) replacement;
        } else {
            return wrapped.getAnnotation(annotationType);
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.reflect.AnnotatedElement#getAnnotations()
     */

    public Annotation[] getAnnotations() {
        Annotation[] annotations = wrapped.getAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            if (annotations[i].getClass().isAssignableFrom(replacement.getClass())) {
                annotations[i] = replacement;
            }
        }
        return annotations;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.reflect.AnnotatedElement#getDeclaredAnnotations()
     */

    public Annotation[] getDeclaredAnnotations() {
        Annotation[] annotations = wrapped.getDeclaredAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            if (annotations[i].getClass() == replacement.getClass()) {
                annotations[i] = replacement;
            }
        }
        return annotations;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.reflect.AnnotatedElement#isAnnotationPresent(java.lang.Class)
     */

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        if (wrapped.isAnnotationPresent(annotationType)) {
            return true;
        } else {
            return replacement.getClass() == annotationType;
        }
    }

}
