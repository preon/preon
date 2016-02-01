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

import org.codehaus.preon.el.Expression;
import org.codehaus.preon.Codec;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.annotation.LazyLoading;
import org.codehaus.preon.buffer.BitBuffer;

import junit.framework.TestCase;


import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

public class LazyLoadingCodecDecoratorTest extends TestCase {

    private Codec wrapped;

    private BitBuffer buffer;

    private LazyLoadingCodecDecorator factory;

    private AnnotatedElement metadata;

    private LazyLoading annotation;

    private Resolver resolver;

    private Expression<Integer, Resolver> sizeExpr;

    public void setUp() {
        wrapped = createMock(Codec.class);
        buffer = createMock(BitBuffer.class);
        factory = new LazyLoadingCodecDecorator();
        metadata = createMock(AnnotatedElement.class);
        annotation = createMock(LazyLoading.class);
        resolver = createMock(Resolver.class);
        sizeExpr = createMock(Expression.class);
    }

    @SuppressWarnings("unchecked")
    public void testHappyPath() throws DecodingException {

        Test test = new Test();

        // Stuff expected when Codec is getting constructed
        expect(metadata.isAnnotationPresent(LazyLoading.class))
                .andReturn(true);
        expect(wrapped.getSize()).andReturn(sizeExpr);
        expect(sizeExpr.eval(resolver)).andReturn(32);

        // Stuff expected when Test instance is constructed using Codec
        expect(buffer.getBitPos()).andReturn(64L);
        buffer.setBitPos(64L + 32);

        // Stuff expected after when Test instance is accessed
        buffer.setBitPos(64L);
        expect(wrapped.decode(buffer, resolver, null)).andReturn(test);

        // Replay
        replay(wrapped, buffer, metadata, annotation, resolver, sizeExpr);
        Codec<Test> codec = factory.decorate(wrapped, metadata, Test.class, null);
        assertNotNull(codec);
        Test result = codec.decode(buffer, resolver, null);
        assertNotNull(result);
        assertEquals("bar", result.getFoo());
        // Second time should not cause reload.
        assertEquals("bar", result.getFoo());
        verify(wrapped, buffer, metadata, annotation, resolver, sizeExpr);
    }

    public static class Test {

        public String getFoo() {
            return "bar";
        }

    }

}
