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

import org.codehaus.preon.el.Expression;
import nl.flotsam.pecia.ParaContents;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;

import java.io.IOException;
import java.util.Collection;

/**
 * The interface to be implemented by objects that have the capability to select a {@link Codec} based on data on the
 * buffer and a context for resolving references.
 *
 * @author Wilfred Springer
 */
public interface CodecSelector {

    /**
     * Selects the {@link Codec} to be used for decoding, based on the bits on the {@link BitBuffer} and the references
     * that can be resolved using the resolver.
     *
     * @param buffer   The buffer providing the bits.
     * @param resolver The resolver for resolving references.
     * @return The {@link Codec} that needs to be used.
     * @throws DecodingException If we fail to select a {@link Codec} for the data found in the {@link BitBuffer}.
     */
    Codec<?> select(BitBuffer buffer, Resolver resolver)
            throws DecodingException;

    <T> Codec<?> select(Class<T> type, BitChannel channel, Resolver resolver) throws IOException;

    /**
     * Returns the collection of all choices this selector will have to choose from.
     *
     * @return The <code>Collection</code> of all choices this selector will have to choose from.
     */
    Collection<Codec<?>> getChoices();

    /**
     * Documents the procedure for deciding among a couple of {@link Codec}s.
     *
     * @param para The context for generating the content.
     */
    void document(ParaContents<?> para);

    /** Returns an expression representing the number of bits inhabited by the actual selecting bit. */
    Expression<Integer, Resolver> getSize();

}
