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
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.AnnotatedElement;
import java.nio.ByteBuffer;

import nl.flotsam.preon.Builder;
import nl.flotsam.preon.Codec;
import nl.flotsam.preon.DecodingException;
import nl.flotsam.preon.Resolver;
import nl.flotsam.preon.ResolverContext;
import nl.flotsam.preon.annotation.BoundString;
import nl.flotsam.preon.annotation.BoundString.Encoding;
import nl.flotsam.preon.annotation.BoundString.NullConverter;
import nl.flotsam.preon.buffer.BitBuffer;
import nl.flotsam.preon.buffer.DefaultBitBuffer;
import nl.flotsam.preon.codec.StringCodecFactory;

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
        expect(buffer.readAsByte(8)).andReturn((byte) 0x00);
        expect(settings.match()).andReturn("");
        replay(settings, buffer, metadata, context, resolver);
        StringCodecFactory factory = new StringCodecFactory();
        Codec<String> codec = factory.create(metadata, String.class, context);
        assertEquals("bm", codec.decode(buffer, resolver, builder));
        verify(settings, metadata, buffer, context, resolver);
    }

    public void testDecodeASCII() throws UnsupportedEncodingException {
        byte[] buffer = "foobar".getBytes();
        assertEquals("foobar", BoundString.Encoding.ASCII.decode(buffer));
    }

    public void testDecodeAZ09() throws UnsupportedEncodingException {
        // byte[][] buffer = {
        // { -104, -104, -104, 9, 102, 30, 108, -59, -128, -104, -98,
        // -108, -108, -115, -110, -112, -115, -104, -107, -103,
        // -110, -107, -105, -112, -108 },
        // { 21, 75, 17, -57, -13, 36, 77, 20, 74, 20, -40, 29, -57, -49,
        // -128, -108, -103, -104, -105 },
        // { 21, 75, 17, -57, -13, 36, 77, 20, 74, 20, -40, 29, -57, -49,
        // -128, -111, -111, -106, -112, -109 },
        // { 93, 7, 49, 2, -46, -19, 33, 95, 77, 6, 44, -105 } };
        byte[][] buffer = new byte[][] { { -85, 83, -99, 53, 21, 34 },
                { 29, 43, 20, -47, 16, -50, -56, -23 } };
        // byte[][] buffer = new byte[][] { { (byte) 0xab, 0x53, (byte) 0x9d,
        // 0x35, 0x15, 0x22 } };
        for (int i = 0; i < buffer.length; i++) {
            System.out.println(buffer[i].length);
            System.out.println(BoundString.Encoding.AZ09.decode(buffer[i]));
        }
    }

    public void testEncodeAndDecodeAZ09() throws UnsupportedEncodingException {
        String AZ09 = "\0abcdefghijklmnopqrstuvwxyz0123456789 .-";
        // 0xab 0x53 0x9d 0x35 0x15 0x22
        String text = "somewhere";
        byte[] result = new byte[((int) Math.ceil((double) text.length() / 3)) * 2];
        int pos = 0;
        for (int i = 0; i < text.length(); i = i + 3) {
            int value = 0;
            for (int j = 2; j >= 0; j--) {
                if (i + j < text.length()) {
                    value *= 40;
                    value += AZ09.indexOf(text.charAt(i + j));
                }
            }
            result[pos++] = (byte) (0xFF & value);
            result[pos++] = (byte) (0xFF & (value >> 8));
        }
        assertEquals(text, Encoding.AZ09.decode(result));
    }

    public void testDecodeTriple() throws UnsupportedEncodingException {

        byte[] buffer = { 18, 11, 32, 88, 18, 68, 9, -56, -51, -23, 25, -57,
                32, 77, 20, 68, 65, 104, -51, -13, 96, -89, -45, -98, -109,
                -107, -111, -115, -110, -111, -108, -106, -112, -109, -105,
                -105, -112 };
        // byte[] buffer = { (byte) 0xc1, (byte) 0x8f, (byte) 0xf1, (byte) 0xe4,
        // (byte) 0xbc, (byte) 0xc8, (byte) 0xbc, (byte) 0xd6 };
        assertEquals("auto vaga comŽrcio de autom—veis>351-214603770",
                Encoding.TRIPLE.decode(buffer));
    }

    public void testDecodeNamePhone() throws UnsupportedEncodingException {
        byte[] buffer = { 0x20, (byte) 0x88, 0x41, (byte) 0x8a, 0x39, 0x28,
                (byte) 0xa9, (byte) 0xc5, (byte) 0x9a, 0x7b, 0x30, (byte) 0xca,
                0x49, (byte) 0xab, (byte) 0xbd, 0x58, (byte) 0x87, 0x0c,
                (byte) 0x95, 0x1d, (byte) 0xa6, 0x02 };
        assertEquals("abcdefghijklmnoprstuvwxyz>0123456789",
                BoundString.Encoding.NAME_PHONE.decode(buffer));
    }

}
