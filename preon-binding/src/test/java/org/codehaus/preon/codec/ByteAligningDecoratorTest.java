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
