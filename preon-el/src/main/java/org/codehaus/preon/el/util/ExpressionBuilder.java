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
package org.codehaus.preon.el.util;

import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Expressions;
import org.codehaus.preon.el.InvalidExpressionException;
import org.codehaus.preon.el.ReferenceContext;

/**
 * A simple convenience class for building {@link Expression} instances, simply
 * encapsulating a {@link ReferenceContext}, allowing clients to be unaware of
 * the particular {@link ReferenceContext} in use.
 * 
 * @author Wilfred Springer
 * 
 * @param <E>
 *            The type of environment used to resolve references.
 */
public class ExpressionBuilder<E> {

    /**
     * The context for constructing references.
     */
    private ReferenceContext<E> context;

    /**
     * Constructs a new instance.
     * 
     * @param context
     *            The context for constructing references.
     */
    public ExpressionBuilder(ReferenceContext<E> context) {
        this.context = context;
    }

    /**
     * Creates an {@link Expression} of type boolean.
     * 
     * @param expr
     *            A character sequence conforming to the el grammar,
     *            returning a boolean.
     * @return A boolean {@link Expression}.
     * @throws InvalidExpressionException
     *             If the character sequence passed in is not a valid el
     *             expression returning a boolean, or if the references used
     *             cannot be linked to the {@link #context ReferenceContext}.
     */
    public Expression<Boolean, E> createBoolean(String expr)
            throws InvalidExpressionException {
        return Expressions.createBoolean(context, expr);
    }

    /**
     * Creates an {@link Expression} of type integer.
     * 
     * @param expr
     *            A character sequence conforming to the el grammar,
     *            returning an integer.
     * @return A boolean {@link Expression}.
     * @throws InvalidExpressionException
     *             If the character sequence passed in is not a valid el
     *             expression returning an integer, or if the references used
     *             cannot be linked to the {@link #context ReferenceContext}.
     */
    public Expression<Integer, E> createInteger(String expr)
            throws InvalidExpressionException {
        return Expressions.createInteger(context, expr);
    }

}
