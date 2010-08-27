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

import org.codehaus.preon.el.ctx.MultiReference;

/**
 * A reference to an Object, based on an environment of type <code>E</type>.
 *
 * @author Wilfred Springer
 * @param <E>
 * The type of environment on which this reference is based.
 */
public interface Reference<E> extends ReferenceContext<E> {

    /**
     * Resolves the reference in a certain context.
     *
     * @param context The context in which references need to be resolved.
     * @return The object referenced by the Reference.
     */
    Object resolve(E context);

    /**
     * Returns the context in which this {@link Reference} is applicable.
     *
     * @return The {@link ReferenceContext} in which this reference is
     *         applicable.
     */
    ReferenceContext<E> getReferenceContext();

    /**
     * Returns a boolean indicating if the value referenced by this reference
     * can be assigned to a variable of the given type.
     *
     * @param type The type of variable that needs to receive the value.
     * @return A boolean indicating if the value reference by this reference can
     *         be assigned to a variable of the given type.
     */
    boolean isAssignableTo(Class<?> type);

    /**
     * Returns the type of value referenced by this reference.
     *
     * @return The type of value reference by this reference.
     */
    Class<?> getType();

//    /**
//     * Returns if the {@link Reference} supports the narrow
//     * {@link #narrow(Class)} operation; that is, if this operation returns
//     * <code>false</code>, then calling {@link #narrow(Class)} is pointless, so
//     * you might as well not do it.
//     * 
//     * @return A boolean indicating if it makes sense to call
//     *         {@link #narrow(Class)} on this reference: <code>true</code> if it
//     *         does, <code>false</code> if it doesn't.
//     */
//    boolean supportsNarrow();
//

    /**
     * Narrow the reference to a reference of the given type. The resulting
     * reference is expected to <em>always</em> evaluate to an instance of
     * <code>type</code>.
     * <p/>
     * <p/>
     * In order to understand the effect of this operation, it is important to
     * understand that there may sometimes be some ambiguity in a reference.
     * Take for instance the {@link MultiReference}. Calling
     * {@link MultiReference#selectAttribute(String)} will create another
     * {@link MultiReference}, pointing to different properties of different
     * types on different types of objects. Calling {@link #narrow(Class)} will
     * essentially drop all references not pointing to an instance of
     * <code>type</code>.
     *
     * @param type Narrows the reference to a reference that is guaranteed to
     *             point to a subclass of <code>type</code>.
     * @return A reference that is guaranteed to point to a subclass of
     *         <code>type</code>, or <code>null</code> if it cannot be narrowed.
     */
    Reference<E> narrow(Class<?> type);

    /**
     * Returns a boolean, indicating if the reference is based on the given context.
     *
     * @param context
     * @return <code>true</code> if the outcome of the reference is based on the given context; <code>false</code> otherwise.
     */
    boolean isBasedOn(ReferenceContext<E> context);

    Reference<E> rescope(ReferenceContext<E> context);

}
