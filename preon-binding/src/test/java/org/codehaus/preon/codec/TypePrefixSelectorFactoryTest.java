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

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.preon.buffer.ByteOrder;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.Codec;
import org.codehaus.preon.CodecSelector;
import org.codehaus.preon.CodecSelectorFactory;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.ResolverContext;
import org.codehaus.preon.annotation.TypePrefix;
import org.codehaus.preon.buffer.BitBuffer;


import junit.framework.TestCase;

public class TypePrefixSelectorFactoryTest extends TestCase {

    private ResolverContext context;

    private Codec<?> codec1;

    private Codec<?> codec2;

    private Resolver resolver;

    private BitBuffer bitBuffer;

    private Reference<Resolver> reference;

    @SuppressWarnings("unchecked")
    public void setUp() {
        context = createMock(ResolverContext.class);
        codec1 = createMock(Codec.class);
        codec2 = createMock(Codec.class);
        resolver = createMock(Resolver.class);
        bitBuffer = createMock(BitBuffer.class);
        reference = createMock(Reference.class);
    }

    public void testSimplePrefixes() throws DecodingException {
        expect(codec1.getTypes()).andReturn(new Class<?>[]{Test1.class});
        expect(codec2.getTypes()).andReturn(new Class<?>[]{Test2.class});
        expect(bitBuffer.readAsLong(8, ByteOrder.LittleEndian)).andReturn(1L);
        replay(context, codec1, codec2, resolver, bitBuffer);
        CodecSelectorFactory factory = new TypePrefixSelectorFactory();
        List<Codec<?>> codecs = new ArrayList<Codec<?>>();
        codecs.add(codec1);
        codecs.add(codec2);
        CodecSelector selector = factory.create(context, codecs);
        selector.select(bitBuffer, resolver);
        verify(context, codec1, codec2, resolver, bitBuffer);
    }

    public void testPrefixesWithReferences() throws DecodingException {
        expect(codec1.getTypes()).andReturn(new Class<?>[]{Test3.class});
        expect(codec2.getTypes()).andReturn(new Class<?>[]{Test4.class});
        expect(context.selectAttribute("p")).andReturn(reference);
        expect(bitBuffer.readAsLong(8, ByteOrder.LittleEndian)).andReturn(1L);
        expect(reference.resolve(resolver)).andReturn(-2);
        expect(reference.getType()).andReturn((Class) Integer.class).anyTimes();
        replay(context, codec1, codec2, resolver, bitBuffer, reference);
        CodecSelectorFactory factory = new TypePrefixSelectorFactory();
        List<Codec<?>> codecs = new ArrayList<Codec<?>>();
        codecs.add(codec1);
        codecs.add(codec2);
        CodecSelector selector = factory.create(context, codecs);
        selector.select(bitBuffer, resolver);
        verify(context, codec1, codec2, resolver, bitBuffer, reference);
    }

    @TypePrefix(value = "1", size = 8)
    private static class Test1 {

    }

    @TypePrefix(value = "2", size = 8)
    private static class Test2 {

    }

    @TypePrefix(value = "p + 3", size = 8)
    private static class Test3 {

    }

    @TypePrefix(value = "2", size = 8)
    private static class Test4 {

    }

}
