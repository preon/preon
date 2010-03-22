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
package nl.flotsam.preon.codec;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.lang.reflect.AnnotatedElement;
import java.util.List;

import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Expressions;
import nl.flotsam.preon.Builder;
import nl.flotsam.preon.Codec;
import nl.flotsam.preon.CodecFactory;
import nl.flotsam.preon.DecodingException;
import nl.flotsam.preon.Resolver;
import nl.flotsam.preon.ResolverContext;
import nl.flotsam.preon.annotation.BoundList;
import nl.flotsam.preon.buffer.BitBuffer;
import nl.flotsam.preon.buffer.BitBufferUnderflowException;
import nl.flotsam.preon.codec.ListCodecFactory;

import junit.framework.TestCase;


public class ListCodecFactoryTest extends TestCase {

    private CodecFactory delegate;

    private AnnotatedElement metadata;

    private BoundList listSettings;

    private Codec elementCodec;

    private BitBuffer buffer;

    private Resolver resolver;

    private Builder builder;

    private ResolverContext context;
    
    private Expression<Integer,Resolver> sizeExpr;

    public void setUp() {
        delegate = createMock(CodecFactory.class);
        metadata = createMock(AnnotatedElement.class);
        listSettings = createMock(BoundList.class);
        elementCodec = createMock(Codec.class);
        buffer = createMock(BitBuffer.class);
        resolver = createMock(Resolver.class);
        builder = createMock(Builder.class);
        context = createMock(ResolverContext.class);
        sizeExpr = createMock(Expression.class);
    }

    public void testStaticListDecoding() throws DecodingException {

        // Set expectations for creating the Codec
        expect(metadata.getAnnotation(BoundList.class)).andReturn(listSettings);
        expect(listSettings.type()).andReturn((Class) TestElement.class).anyTimes();
        expect(listSettings.size()).andReturn("3").times(2);
        expect(listSettings.offset()).andReturn("");
        expect(delegate.create(isA(AnnotatedElement.class), eq(TestElement.class), eq(context)))
                .andReturn(elementCodec);
        expect(buffer.getBitPos()).andReturn(Long.valueOf(10));
        expect(elementCodec.getSize()).andReturn(Expressions.createInteger(10, Resolver.class)).times(2);

        // Set expectations for retrieving the values
        TestElement element1 = new TestElement();
        buffer.setBitPos(10); // Moving to first value.
        expect(elementCodec.decode(buffer, resolver, builder)).andReturn(element1);

        // Replay
        replay(delegate, metadata, listSettings, elementCodec, buffer, resolver, builder, context);
        ListCodecFactory factory = new ListCodecFactory(delegate);
        Codec<List> codec = factory.create(metadata, List.class, context);
        List result = codec.decode(buffer, resolver, builder);
        assertEquals(3, result.size());
        Object value1 = result.get(0);
        assertNotNull(value1);
        assertEquals(element1, value1);

        // Verification
        verify(delegate, metadata, listSettings, elementCodec, buffer, resolver, builder, context);
    }

    public void testDynamicListWithBitBufferUnderFlowException() throws DecodingException {

        // Set expectations for creating the Codec
        expect(metadata.getAnnotation(BoundList.class)).andReturn(listSettings);
        expect(listSettings.type()).andReturn((Class) TestElement.class).anyTimes();
        expect(listSettings.size()).andReturn("");
        expect(delegate.create(isA(AnnotatedElement.class), eq(TestElement.class), eq(context)))
                .andReturn(elementCodec);

        expect(buffer.getBitPos()).andReturn(1L);
        expect(elementCodec.decode(buffer, resolver, builder)).andReturn(new TestElement());
        expect(buffer.getBitPos()).andReturn(2L);
        expect(elementCodec.decode(buffer, resolver, builder)).andThrow(
                new BitBufferUnderflowException(3, 4));

        // Replay
        replay(delegate, metadata, listSettings, elementCodec, buffer, resolver);
        ListCodecFactory factory = new ListCodecFactory(delegate);
        Codec<List> codec = factory.create(metadata, List.class, context);
        List result = codec.decode(buffer, resolver, builder);
        assertEquals(1, result.size());

        // Verification
        verify(delegate, metadata, listSettings, elementCodec, buffer, resolver);
    }

    public void testDynamicListWithDecodingException() throws DecodingException {

        // Set expectations for creating the Codec
        expect(metadata.getAnnotation(BoundList.class)).andReturn(listSettings);
        expect(listSettings.type()).andReturn((Class) TestElement.class).anyTimes();
        expect(listSettings.size()).andReturn("");
        expect(delegate.create(isA(AnnotatedElement.class), eq(TestElement.class), eq(context)))
                .andReturn(elementCodec);

        expect(buffer.getBitPos()).andReturn(1L);
        expect(elementCodec.decode(buffer, resolver, builder)).andReturn(new TestElement());
        expect(buffer.getBitPos()).andReturn(2L);
        expect(elementCodec.decode(buffer, resolver, builder)).andThrow(
                new DecodingException("Whatever"));
        buffer.setBitPos(2L);

        // Replay
        replay(delegate, metadata, listSettings, elementCodec, buffer, resolver);
        ListCodecFactory factory = new ListCodecFactory(delegate);
        Codec<List> codec = factory.create(metadata, List.class, context);
        List result = codec.decode(buffer, resolver, builder);
        assertEquals(1, result.size());

        // Verification
        verify(delegate, metadata, listSettings, elementCodec, buffer, resolver);
    }

    public static class TestElement {

    }

}
