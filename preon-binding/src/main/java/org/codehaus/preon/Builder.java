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

/**
 * The interface to be implemented by objects with the capability to create default instances of arbitrary types being
 * passed in. This interface was introduced to allow {@link Codec Codecs} to create instances of a certain type without
 * having to worry on how these instances need to be constructed. <p/> <p> This is needed in particular when the {@link
 * Codec Codecs} need to create instances of (non-static) inner classes. In those cases, the new instances need to be
 * constructed by passing a reference to the outer instance. Since {@link Codec Codecs} are not aware of the outer
 * instance, they would never be able to create instances of the inner class. By introducing the {@link Builder} we are
 * able to pass in a context from the owning {@link Codec} with the capability to create instances of inner classes.
 * </p>
 *
 * @author Wilfred Springer
 */
public interface Builder {

    /**
     * Creates a default instance of T.
     *
     * @param type
     *            The type of instance desired.
     * @param <T>
     *            The type of instance we need.
     * @return An instance of T.
     * @throws InstantiationException
     *             If we can't instantiate the given type.
     * @throws IllegalAccessException
     *             If we are not allowed to instantiate the given type.
     */
    <T> T create(Class<T> type) throws InstantiationException, IllegalAccessException;

}
