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
 * Preon; see the file COPYING. If not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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

import java.lang.reflect.AnnotatedElement;

import junit.framework.TestCase;

import nl.flotsam.preon.Codec;
import nl.flotsam.preon.CodecFactory;
import nl.flotsam.preon.DecodingException;
import nl.flotsam.preon.Resolver;
import nl.flotsam.preon.annotation.Bound;
import nl.flotsam.preon.annotation.BoundNumber;
import nl.flotsam.preon.buffer.BitBuffer;
import nl.flotsam.preon.buffer.ByteOrder;
import nl.flotsam.preon.codec.NumberCodecFactory;

import org.easymock.EasyMock;

/**
 * A collection of tests for the {@link NumberCodecFactory}.
 * 
 * @author Wilfred Springer
 * 
 */
public class NumberCodecFactoryTest extends TestCase {

    private AnnotatedElement metadata;

    private CodecFactory delegate;

    private BitBuffer buffer;

    private Resolver resolver;

    private Bound bound;

    private BoundNumber boundNumber;

    private NumberCodecFactory factory = new NumberCodecFactory();

    public void setUp() {
        metadata = EasyMock.createMock(AnnotatedElement.class);
        delegate = EasyMock.createMock(CodecFactory.class);
        buffer = EasyMock.createMock(BitBuffer.class);
        resolver = EasyMock.createMock(Resolver.class);
        bound = EasyMock.createMock(Bound.class);
        boundNumber = EasyMock.createMock(BoundNumber.class);
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

    public abstract class Kit<T> {

        @SuppressWarnings("unchecked")
        public void test(ByteOrder endian, String size, int readSize, T value,
                String match, boolean expectException) throws DecodingException {
            EasyMock.expect(metadata.isAnnotationPresent(Bound.class))
                    .andReturn(false);
            EasyMock.expect(metadata.isAnnotationPresent(BoundNumber.class))
                    .andReturn(true);
            EasyMock.expect(metadata.getAnnotation(BoundNumber.class))
                    .andReturn(boundNumber);
            EasyMock.expect(boundNumber.byteOrder()).andReturn(endian);
            EasyMock.expect(boundNumber.size()).andReturn(size);
            EasyMock.expect(boundNumber.match()).andReturn(
                    match == null ? "" : match).anyTimes();
            verifyRead(readSize, endian, value);
            EasyMock.replay(metadata, delegate, buffer, resolver, bound,
                    boundNumber);
            Codec<T> codec = (Codec<T>) factory.create(metadata, value
                    .getClass(), null);
            try {
                T result = codec.decode(buffer, resolver, null);
                if (expectException) {
                    fail();
                }
                assertEquals(value, result);
            } catch (DecodingException de) {
                if (!expectException)
                    fail("Unexpected exception: " + de.getMessage());
            }
            EasyMock.verify(metadata, delegate, buffer, resolver, bound,
                    boundNumber);
            EasyMock.reset(metadata, delegate, buffer, resolver, bound,
                    boundNumber);
        }

        public abstract void verifyRead(int readSize, ByteOrder endian, T value);

    }

    public class IntegerKit extends Kit<Integer> {

        @Override
        public void verifyRead(int readSize, ByteOrder endian, Integer value) {
            EasyMock.expect(buffer.readAsInt(readSize, endian))
                    .andReturn(value);
        }
    }

    public class LongKit extends Kit<Long> {

        @Override
        public void verifyRead(int readSize, ByteOrder endian, Long value) {
            EasyMock.expect(buffer.readAsLong(readSize, endian)).andReturn(
                    value);
        }
    }

    public class ShortKit extends Kit<Short> {

        @Override
        public void verifyRead(int readSize, ByteOrder endian, Short value) {
            EasyMock.expect(buffer.readAsShort(readSize, endian)).andReturn(
                    value);
        }

    }

    public class FloatKit extends Kit<Float> {

        @Override
        public void verifyRead(int readSize, ByteOrder endian, Float value) {
            EasyMock.expect(buffer.readAsInt(readSize, endian)).andReturn(
                    Float.floatToIntBits(value));
        }

    }

    public class DoubleKit extends Kit<Double> {

        @Override
        public void verifyRead(int readSize, ByteOrder endian, Double value) {
            EasyMock.expect(buffer.readAsLong(readSize, endian)).andReturn(
                    Double.doubleToLongBits(value));
        }

    }

    public class ByteKit extends Kit<Byte> {

        @Override
        public void verifyRead(int readSize, ByteOrder endian, Byte value) {
            EasyMock.expect(buffer.readAsByte(readSize, endian)).andReturn(
                    value);
        }

    }

}
