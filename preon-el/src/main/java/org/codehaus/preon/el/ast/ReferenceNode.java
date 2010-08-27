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
package org.codehaus.preon.el.ast;

import java.util.Collections;
import java.util.Set;

import org.codehaus.preon.el.Document;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.ReferenceContext;
import org.codehaus.preon.el.ctx.ConvertingReference;
import org.codehaus.preon.el.util.ClassUtils;

public class ReferenceNode<T, E> implements Node<T, E> {

    private Reference<E> reference;

    public ReferenceNode(Reference<E> reference) {
        Class<?> type = reference.getType();
        if (Byte.class == type || Short.class == type || Long.class == type
                || byte.class == type || long.class == type
                || short.class == type) {
            // Let's convert to Integer, to make our life a little easier.
            this.reference = ConvertingReference.create(Integer.class,
                    reference);
        } else {
            this.reference = reference;
        }
    }

    public int compareTo(E context, Node<T, E> other) {
        Class<?> thisType = getType();
        Class<?> otherType = other.getType();
        Class<?> common = ClassUtils.calculateCommonSuperType(thisType,
                otherType);
        if (Comparable.class.isAssignableFrom(common)) {
            return ((Comparable) eval(context)).compareTo(other.eval(context));
        } else {
            throw new ClassCastException("Incomparable types.");
        }
    }

    public T eval(E context) {
        Object result = reference.resolve(context);
        return (T) result;
    }

    public void gather(Set<Reference<E>> references) {
        references.add(reference);
    }

    public Class<T> getType() {
        return (Class<T>) reference.getType();
    }

    public Node<T, E> simplify() {
        return this;
    }

    public Node<T, E> rescope(ReferenceContext<E> context) {
        return new ReferenceNode<T,E>(reference.rescope(context));
    }

    public boolean isConstantFor(ReferenceContext<E> context) {
        return reference.isBasedOn(context);
    }

    public Set<Reference<E>> getReferences() {
        return Collections.singleton(reference);
    }

    public boolean isParameterized() {
        return true;
    }

    public void document(Document target) {
        reference.document(target);
    }

    /**
     * Narrows the underlying reference to a reference of the given type, and
     * returns a new ReferenceNode pointing to this new reference.
     * 
     * @param type
     *            The type to narrow to.
     * @return A new ReferenceNode, pointing to a narrowed version of the
     *         reference held by this object, or the original ReferenceNode, if
     *         the Reference cannot be narrowed.
     */
    public <V> Node<?, E> narrow(Class<V> type) {
        Reference<E> narrowed = reference.narrow(type);
        if (narrowed == null) {
            return this;
        } else {
            return new ReferenceNode<V, E>(narrowed);
        }
    }

}
