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

import org.codehaus.preon.*;
import org.codehaus.preon.annotation.Bound;
import org.codehaus.preon.annotation.BoundObject;
import org.codehaus.preon.annotation.Choices;
import org.codehaus.preon.annotation.TypePrefix;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.buffer.ByteOrder;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.AnnotatedElement;

import static junit.framework.Assert.fail;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ObjectCodecFactoryTest {

    private AnnotatedElement metadata;

    private CodecFactory delegate;

    private BitBuffer buffer;

    private Resolver resolver;

    private Builder builder;

    private BoundObject settings;

    private Choices choices;

    @Before
    public void setUp() {
        metadata = createMock(AnnotatedElement.class);
        delegate = createMock(CodecFactory.class);
        buffer = createMock(BitBuffer.class);
        resolver = createMock(Resolver.class);
        settings = createMock(BoundObject.class);
        builder = createMock(Builder.class);
        choices = createMock(Choices.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBoundObjectNoMembersTwoTypesNoPrefix() throws DecodingException {
        Codec codec1 = createMock(Codec.class);
        Codec codec2 = createMock(Codec.class);
        expect(metadata.isAnnotationPresent(Bound.class)).andReturn(false).anyTimes();
        expect(metadata.isAnnotationPresent(BoundObject.class)).andReturn(true).anyTimes();
        expect(metadata.getAnnotation(BoundObject.class)).andReturn(settings);
        expect(settings.selectFrom()).andReturn(choices).times(2);
        expect(choices.alternatives()).andReturn(new Choices.Choice[0]);
        expect(choices.defaultType()).andReturn((Class) Void.class);
        expect(settings.type()).andReturn((Class) Void.class);
        expect(settings.types()).andReturn(new Class[]{TestObject1.class, TestObject2.class})
                .times(2);
        expect(
                delegate.create((AnnotatedElement) EasyMock.isNull(), EasyMock.isA(Class.class),
                        (ResolverContext) EasyMock.isNull())).andReturn(codec1);
        expect(codec1.getTypes()).andReturn(new Class[]{TestObject1.class});
        expect(codec2.getTypes()).andReturn(new Class[]{TestObject2.class});
        expect(
                delegate.create((AnnotatedElement) EasyMock.isNull(), EasyMock.isA(Class.class),
                        (ResolverContext) EasyMock.isNull())).andReturn(codec2);
        replay(metadata, delegate, buffer, resolver, settings, codec1, codec2, choices);
        ObjectCodecFactory factory = new ObjectCodecFactory(delegate);
        try {
            Codec<TestObject1> created = factory.create(metadata, TestObject1.class, null);
            fail("Expecting failure due to missing prefixes.");
        } catch (CodecConstructionException cce) {
            // What we expect.
        }
        verify(metadata, delegate, buffer, resolver, settings, choices);
    }

    @Test
    public void testBoundObjectNoMembersTwoTypesWithPrefix() throws DecodingException {
        Codec codecTest3 = createMock(Codec.class);
        Codec codecTest4 = createMock(Codec.class);
        expect(metadata.isAnnotationPresent(Bound.class)).andReturn(false).anyTimes();
        expect(metadata.isAnnotationPresent(BoundObject.class)).andReturn(true).anyTimes();
        expect(settings.selectFrom()).andReturn(choices).times(2);
        expect(choices.alternatives()).andReturn(new Choices.Choice[0]);
        expect(choices.defaultType()).andReturn((Class) Void.class);
        expect(metadata.getAnnotation(BoundObject.class)).andReturn(settings);
        expect(settings.type()).andReturn((Class) Void.class);
        expect(settings.types()).andReturn(new Class[]{TestObject3.class, TestObject4.class})
                .times(2);
        expect(delegate.create(null, TestObject3.class, null)).andReturn(codecTest3);
        expect(codecTest3.getTypes()).andReturn(new Class<?>[]{TestObject3.class});
        expect(codecTest4.getTypes()).andReturn(new Class<?>[]{TestObject4.class});
        expect(delegate.create(null, TestObject4.class, null)).andReturn(codecTest4);
        // expect(codecTest3.getSize(resolver)).andReturn(6);
        // expect(codecTest4.getSize(resolver)).andReturn(6);
        expect(buffer.readAsLong(8, ByteOrder.LittleEndian)).andReturn(0L);
        expect(codecTest3.decode(buffer, resolver, builder)).andReturn(new TestObject3());
        replay(metadata, delegate, buffer, resolver, settings, codecTest3, codecTest4, builder,
                choices);
        ObjectCodecFactory factory = new ObjectCodecFactory(delegate);
        Codec<TestObject1> codec = factory.create(metadata, TestObject1.class, null);
        assertNotNull(codec);
        TestObject1 result = codec.decode(buffer, resolver, builder);
        assertNotNull(result);
        assertTrue(!(result instanceof TestObject4));
        assertTrue(result instanceof TestObject3);
        verify(metadata, delegate, buffer, resolver, settings, codecTest3, codecTest4, builder,
                choices);
    }

    public static class TestObject1 {

    }

    public static class TestObject2 {

    }

    @TypePrefix(size = 8, value = "0")
    public static class TestObject3 extends TestObject1 {

    }

    @TypePrefix(size = 8, value = "1")
    public static class TestObject4 extends TestObject1 {

    }

}
