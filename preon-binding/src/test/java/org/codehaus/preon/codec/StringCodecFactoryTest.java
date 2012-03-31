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
    /* TODO: fix to new impl of NullTerminatedStringCodec
    public void testNullTerminatedString() throws DecodingException {
        expect(metadata.getAnnotation(BoundString.class)).andReturn(settings);
        expect(settings.encoding()).andReturn(Encoding.ASCII);
        expect(settings.size()).andReturn("").anyTimes();
		expect(buffer.readAsByte(8)).andReturn((byte) 'b');
		expect(buffer.readAsByte(8)).andReturn((byte) 'm');
		expect(buffer.readAsByte(8)).andReturn((byte) 0);
		expect(settings.match()).andReturn("");
        replay(settings, buffer, metadata, context, resolver);
        StringCodecFactory factory = new StringCodecFactory();
        Codec<String> codec = factory.create(metadata, String.class, context);
        assertEquals("bm", codec.decode(buffer, resolver, builder));
        verify(settings, metadata, buffer, context, resolver);
    }*/

    /* public void testDecodeASCII() throws UnsupportedEncodingException {
        byte[] buffer = "foobar".getBytes();
        assertEquals("foobar", BoundString.Encoding.ASCII.decode(buffer));
    } */ //Commented out, as no longer implemented this way

}
