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

import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Reference;

/**
 * A node in the AST representing the expression.
 * 
 * 
 * @author Wilfred Springer
 * 
 * @param <T>
 *            The type of value represented by the node. (Or more precisely, the
 *            type of value to which this node will evaluate when invoking
 *            {@link #eval(E context)}.
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
