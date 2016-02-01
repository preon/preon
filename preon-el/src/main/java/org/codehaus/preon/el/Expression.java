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
package org.codehaus.preon.el;

import java.util.Set;

/**
 * An object encapsulating an expression, with the ability to evaluate itself
 * against an external context, and the ability to describe itself against a
 * certain external context.
 * <p/>
 * <p>
 * Here are some of the typical things you might expect to get when describing
 * the expression in human-readable language:
 * </p>
 * <p/>
 * <pre>
 * car.tyre.weight &gt; 3
 *     &quot;the weight of the car's tyre is bigger than 3&quot;
 * map.settings.version &lt; 650
 *     &quot;the version of the map's settings is smaller than 650&quot;
 * body.weight = 70
 *     &quot;the body's weight equals 70&quot;
 * body.weight.max = 80
 *     &quot;the max of the body's weight equals 80&quot;
 * </pre>
 *
 * @author Wilfred Springer
 * @param <R>
 * The type of value returned when evaluating this expression.
 * @param <C>
 * The type of context to which this expression applies.
 */
public interface Expression<R, C> extends Descriptive {

    /**
     * Evaluates the expression and returns the result.
     *
     * @param context The object responsible for providing values for variables
     *                referenced in the expression.
     * @return The result of evaluating the expression.
     * @throws BindingException If references in the expression cannot be bound to the
     *                          context passed in.
     */
    R eval(C context) throws BindingException;

    /**
     * Returns a set of all references included in the expression.
     *
     * @return A set of all references included in the expression.
     */
    Set<Reference<C>> getReferences();

    /**
     * Returns a boolean, indicating if this expression depends on a context of
     * type C passed in.
     *
     * @return A boolean indicating if the expression depends on parameters on
     *         the context.
     */
    boolean isParameterized();

    /**
     * Returns the type of value returned by this expression.
     *
     * @return The type of value returned by this expression.
     */
    Class<R> getType();

    /**
     * Attempts to simplify the expression. It will return a (potentially) new
     * node, containing the simplified substitute.
     *
     * @return The simplified expression.
     */
    Expression<R, C> simplify();

    /**
     * Returns a boolean, indicating if the expression is constant in scope of the given context.
     *
     * @param context
     * @return <code>true</code> if the outcome of the expression is defined completely by the given context; <code>false</code> otherwise.
     */
    boolean isConstantFor(ReferenceContext<C> context);

    /**
     * Returns an expression that is rescoped for the context passed in. Only if {@link #isConstantFor(ReferenceContext)} returns true.
     *
     * @param context The context to rescope for.
     * @return A rescoped version of the expression.
     */
    Expression<R, C> rescope(ReferenceContext<C> context);

}
