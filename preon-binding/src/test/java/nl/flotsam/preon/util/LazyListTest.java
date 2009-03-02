/*
 * Copyright (C) 2008 Wilfred Springer
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
 * Preon; see the file COPYING. If not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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

package nl.flotsam.preon.util;

import java.util.Iterator;
import java.util.List;

import nl.flotsam.limbo.Expression;
import nl.flotsam.preon.Builder;
import nl.flotsam.preon.Codec;
import nl.flotsam.preon.DecodingException;
import nl.flotsam.preon.Resolver;
import nl.flotsam.preon.buffer.BitBuffer;
import nl.flotsam.preon.util.EvenlyDistributedLazyList;

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
        expect(codec.getSize()).andReturn(sizeExpr);
        expect(sizeExpr.eval(resolver)).andReturn(20);
        buffer.setBitPos(20);
        expect(codec.decode(buffer, resolver, builder)).andReturn(value);
        replay(buffer, codec, resolver, builder, sizeExpr);
        EvenlyDistributedLazyList<Object> list = new EvenlyDistributedLazyList<Object>(
                codec, 0, buffer, 10, builder, resolver);
        list.get(1);
        verify(buffer, codec, resolver, builder, sizeExpr);
    }

    public void testIndexToLow() {
        expect(codec.getSize()).andReturn(sizeExpr);
        expect(sizeExpr.eval(resolver)).andReturn(20);
        replay(buffer, codec, resolver, builder, sizeExpr);
        try {
            EvenlyDistributedLazyList<Object> list = new EvenlyDistributedLazyList<Object>(
                    codec, 0, buffer, 10, builder, resolver);
            list.get(-1);
            fail(); // Expecting exception
        } catch (IndexOutOfBoundsException iobe) {
            // That's ok.
        }
        verify(buffer, codec, resolver, builder, sizeExpr);
    }

    public void testIndexToHigh() {
        expect(codec.getSize()).andReturn(sizeExpr);
        expect(sizeExpr.eval(resolver)).andReturn(20);
        replay(buffer, codec, resolver, builder, sizeExpr);
        try {
            EvenlyDistributedLazyList<Object> list = new EvenlyDistributedLazyList<Object>(
                    codec, 0, buffer, 10, builder, resolver);
            list.get(10);
            fail(); // Expecting exception
        } catch (IndexOutOfBoundsException iobe) {
            // That's ok.
        }
        verify(buffer, codec, resolver, builder, sizeExpr);
    }

    public void testSubList() throws DecodingException {
        Object value = new Object();
        expect(codec.getSize()).andReturn(sizeExpr).times(2);
        expect(sizeExpr.eval(resolver)).andReturn(20).times(2);
        buffer.setBitPos(20);
        expect(codec.decode(buffer, resolver, builder)).andReturn(value);
        replay(buffer, codec, resolver, builder, sizeExpr);
        EvenlyDistributedLazyList<Object> list = new EvenlyDistributedLazyList<Object>(
                codec, 0, buffer, 10, builder, resolver);
        List<Object> sublist = list.subList(1, 3);
        sublist.get(0);
        verify(buffer, codec, resolver, builder, sizeExpr);
    }

    public void testIterator() throws DecodingException {
        Object value = new Object();
        expect(codec.getSize()).andReturn(sizeExpr);
        expect(sizeExpr.eval(resolver)).andReturn(20);
        buffer.setBitPos(0);
        expect(codec.decode(buffer, resolver, builder)).andReturn(value);
        buffer.setBitPos(20);
        expect(codec.decode(buffer, resolver, builder)).andReturn(value);
        buffer.setBitPos(40);
        expect(codec.decode(buffer, resolver, builder)).andReturn(value);
        replay(buffer, codec, resolver, builder, sizeExpr);
        EvenlyDistributedLazyList<Object> list = new EvenlyDistributedLazyList<Object>(
                codec, 0, buffer, 3, builder, resolver);
        Iterator<Object> iterator = list.iterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
        verify(buffer, codec, resolver, builder, sizeExpr);
    }

}
