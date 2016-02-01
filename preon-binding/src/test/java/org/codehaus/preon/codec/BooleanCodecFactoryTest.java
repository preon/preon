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

import java.lang.reflect.AnnotatedElement;
import java.nio.ByteBuffer;

import org.codehaus.preon.Codec;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.annotation.Bound;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.buffer.DefaultBitBuffer;


import static org.easymock.EasyMock.*;

import junit.framework.TestCase;

public class BooleanCodecFactoryTest extends TestCase {

    private AnnotatedElement metadata;

    private BitBuffer buffer;

    private BooleanCodecFactory factory;

    public void setUp() {
        metadata = createMock(AnnotatedElement.class);
        factory = new BooleanCodecFactory();
    }

    public void testConstructionBooleanPrimitive() {
        expect(metadata.isAnnotationPresent(Bound.class)).andReturn(true);
        replay(metadata);
        assertNotNull(factory.create(metadata, boolean.class, null));
        verify(metadata);
    }

    public void testConstructionBooleanNonPrimitive() {
        expect(metadata.isAnnotationPresent(Bound.class)).andReturn(true);
        replay(metadata);
        assertNotNull(factory.create(metadata, Boolean.class, null));
        verify(metadata);
    }

    public void testConstructionNoBound() {
        expect(metadata.isAnnotationPresent(Bound.class)).andReturn(false);
        replay(metadata);
        assertNull(factory.create(metadata, Boolean.class, null));
        verify(metadata);
    }

    public void testDecoding() throws DecodingException {
        BitBuffer buffer = new DefaultBitBuffer(ByteBuffer.wrap(new byte[]{(byte) 0xF0}));
        expect(metadata.isAnnotationPresent(Bound.class)).andReturn(true);
        replay(metadata);
        Codec<Boolean> codec = factory.create(metadata, Boolean.class, null);
        assertTrue(codec.decode(buffer, null, null));
        assertTrue(codec.decode(buffer, null, null));
        assertTrue(codec.decode(buffer, null, null));
        assertTrue(codec.decode(buffer, null, null));
        assertFalse(codec.decode(buffer, null, null));
        assertFalse(codec.decode(buffer, null, null));
        assertFalse(codec.decode(buffer, null, null));
        assertFalse(codec.decode(buffer, null, null));
        verify(metadata);
    }
}
