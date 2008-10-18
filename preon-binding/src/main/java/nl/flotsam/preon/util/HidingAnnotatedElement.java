/*
 * Copyright (C) 2008 Wilfred Springer
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

package nl.flotsam.preon.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class HidingAnnotatedElement implements AnnotatedElement {

    private Class<? extends Annotation> hidden;

    private AnnotatedElement delegate;

    public HidingAnnotatedElement(Class<? extends Annotation> hidden,
            AnnotatedElement delegate) {
        this.hidden = hidden;
        this.delegate = delegate;
    }

    public <T extends Annotation> T getAnnotation(Class<T> type) {
        if (hidden.equals(type)) {
            return null;
        } else {
            return delegate.getAnnotation(type);
        }
    }

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

    public boolean isAnnotationPresent(Class<? extends Annotation> type) {
        if (hidden.equals(type)) {
            return false;
        } else {
            return delegate.isAnnotationPresent(type);
        }
    }

}
