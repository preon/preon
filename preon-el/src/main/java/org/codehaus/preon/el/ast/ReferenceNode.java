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
