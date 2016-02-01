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
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.lang.reflect.AnnotatedElement;

import junit.framework.TestCase;
import org.codehaus.preon.Codec;
import org.codehaus.preon.CodecFactory;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.annotation.Bound;
import org.codehaus.preon.annotation.BoundNumber;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.buffer.ByteOrder;

/**
 * A collection of tests for the {@link org.codehaus.preon.codec.NumericCodec.Factory}.
 *
 * @author Wilfred Springer
 */
public class NumberCodecFactoryTest extends TestCase {

    private AnnotatedElement metadata;

    private CodecFactory delegate;

    private BitBuffer buffer;

    private Resolver resolver;

    private Bound bound;

    private BoundNumber boundNumber;

    private NumericCodec.Factory factory = new NumericCodec.Factory();

    public void setUp() {
        metadata = createMock(AnnotatedElement.class);
        delegate = createMock(CodecFactory.class);
        buffer = createMock(BitBuffer.class);
        resolver = createMock(Resolver.class);
        bound = createMock(Bound.class);
        boundNumber = createMock(BoundNumber.class);
    }

    public void testDecodingHex() {
        System.out.println(Long.parseLong("CAFEBABE", 16));
    }

    public void testDecodingInteger() throws DecodingException {
        Kit<Integer> kit = new IntegerKit();
        kit.test(ByteOrder.BigEndian, "", 32, 256, null, false);
        kit.test(ByteOrder.LittleEndian, "", 32, 256, null, false);
        kit.test(ByteOrder.BigEndian, "4", 4, 16, null, false);
        kit.test(ByteOrder.BigEndian, "8", 8, 256, null, false);
    }

    public void testDecodingShort() throws DecodingException {
        Kit<Short> kit = new ShortKit();
        kit.test(ByteOrder.BigEndian, "", 16, (short) 256, null, false);
        kit.test(ByteOrder.LittleEndian, "", 16, (short) 256, null, false);
        kit.test(ByteOrder.BigEndian, "4", 4, (short) 16, null, false);
        kit.test(ByteOrder.BigEndian, "8", 8, (short) 256, null, false);
        kit.test(ByteOrder.LittleEndian, "", 16, (short) 256, "256", false);
        kit.test(ByteOrder.LittleEndian, "", 16, (short) 256, "25", true);
    }

    public void testDecodingByte() throws DecodingException {
        Kit<Byte> kit = new ByteKit();
        kit.test(ByteOrder.BigEndian, "", 8, (byte) 256, null, false);
        kit.test(ByteOrder.LittleEndian, "", 8, (byte) 256, null, false);
        kit.test(ByteOrder.BigEndian, "4", 4, (byte) 16, null, false);
        kit.test(ByteOrder.BigEndian, "8", 8, (byte) 256, null, false);
    }

    public void testDecodingLong() throws DecodingException {
        Kit<Long> kit = new LongKit();
        kit.test(ByteOrder.BigEndian, "", 64, 256L, null, false);
        kit.test(ByteOrder.LittleEndian, "", 64, 256L, null, false);
        kit.test(ByteOrder.BigEndian, "4", 4, 16L, null, false);
        kit.test(ByteOrder.BigEndian, "8", 8, 256L, null, false);
    }

    public void testDecodingFloat() throws DecodingException {
        Kit<Float> kit = new FloatKit();
        kit.test(ByteOrder.BigEndian, "", 32, 5.0f, null, false);
        kit.test(ByteOrder.BigEndian, "", 32, Float.MAX_VALUE, null, false);
        kit.test(ByteOrder.BigEndian, "", 32, Float.MIN_VALUE, null, false);
        kit.test(ByteOrder.BigEndian, "", 32, Float.NaN, null, false);
    }

    public void testDecodingDouble() throws DecodingException {
        Kit<Double> kit = new DoubleKit();
        kit.test(ByteOrder.BigEndian, "", 64, 5.0d, null, false);
        kit.test(ByteOrder.BigEndian, "", 64, Double.MAX_VALUE, null, false);
        kit.test(ByteOrder.BigEndian, "", 64, Double.MIN_VALUE, null, false);
        kit.test(ByteOrder.BigEndian, "", 64, Double.NaN, null, false);
        kit.test(ByteOrder.BigEndian, "32", 32, 5.0d, null, false);
    }

    public void testDecodingWithTypeOverrideDoubleInteger() throws DecodingException {
        Kit<Integer> kit = new DoubleAsIntegerKit();
        kit.test(ByteOrder.BigEndian, "", 64, 5, Double.class, null, false);
        kit.test(ByteOrder.BigEndian, "32", 32, 5, Double.class, null, false);
    }

