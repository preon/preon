/*
 * Copyright 2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package nl.flotsam.limbo.ast;

import java.util.Collections;
import java.util.Set;

import nl.flotsam.limbo.Document;
import nl.flotsam.limbo.Reference;
import nl.flotsam.limbo.ctx.ConvertingReference;
import nl.flotsam.limbo.util.ClassUtils;

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
