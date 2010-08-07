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

import java.util.Set;

import nl.flotsam.limbo.Document;
import nl.flotsam.limbo.Reference;

/**
 * The superclass of reference nodes.
 * 
 * @author Wilfred Springer
 * 
 * @param <T>
 *            The type of value to which the reference should evaluate.
 */
public abstract class AbstractReferenceNode<T extends Comparable<T>, E> extends AbstractNode<T, E> {

    private Reference<E> reference;

    public AbstractReferenceNode(Reference<E> reference) {
        this.reference = reference;
    }

    @SuppressWarnings("unchecked")
    public T eval(E context) {
        return (T) resolveValue(context);
    }

    protected Object resolveValue(E context) {
        return reference.resolve(context);
    }

    public void gather(Set<Reference<E>> references) {
        references.add(reference);
    }

    public void document(Document target) {
        reference.document(target);
    }

}
