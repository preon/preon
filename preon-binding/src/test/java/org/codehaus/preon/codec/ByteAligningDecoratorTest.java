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

import org.codehaus.preon.Builder;
import org.codehaus.preon.Codec;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.ResolverContext;
import org.codehaus.preon.annotation.ByteAlign;
import org.codehaus.preon.buffer.BitBuffer;


import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

public class ByteAligningDecoratorTest extends junit.framework.TestCase {

    private Codec codec;
    private AnnotatedElement metadata;
    private BitBuffer buffer;
    private Resolver resolver;
    private Builder builder;
    private ResolverContext context;

    public void setUp() {
        codec = createMock(Codec.class);
        metadata = createMock(AnnotatedElement.class);
        buffer = createMock(BitBuffer.class);
        resolver = createMock(Resolver.class);
        context = createMock(ResolverContext.class);
    }

    public void testAligningType() throws DecodingException {
        ByteAligningDecorator decorator = new ByteAligningDecorator();
        expect(codec.decode(buffer, resolver, builder)).andReturn(new Object());
        expect(buffer.getBitPos()).andReturn(12L).anyTimes();
        buffer.setBitPos(16L);
        replay(codec, metadata, buffer, resolver, context);
        Codec decorated = decorator.decorate(codec, metadata, Test1.class, context);
        assertNotSame(decorated, codec);
        decorated.decode(buffer, resolver, builder);
        verify(codec, metadata, buffer, resolver, context);
    }

    public void testAligningField() throws DecodingException {
        ByteAligningDecorator decorator = new ByteAligningDecorator();
        expect(codec.decode(buffer, resolver, builder)).andReturn(new Object());
        expect(buffer.getBitPos()).andReturn(12L).anyTimes();
        expect(metadata.isAnnotationPresent(ByteAlign.class)).andReturn(true);
        buffer.setBitPos(16L);
        replay(codec, metadata, buffer, resolver, context);
        Codec decorated = decorator.decorate(codec, metadata, Test2.class, context);
        assertNotSame(decorated, codec);
        decorated.decode(buffer, resolver, builder);
        verify(codec, metadata, buffer, resolver, context);
    }


    @ByteAlign
    private class Test1 {

    }

    private class Test2 {

    }

}
