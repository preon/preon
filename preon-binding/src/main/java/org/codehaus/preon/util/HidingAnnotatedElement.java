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
 * A wrapper of {@link AnnotatedElement}s, hiding certain annotations.
 *
 * @author Wilfred Springer
 */
public class HidingAnnotatedElement implements AnnotatedElement {

    /** The type of annotation that needs to be hidden. */
    private Class<? extends Annotation> hidden;

    /** The {@link AnnotatedElement} to wrap. */
    private AnnotatedElement delegate;

    /**
     * Constructs a new instance, accepting the annotation that needs to be hidden, as well as the {@link
     * AnnotatedElement} that (probably) carries that and other annotations.
     *
     * @param hidden   The type of annotation that need to be hidden.
     * @param delegate The {@link AnnotatedElement} from which a certain type of annotation needs to be hidden.
     */
    public HidingAnnotatedElement(Class<? extends Annotation> hidden, AnnotatedElement delegate) {
        this.hidden = hidden;
        this.delegate = delegate;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.reflect.AnnotatedElement#getAnnotation(java.lang.Class)
     */

    public <T extends Annotation> T getAnnotation(Class<T> type) {
        if (hidden.equals(type)) {
            return null;
        } else {
            return delegate.getAnnotation(type);
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.reflect.AnnotatedElement#getAnnotations()
     */

    public Annotation[] getAnnotations() {
        if (delegate.isAnnotationPresent(hidden)) {
            Annotation[] unhidden = delegate.getAnnotations();
            Annotation[] result = new Annotation[unhidden.length - 1];
            int j = 0;
            for (int i = 0; i < unhidden.length; i++) {
                if (!hidden.equals(unhidden[i].getClass())) {
                    result[j++] = unhidden[i];
                }
            }
            return result;
        } else {
            return delegate.getAnnotations();
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.reflect.AnnotatedElement#getDeclaredAnnotations()
     */

    public Annotation[] getDeclaredAnnotations() {
        if (delegate.isAnnotationPresent(hidden)) {
            Annotation[] unhidden = delegate.getDeclaredAnnotations();
            Annotation[] result = new Annotation[unhidden.length - 1];
            int j = 0;
            for (int i = 0; i < unhidden.length; i++) {
                if (!hidden.equals(unhidden[i].getClass())) {
                    result[j++] = unhidden[i];
                }
            }
            return result;
        } else {
            return delegate.getDeclaredAnnotations();
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.reflect.AnnotatedElement#isAnnotationPresent(java.lang.Class)
     */

    public boolean isAnnotationPresent(Class<? extends Annotation> type) {
        if (hidden.equals(type)) {
            return false;
        } else {
            return delegate.isAnnotationPresent(type);
        }
    }

}
