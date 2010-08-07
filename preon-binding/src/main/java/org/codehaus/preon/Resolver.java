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
package org.codehaus.preon;

import org.codehaus.preon.el.BindingException;

/**
 * A simple interface for resolving variable values. The interface is introduced to have a flexible bridge between Limbo
 * and Preon. With this interface, we can still have different ways of retrieving values.
 *
 * @author Wilfred Springer
 */
public interface Resolver {

    /**
     * Returns the value for a named variable.
     *
     * @param name The name.
     * @return The value.
     * @throws BindingException If the name does not happen to be bound to a variable at runtime.
     */
    Object get(String name) throws BindingException;

    /**
     * Returns a reference to the original Resolver for an expression.
     *
     * @return The original {@link Resolver}.
     */
    Resolver getOriginalResolver();

}