    public void testDecodingWithTypeOverrideIntegerDouble() throws DecodingException {
        Kit<Double> kit = new IntegerAsDoubleKit();
        kit.test(ByteOrder.BigEndian, "", 32, 5.0, Integer.class, null, false);
        kit.test(ByteOrder.BigEndian, "64", 64, 5.0, Integer.class, null, false);
    }

    public abstract class Kit<T> {

        public void test(ByteOrder endian, String size, int readSize, T value,
                         String match, boolean expectException) throws DecodingException {
            test(endian, size, readSize, value, Number.class, match, expectException);
        }

        @SuppressWarnings("unchecked")
        public void test(ByteOrder endian, String size, int readSize, T value, Class<? extends Number> typeOverride,
                         String match, boolean expectException) throws DecodingException {
            expect(metadata.isAnnotationPresent(BoundNumber.class))
                    .andReturn(true);
            expect(metadata.getAnnotation(BoundNumber.class))
                    .andReturn(boundNumber);
            expect(boundNumber.type()).andStubReturn(typeOverride);
            expect(metadata.isAnnotationPresent(Bound.class))
                    .andReturn(false);
            expect(metadata.isAnnotationPresent(BoundNumber.class))
                    .andReturn(true);
            expect(metadata.getAnnotation(BoundNumber.class))
                    .andReturn(boundNumber);
            expect(boundNumber.byteOrder()).andReturn(endian);
            expect(boundNumber.size()).andReturn(size);
            expect(boundNumber.match()).andReturn(
                    match == null ? "" : match).anyTimes();
            verifyRead(readSize, endian, value);
            replay(metadata, delegate, buffer, resolver, bound,
                    boundNumber);
            Codec<T> codec = (Codec<T>) factory.create(metadata, value
                    .getClass(), null);
            try {
                T result = codec.decode(buffer, resolver, null);
                if (expectException) {
                    fail();
                }
                verifyResult(value, result);
            } catch (DecodingException de) {
                if (!expectException)
                    fail("Unexpected exception: " + de.getMessage());
            }
            verify(metadata, delegate, buffer, resolver, bound,
                    boundNumber);
            reset(metadata, delegate, buffer, resolver, bound,
                    boundNumber);
        }

        public abstract void verifyRead(int readSize, ByteOrder endian, T value);

        public void verifyResult(Object expected, Object actual) {
            assertEquals(expected, actual);
        }

    }

    public class IntegerKit extends Kit<Integer> {

        @Override
        public void verifyRead(int readSize, ByteOrder endian, Integer value) {
            expect(buffer.readAsInt(readSize, endian))
                    .andReturn(value);
        }
    }

    public class LongKit extends Kit<Long> {

        @Override
        public void verifyRead(int readSize, ByteOrder endian, Long value) {
            expect(buffer.readAsLong(readSize, endian)).andReturn(
                    value);
        }
    }

    public class ShortKit extends Kit<Short> {

        @Override
        public void verifyRead(int readSize, ByteOrder endian, Short value) {
            expect(buffer.readAsShort(readSize, endian)).andReturn(
                    value);
        }

    }

    public class FloatKit extends Kit<Float> {

        @Override
        public void verifyRead(int readSize, ByteOrder endian, Float value) {
            expect(buffer.readAsInt(readSize, endian)).andReturn(
                    Float.floatToIntBits(value));
        }

    }

    public class DoubleKit extends Kit<Double> {

        @Override
        public void verifyRead(int readSize, ByteOrder endian, Double value) {
            expect(buffer.readAsLong(readSize, endian)).andReturn(
                    Double.doubleToLongBits(value));
        }

    }

    public class ByteKit extends Kit<Byte> {

        @Override
        public void verifyRead(int readSize, ByteOrder endian, Byte value) {
            expect(buffer.readAsByte(readSize, endian)).andReturn(
                    value);
        }

    }

    public class DoubleAsIntegerKit extends Kit<Integer> {

        @Override
        public void verifyRead(int readSize, ByteOrder endian, Integer value) {
            new DoubleKit().verifyRead(readSize, endian, value.doubleValue());
        }

        @Override
        public void verifyResult(Object expected, Object actual) {
            assertEquals(((Number) expected).doubleValue(), actual);
        }
    }

    public class IntegerAsDoubleKit extends Kit<Double> {

        @Override
        public void verifyRead(int readSize, ByteOrder endian, Double value) {
            new IntegerKit().verifyRead(readSize, endian, value.intValue());
        }

        @Override
        public void verifyResult(Object expected, Object actual) {
            assertEquals(((Number) expected).intValue(), actual);
        }
    }

}
