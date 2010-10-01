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
package org.codehaus.preon.emitter;

import org.codehaus.preon.*;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.descriptor.PassThroughCodecDescriptor2;
import org.codehaus.preon.el.Expression;

/**
 * The {@link org.codehaus.preon.Codec} constructed by the {@link org.codehaus.preon.emitter.EmittingCodecDecorator}.
 *
 * @author Wilfred Springer (wis)
 * @param <T>
 */
public class EmittingCodec<T> implements Codec<T> {

    /** The {@link org.codehaus.preon.Codec} wrapped. */
    private final Codec<T> codec;

    /** The {@link Emitter} to use. */
    private final Emitter emitter;

    /**
     * Constructs a new instance.
     *
     * @param codec  The {@link org.codehaus.preon.Codec} to wrap.
     * @param emitter The {@link Emitter} to use.
     */
    public EmittingCodec(Codec<T> codec, Emitter emitter) {
        assert codec != null;
        assert emitter != null;
        this.codec = codec;
        this.emitter = emitter;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codehaus.preon.Codec#decode(org.codehaus.preon.buffer.BitBuffer,
     * org.codehaus.preon.Resolver, org.codehaus.preon.Builder)
     */

    public T decode(BitBuffer buffer, Resolver resolver, Builder builder)
            throws DecodingException {
        T result = null;
        long pos = buffer.getActualBitPos();
        emitter.markStart(codec, pos, buffer);
        try {
            result = codec.decode(buffer, resolver, builder);
        } catch (DecodingException de) {
            emitter.markFailure();
            throw de;
        } finally {
            emitter.markEnd(codec, buffer.getActualBitPos(), buffer
                    .getActualBitPos()
                    - pos, result);
        }
        return result;
    }

    public void encode(T object, BitChannel channel, Resolver resolver) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codehaus.preon.Codec#getTypes()
     */

    public Class<?>[] getTypes() {
        return codec.getTypes();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codehaus.preon.Codec#getSize()
     */

    public Expression<Integer, Resolver> getSize() {
        return codec.getSize();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codehaus.preon.Codec#getType()
     */

    public Class<?> getType() {
        return codec.getType();
    }

    public CodecDescriptor getCodecDescriptor() {
        return new PassThroughCodecDescriptor2(codec.getCodecDescriptor(),
                false);
    }

}
