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

import java.util.HashSet;
import java.util.Set;

import nl.flotsam.limbo.Reference;

/**
 * A base class for {@link Node} implementations, implementing the
 * {@link #getReferences()} operation.
 * 
 * @author Wilfred Springer (wis)
 * 
 * @param <T>
 *            The type of value to which this AST node resolves in
 *            {@link #eval(Object)}
 * @param <E>
 *            The type of context passed in to resolve references.
 */
public abstract class AbstractNode<T extends Comparable<T>, E> implements Node<T, E> {

    public int compareTo(E context, Node<T, E> other) {
        return eval(context).compareTo(other.eval(context));
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.flotsam.limbo.Expression#getReferences()
     */
    public Set<Reference<E>> getReferences() {
        Set<Reference<E>> references = new HashSet<Reference<E>>();
        gather(references);
        return references;
    }

}
