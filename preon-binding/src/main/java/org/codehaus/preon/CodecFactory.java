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
 * A factory for {@link Codec Codecs}. A {@link CodecFactory} is not tightly coupled to a certain type of {@link
 * Codec}.
 *
 * @author Wifred Springer
 */
public interface CodecFactory {

    /**
     * Constructs a new {@link Codec}. The <code>metadata</code> argument is used for passing data on the expectations
     * of the clients of the factory. <p> The client might choose not to pass any metadata at all, basically leaving it
     * up to the factory to make its own decisions based on type information only. The client might also pass an empty
     * {@link AnnotatedElement}. </p> <p> Note the subtle difference here. Passing <code>null</code> means: if you can
     * create a {@link Codec} for this type, then please do so. Passing an empty {@link AnnotatedElement} means: please
     * give me a {@link Codec} for the given type, but only if you find an annotation that explicitly tells you to do
     * so. </p>
     *
     * @param <T>      The type of the objects to be returned by the {@link Codec}.
     * @param metadata A bucket of metadata, used by the the factory to determine <em>if</em> it should be creating a
     *                 {@link Codec} for a certain type, but also <em>how</em> the {@link Codec} should be created.
     * @param type     The type of the objects to be returned by the {@link Codec}. (Not null).
     * @param context  The context for creating references.
     * @return A new {@link Codec} for the given type.
     */
    <T> Codec<T> create(AnnotatedElement metadata, Class<T> type,
                        ResolverContext context);

}
