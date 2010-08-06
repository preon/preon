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

import java.lang.reflect.AnnotatedElement;


/**
 * A factory for {@link Codec} decorators.
 *
 * @author Wilfred Springer
 */
public interface CodecDecorator {

    /**
     * Attempts to wrap the {@link Codec} passed in with new decorator. Note that if the factory fails to create the
     * decorator, it is expected to return the original {@link Codec}.
     *
     * @param <T>      The type of {@link Codec} to be decorated.
     * @param codec    The object that needs to be decorated.
     * @param metadata Metadata for the type.
     * @param type     The type of {@link Codec} to be be decorated.
     * @param context  TODO
     * @return A decorated {@link Codec} or the original {@link Codec}.
     */
    <T> Codec<T> decorate(Codec<T> codec, AnnotatedElement metadata,
                          Class<T> type, ResolverContext context);

}
