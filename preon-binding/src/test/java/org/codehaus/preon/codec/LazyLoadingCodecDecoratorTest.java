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
