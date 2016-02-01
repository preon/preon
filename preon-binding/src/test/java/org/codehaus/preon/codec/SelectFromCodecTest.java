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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.nio.ByteBuffer;

import org.codehaus.preon.el.Expressions;
import org.codehaus.preon.Builder;
import org.codehaus.preon.Codec;
import org.codehaus.preon.CodecFactory;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.ResolverContext;
import org.codehaus.preon.annotation.Choices;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.buffer.ByteOrder;
import org.codehaus.preon.buffer.DefaultBitBuffer;
import junit.framework.TestCase;

import static org.easymock.EasyMock.*;

/**
 * A collection of tests for the {@link SelectFromCodec}.
 *
 * @author Wilfred Springer (wis)
 */
public class SelectFromCodecTest extends TestCase {

    private Choices choices;

    private ResolverContext context;

    private AnnotatedElement metadata;

    private CodecFactory codecFactory;

    private Codec floatCodec;

    private Codec integerCodec;

    private Codec shortCodec;

    private Resolver resolver;

    private Builder builder;

    public void setUp() {
        context = createMock(ResolverContext.class);
        codecFactory = createMock(CodecFactory.class);
        metadata = createMock(AnnotatedElement.class);
        floatCodec = createMock(Codec.class);
        integerCodec = createMock(Codec.class);
        shortCodec = createMock(Codec.class);
        resolver = createMock(Resolver.class);
        builder = createMock(Builder.class);
        choices = new Choices() {

            public Choice[] alternatives() {
                return new Choice[]{new Choice() {

                    public String condition() {
                        return "prefix==0";
                    }

                    public Class<?> type() {
                        return Integer.class;
                    }

                    public Class<? extends Annotation> annotationType() {
                        return Choice.class;
                    }

                }, new Choice() {

                    public String condition() {
                        return "prefix==1";
                    }

                    public Class<?> type() {
                        return Short.class;
                    }

                    public Class<? extends Annotation> annotationType() {
                        return Choice.class;
                    }

                }};
            }

            public ByteOrder byteOrder() {
                return ByteOrder.BigEndian;
            }

            public Class<?> defaultType() {
                return Float.class;
            }

            public int prefixSize() {
                return 8;
            }

            public Class<? extends Annotation> annotationType() {
                return Choices.class;
            }

        };
    }

    public void testSelectFrom() throws DecodingException {
        BitBuffer buffer = new DefaultBitBuffer(ByteBuffer.wrap(new byte[]{0, 1, (byte) 255,
                (byte) 255, (byte) 255, (byte) 255}));

        // We expect all Codecs to be constructed
        expect(codecFactory.create(null, Float.class, context)).andReturn(floatCodec);
        expect(
                codecFactory.create((AnnotatedElement) isNull(), eq(Integer.class),
                        isA(ResolverContext.class))).andReturn(integerCodec);
        expect(
                codecFactory.create((AnnotatedElement) isNull(), eq(Short.class),
                        isA(ResolverContext.class))).andReturn(shortCodec);
        expect(integerCodec.decode(buffer, resolver, builder)).andReturn(new Integer(3));
        expect(integerCodec.getSize()).andReturn(Expressions.createInteger(32, Resolver.class));
        expect(shortCodec.getSize()).andReturn(Expressions.createInteger(16, Resolver.class));
        expect(shortCodec.decode(buffer, resolver, builder)).andReturn(new Short((short) 5));

        // Replay
        replay(codecFactory, context, metadata, shortCodec, integerCodec, floatCodec, resolver,
                builder);
        SelectFromCodec codec = new SelectFromCodec(Number.class, choices, context, codecFactory,
                metadata);

        // If we can potentially decode a Float, as well as an Integer, or a Short, then we cannot really predict the size.
        assertNull(codec.getSize());

        // Decode first value
        Object value = codec.decode(buffer, resolver, builder);
        assertNotNull(value);
        assertEquals(3, ((Integer) value).intValue());

        // Decode second value
        value = codec.decode(buffer, resolver, builder);
        assertNotNull(value);
        assertEquals(5, ((Short) value).intValue());

        // Verify
        verify(codecFactory, context, metadata, shortCodec, integerCodec, floatCodec, resolver,
                builder);
    }
}
