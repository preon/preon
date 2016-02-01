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

import java.util.HashSet;
import java.util.Set;

import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.ReferenceContext;

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
     * @see org.codehaus.preon.el.Expression#getReferences()
     */
    public Set<Reference<E>> getReferences() {
        Set<Reference<E>> references = new HashSet<Reference<E>>();
        gather(references);
        return references;
    }

    public boolean isConstantFor(ReferenceContext<E> eReferenceContext) {
        return true;
    }
    
}
