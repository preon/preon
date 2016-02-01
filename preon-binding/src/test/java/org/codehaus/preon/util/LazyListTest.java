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
package org.codehaus.preon.util;

import java.util.Iterator;
import java.util.List;

import org.codehaus.preon.el.Expression;
import org.codehaus.preon.Builder;
import org.codehaus.preon.Codec;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.buffer.BitBuffer;

import junit.framework.TestCase;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

public class LazyListTest extends TestCase {

    private BitBuffer buffer;

    private Codec codec;

    private Builder builder;

    private Resolver resolver;

    private Expression<Integer, Resolver> sizeExpr;

    @SuppressWarnings("unchecked")
    public void setUp() {
        buffer = createMock(BitBuffer.class);
        codec = createMock(Codec.class);
        builder = createMock(Builder.class);
        resolver = createMock(Resolver.class);
        sizeExpr = createMock(Expression.class);
    }

    public void testTakingElement() throws DecodingException {
        Object value = new Object();
        buffer.setBitPos(20);
        expect(codec.decode(buffer, resolver, builder)).andReturn(value);
        replay(buffer, codec, resolver, builder, sizeExpr);
        EvenlyDistributedLazyList<Object> list = new EvenlyDistributedLazyList<Object>(
                codec, 0, buffer, 10, builder, resolver, 20);
        list.get(1);
        verify(buffer, codec, resolver, builder, sizeExpr);
    }

    public void testIndexToLow() {
        replay(buffer, codec, resolver, builder, sizeExpr);
        try {
            EvenlyDistributedLazyList<Object> list = new EvenlyDistributedLazyList<Object>(
                    codec, 0, buffer, 10, builder, resolver, 20);
            list.get(-1);
            fail(); // Expecting exception
        } catch (IndexOutOfBoundsException iobe) {
            // That's ok.
        }
        verify(buffer, codec, resolver, builder, sizeExpr);
    }

    public void testIndexToHigh() {
        replay(buffer, codec, resolver, builder, sizeExpr);
        try {
            EvenlyDistributedLazyList<Object> list = new EvenlyDistributedLazyList<Object>(
                    codec, 0, buffer, 10, builder, resolver, 20);
            list.get(10);
            fail(); // Expecting exception
        } catch (IndexOutOfBoundsException iobe) {
            // That's ok.
        }
        verify(buffer, codec, resolver, builder, sizeExpr);
    }

    public void testSubList() throws DecodingException {
        Object value = new Object();
        buffer.setBitPos(20);
        expect(codec.decode(buffer, resolver, builder)).andReturn(value);
        replay(buffer, codec, resolver, builder, sizeExpr);
        EvenlyDistributedLazyList<Object> list = new EvenlyDistributedLazyList<Object>(
                codec, 0, buffer, 10, builder, resolver, 20);
        List<Object> sublist = list.subList(1, 3);
        sublist.get(0);
        verify(buffer, codec, resolver, builder, sizeExpr);
    }

    public void testIterator() throws DecodingException {
        Object value = new Object();
        buffer.setBitPos(0);
        expect(codec.decode(buffer, resolver, builder)).andReturn(value);
        buffer.setBitPos(20);
        expect(codec.decode(buffer, resolver, builder)).andReturn(value);
        buffer.setBitPos(40);
        expect(codec.decode(buffer, resolver, builder)).andReturn(value);
        replay(buffer, codec, resolver, builder, sizeExpr);
        EvenlyDistributedLazyList<Object> list = new EvenlyDistributedLazyList<Object>(
                codec, 0, buffer, 3, builder, resolver, 20);
        Iterator<Object> iterator = list.iterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
        verify(buffer, codec, resolver, builder, sizeExpr);
    }

}
