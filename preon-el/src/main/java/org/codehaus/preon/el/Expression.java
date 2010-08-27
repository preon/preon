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
