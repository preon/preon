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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.AnnotatedElement;

import org.codehaus.preon.Builder;
import org.codehaus.preon.Codec;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.ResolverContext;
import org.codehaus.preon.annotation.BoundString;
import org.codehaus.preon.annotation.BoundString.Encoding;
import org.codehaus.preon.annotation.BoundString.NullConverter;
import org.codehaus.preon.buffer.BitBuffer;

import java.nio.ByteBuffer;

import junit.framework.TestCase;


public class StringCodecFactoryTest extends TestCase {

    private BoundString settings;

    private AnnotatedElement metadata;

    private BitBuffer buffer;

    private ResolverContext context;

    private Builder builder;

    private Resolver resolver;

    public void setUp() {
        settings = createMock(BoundString.class);
        metadata = createMock(AnnotatedElement.class);
        buffer = createMock(BitBuffer.class);
        context = createMock(ResolverContext.class);
        builder = createMock(Builder.class);
        resolver = createMock(Resolver.class);
    }

    public void testDecoding() throws DecodingException {
        expect(metadata.getAnnotation(BoundString.class)).andReturn(settings);
        expect(settings.encoding()).andReturn(Encoding.ASCII);
        expect(settings.size()).andReturn("2").anyTimes();
        expect(settings.converter()).andStubReturn(NullConverter.class);
        expect(settings.match()).andReturn("");
        expect(buffer.readAsByte(8)).andReturn((byte) 'b');
		expect(buffer.readAsByte(8)).andReturn((byte) 'm');
        replay(settings, metadata, buffer, context, resolver);

        StringCodecFactory factory = new StringCodecFactory();
        Codec<String> codec = factory.create(metadata, String.class, context);
        // Null resolver, since the resolver isn't used (yet)
        String result = codec.decode(buffer, resolver, builder);
        assertNotNull(result);
        assertEquals("bm", result);

        verify(settings, metadata, buffer, context, resolver);
    }

    public void testMatching() throws DecodingException {
        expect(metadata.getAnnotation(BoundString.class)).andReturn(settings);
        expect(settings.encoding()).andReturn(Encoding.ASCII);
        expect(settings.size()).andReturn("2").anyTimes();
        expect(settings.converter()).andStubReturn(NullConverter.class);
        expect(settings.match()).andReturn("fo");
        expect(buffer.readAsByte(8)).andReturn((byte) 'b');
		expect(buffer.readAsByte(8)).andReturn((byte) 'm');
        replay(settings, metadata, buffer, context, resolver);
        StringCodecFactory factory = new StringCodecFactory();
        Codec<String> codec = factory.create(metadata, String.class, context);
        // Null resolver, since the resolver isn't used (yet)
        try {
            String result = codec.decode(buffer, resolver, builder);
            fail("Matching should have failed.");
        } catch (DecodingException de) {
            assertEquals(IllegalStateException.class, de.getCause().getClass());
        }
        verify(settings, metadata, buffer, context, resolver);
    }

    public void testNullTerminatedString() throws DecodingException {
        expect(metadata.getAnnotation(BoundString.class)).andReturn(settings);
        expect(settings.encoding()).andReturn(Encoding.ASCII);
        expect(settings.size()).andReturn("").anyTimes();
        expect(settings.converter()).andStubReturn(NullConverter.class);
		expect(buffer.readAsByte(8)).andReturn((byte) 'b');
		expect(buffer.readAsByte(8)).andReturn((byte) 'm');
		expect(buffer.readAsByte(8)).andReturn((byte) 0);
		expect(settings.match()).andReturn("");
        replay(settings, buffer, metadata, context, resolver);
        StringCodecFactory factory = new StringCodecFactory();
        Codec<String> codec = factory.create(metadata, String.class, context);
        assertEquals("bm", codec.decode(buffer, resolver, builder));
        verify(settings, metadata, buffer, context, resolver);
    }

    /* public void testDecodeASCII() throws UnsupportedEncodingException {
        byte[] buffer = "foobar".getBytes();
        assertEquals("foobar", BoundString.Encoding.ASCII.decode(buffer));
    } */ //Commented out, as no longer implemented this way

}
