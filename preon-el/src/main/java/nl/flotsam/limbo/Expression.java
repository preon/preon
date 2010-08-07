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

package nl.flotsam.limbo;

import java.util.Set;

/**
 * An object encapsulating an expression, with the ability to evaluate itself
 * against an external context, and the ability to describe itself against a
 * certain external context.
 * 
 * <p>
 * Here are some of the typical things you might expect to get when describing
 * the expression in human-readable language:
 * </p>
 * 
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
 * 
 * @param <R>
 *            The type of value returned when evaluating this expression.
 * @param <C>
 *            The type of context to which this expression applies.
 */
public interface Expression<R, C> extends Descriptive {

    /**
     * Evaluates the expression and returns the result.
     * 
     * @param resolver
     *            The object responsible for providing values for variables
     *            referenced in the expression.
     * @return The result of evaluating the expression.
     * @throws BindingException
     *             If references in the expression cannot be bound to the
     *             context passed in.
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

}
