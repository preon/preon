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
