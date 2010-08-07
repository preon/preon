/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.flotsam.limbo.util;

import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Expressions;
import nl.flotsam.limbo.InvalidExpressionException;
import nl.flotsam.limbo.ReferenceContext;

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
     *            A character sequence conforming to the limbo grammar,
     *            returning a boolean.
     * @return A boolean {@link Expression}.
     * @throws InvalidExpressionException
     *             If the character sequence passed in is not a valid limbo
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
     *            A character sequence conforming to the limbo grammar,
     *            returning an integer.
     * @return A boolean {@link Expression}.
     * @throws InvalidExpressionException
     *             If the character sequence passed in is not a valid limbo
     *             expression returning an integer, or if the references used
     *             cannot be linked to the {@link #context ReferenceContext}.
     */
    public Expression<Integer, E> createInteger(String expr)
            throws InvalidExpressionException {
        return Expressions.createInteger(context, expr);
    }

}
