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

/**
 * A context for building references to elements in an environment of E. The
 * {@link ReferenceContext} interface allows us to build references relying on
 * different types of environments.
 * 
 * <p>
 * The main reason for having the {@link ReferenceContext} interface is to have
 * a better abstraction from the environment in which a Limbo expression is
 * executed. With these abstractions, the component <em>using</em> Limbo in a
 * certain context is still able to define the meaning of the selectors for that
 * particular context. This allows Quarks to build meaningful descriptions of
 * references by taking Codec information into account.
 * </p>
 * 
 * @author Wilfred Springer
 * 
 * @param <E>
 *            The type of environment that needs to be passed in in order to
 *            resolve a {@link Reference} created from this
 *            {@link ReferenceContext}.
 */
public interface ReferenceContext<E> extends Descriptive {

    /**
     * Creates a new {@link Reference}; the new {@link Reference} will be
     * resolved by first resolving the current reference, and then use the name
     * to navigate to another object associated to that object.
     * 
     * @param name
     *            The name to be used to resolve the Reference.
     * @return A new Reference, pointing to the object that is related to the
     *         original object by the given name.
     */
    Reference<E> selectAttribute(String name)
            throws BindingException;

    /**
     * Creates a new {@link Reference}; the new {@link Reference} will be
     * resolved by first resolving the current reference, and then use the index
     * to navigate to the associated object.
     * 
     * @param index
     *            The index to be used. (A Limbo expression.)
     * @return A new Reference, pointing to the object that is related to the
     *         original object by the given index.
     */
    Reference<E> selectItem(String index)
            throws BindingException;

    /**
     * Creates a new {@link Reference}; the new {@link Reference} will be
     * resolved by first resolving the current reference, and then use the index
     * to navigate to the associated object.
     * 
     * @param index
     *            The index to be used. (An object representation of an
     *            Expression on the same context.)
     * @return A new Reference, pointing to the object that is related to the
     *         original object by the given index.
     */
    Reference<E> selectItem(Expression<Integer, E> index)
            throws BindingException;

}
