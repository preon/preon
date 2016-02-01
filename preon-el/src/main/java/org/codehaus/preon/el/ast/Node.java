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

import java.util.Set;

import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.ReferenceContext;

/**
 * A node in the AST representing the expression.
 * 
 * 
 * @author Wilfred Springer
 * 
 * @param <T>
 *            The type of value represented by the node. (Or more precisely, the
 *            type of value to which this node will evaluate when invoking
 *            {@link #eval(Object)}. 
 */
public interface Node<T, E> extends Expression<T, E> {

    /**
     * Evaluates this (part of an) expression and returns the result.
     * 
     * @param context
     *            The object capable of resolving references.
     * @return The result of evaluating the expression.
     */
    T eval(E context);

    /**
     * Returns the type of value to which a node will evaluate.
     * 
     * @return The type of value to which a node will evaluate.
     */
    Class<T> getType();

    /**
     * Attempts to simplify the expression. It will return a (potentially) new
     * node, containing the simplified substitute.
     * 
     * @return The simplified expression.
     */
    Node<T, E> simplify();
    
    Node<T, E> rescope(ReferenceContext<E> context);

    /**
     * Append references used.
     * 
     * @param references
     *            The references gathered so far.
     */
    void gather(Set<Reference<E>> references);

    /**
     * Think {@link Comparable#compareTo(Object)}, but then requiring a context
     * for resolving variables.
     * 
     * @param context
     *            The context allowing you to resolve references.
     * @param other
     *            The object to compare to.
     * @return See {@link Comparable#compareTo(Object)}.
     */
    int compareTo(E context, Node<T, E> other);

}
