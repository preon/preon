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
