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
