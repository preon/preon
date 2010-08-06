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
package org.codehaus.preon.codec;

import org.codehaus.preon.Codec;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.annotation.BoundEnumOption;
import org.codehaus.preon.annotation.BoundNumber;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.buffer.ByteOrder;
import org.codehaus.preon.buffer.DefaultBitBuffer;
import org.easymock.EasyMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.AnnotatedElement;
import java.nio.ByteBuffer;

public class EnumCodecFactoryTest {

    private AnnotatedElement metadata;

    private BoundNumber boundNumber;

    @Before
    public void setUp() {
        metadata = EasyMock.createMock(AnnotatedElement.class);
        boundNumber = EasyMock.createMock(BoundNumber.class);
    }

    @Test
    public void testHappyPath() throws DecodingException {
        // Pre-play behaviour
        EasyMock.expect(metadata.isAnnotationPresent(BoundNumber.class))
                .andReturn(true);
        EasyMock.expect(metadata.getAnnotation(BoundNumber.class)).andReturn(
                boundNumber);
        EasyMock.expect(boundNumber.size()).andReturn("8");
        EasyMock.expect(boundNumber.byteOrder()).andReturn(
                ByteOrder.LittleEndian);

        // Replay
        EasyMock.replay(metadata, boundNumber);
        EnumCodec.Factory factory = new EnumCodec.Factory();
        BitBuffer buffer = new DefaultBitBuffer(ByteBuffer.wrap(new byte[]{0,
                1}));
        Codec<Direction> codec = factory
                .create(metadata, Direction.class, null);
        assertNotNull(codec);
        assertEquals(Direction.Left, codec.decode(buffer, null, null));

        // Verify
        EasyMock.verify(metadata, boundNumber);
    }

    enum Direction {
        @BoundEnumOption(0)
        Left,

        @BoundEnumOption(1)
        Right
    }

}
