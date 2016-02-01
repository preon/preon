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
