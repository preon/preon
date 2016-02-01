/**
 * Copyright (c) 2009-2016 Wilfred Springer
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
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
