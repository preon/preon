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

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.util.List;

import nl.flotsam.preon.Codec;
import nl.flotsam.preon.Codecs;
import nl.flotsam.preon.DecodingException;
import nl.flotsam.preon.Resolver;
import nl.flotsam.preon.annotation.Bound;
import nl.flotsam.preon.annotation.BoundList;
import nl.flotsam.preon.annotation.BoundNumber;
import nl.flotsam.preon.annotation.BoundObject;
import nl.flotsam.preon.annotation.If;
import nl.flotsam.preon.annotation.TypePrefix;
import nl.flotsam.preon.binding.BindingFactory;
import nl.flotsam.preon.binding.ConditionalBindingFactory;
import nl.flotsam.preon.binding.StandardBindingFactory;
import nl.flotsam.preon.buffer.ByteOrder;
import nl.flotsam.preon.codec.CompoundCodecFactory;
import nl.flotsam.preon.codec.ListCodecFactory;
import nl.flotsam.preon.codec.NumberCodecFactory;
import nl.flotsam.preon.codec.ObjectCodecFactory;
import nl.flotsam.preon.codec.IntegrationTest.Test21.Test23;

import junit.framework.TestCase;


public class IntegrationTest extends TestCase {

    private CompoundCodecFactory factory;

    private ObjectCodecFactory objectCodecFactory;

    public void setUp() {
        factory = new CompoundCodecFactory();
        BindingFactory bindingFactory = new StandardBindingFactory();
        bindingFactory = new ConditionalBindingFactory(bindingFactory);
        objectCodecFactory = new ObjectCodecFactory(factory, bindingFactory);
        factory.add(new NumberCodecFactory());
        factory.add(new ListCodecFactory(factory));
        factory.add(objectCodecFactory);
    }

    public void testAllFieldsBound() throws DecodingException {
        Codec<Test1> codec = Codecs.create(Test1.class);
        Test1 result = Codecs.decode(codec, new byte[] { 1, 2, 3 });
        assertNotNull(result);
        assertEquals(1, result.value1);
        assertEquals(2, result.value2);
        assertEquals(3, result.value3);
        assertFalse(codec.getSize().isParameterized());
        assertEquals(24, codec.getSize().eval(null).intValue());
    }

    public void testSomeFieldsTransient() throws DecodingException {
        Codec<Test2> codec = Codecs.create(Test2.class);
        ByteBuffer byteBuffer = ByteBuffer.allocate(12);
        byteBuffer.putInt(1);
        byteBuffer.putInt(2);
        byteBuffer.putInt(3);
        Test2 result = Codecs.decode(codec, byteBuffer);
        assertNotNull(result);
        assertEquals(1, result.value1);
        assertEquals(0, result.value2);
        assertEquals(2, result.value3);
        assertFalse(codec.getSize().isParameterized());
        assertEquals(64, codec.getSize().eval(null).intValue());
    }

    public void testChoice() throws DecodingException {
        Codec<Test28> codec = Codecs.create(Test28.class);
        Test28 result = Codecs.decode(codec, new byte[] { 1, 2, 3, 4 });
        assertNotNull(result);
        assertTrue(result.value instanceof Test5a);
        assertEquals(2, ((Test5a) result.value).value1);
        assertEquals(3, ((Test5a) result.value).value2);
        assertEquals(4, ((Test5a) result.value).value3);
        assertNotNull(codec.getSize());
        assertFalse(codec.getSize().isParameterized());
        assertEquals(24, codec.getSize().eval(null).intValue());
    }

    public void testListSingleElement() throws DecodingException {
        Codec<Test3> codec = Codecs.create(Test3.class);
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[] { 1, 2, 3, 4, 5, 6 });
        Test3 result = Codecs.decode(codec, byteBuffer);
        assertNotNull(result);
        assertEquals(2, result.elements.size());
        assertEquals(1, result.elements.get(0).value1);
        assertEquals(2, result.elements.get(0).value2);
        assertEquals(3, result.elements.get(0).value3);
        assertEquals(4, result.elements.get(1).value1);
        assertEquals(5, result.elements.get(1).value2);
        assertEquals(6, result.elements.get(1).value3);
        assertNotNull(codec.getSize());
        assertFalse(codec.getSize().isParameterized());
        assertEquals(48, codec.getSize(null));
    }

    public void testListMultipleElements() throws DecodingException, FileNotFoundException {
        Codec<Test4> codec = Codecs.create(Test4.class);
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[] { 1, 1, 2, 3, 2, 0, 16, 2 });
        Test4 result = Codecs.decode(codec, byteBuffer);
        assertNotNull(result);
        assertEquals(2, result.elements.size());
        assertEquals(Test5a.class, result.elements.get(0).getClass());
        assertEquals(1, ((Test5a) result.elements.get(0)).value1);
        assertEquals(2, ((Test5a) result.elements.get(0)).value2);
        assertEquals(3, ((Test5a) result.elements.get(0)).value3);
        assertEquals(Test5b.class, result.elements.get(1).getClass());
        assertEquals(16, ((Test5b) result.elements.get(1)).value1);
        assertEquals(2, ((Test5b) result.elements.get(1)).value2);
    }

    public void testConditionals() throws DecodingException {
        Codec<Test6> codec = Codecs.create(Test6.class);
        ByteBuffer buffer = null;
        Test6 result = null;
        buffer = ByteBuffer.wrap(new byte[] { 4, 1 });
        result = Codecs.decode(codec, buffer);
        assertEquals(4, result.value1);
        assertEquals(1, result.value2);
        buffer = ByteBuffer.wrap(new byte[] { 1, 4 });
        result = Codecs.decode(codec, buffer);
        assertEquals(1, result.value1);
        assertEquals(0, result.value2);
        assertNull(codec.getSize());
    }

    public void testCompoundObject() throws DecodingException {
        Codec<Test7> codec = Codecs.create(Test7.class);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[] { 1, 2, 3 });
        Test7 result = Codecs.decode(codec, buffer);
        assertNotNull(result.value1);
        assertEquals(1, result.value1.value1);
        assertEquals(2, result.value1.value2);
        assertEquals(3, result.value1.value3);
    }

    public void testInheritance() throws DecodingException {
        Codec<Test9> codec = Codecs.create(Test9.class);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[] { 7, 8 });
        Test9 result = Codecs.decode(codec, buffer);
        assertNotNull(result);
        assertEquals(7, result.value1);
        assertEquals(8, result.value2);
        assertNotNull(codec.getSize());
        assertEquals(16, codec.getSize().eval(null).intValue());
    }

    public void testArrayOfObjects() throws DecodingException {
        Codec<Test10> codec = Codecs.create(Test10.class);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[] { 1, 2, 3, 4, 5, 6 });
        Test10 value = Codecs.decode(codec, buffer);
        assertEquals(2, value.elements.length);
        assertEquals(1, value.elements[0].value1);
        assertEquals(2, value.elements[0].value2);
        assertEquals(3, value.elements[0].value3);
        assertEquals(4, value.elements[1].value1);
        assertEquals(5, value.elements[1].value2);
        assertEquals(6, value.elements[1].value3);
        assertNotNull(codec.getSize());
        assertFalse(codec.getSize().isParameterized());
        assertEquals(48, codec.getSize().eval(null).intValue());
    }

    public void testArrayOfBytes() throws DecodingException {
        Codec<Test11> codec = Codecs.create(Test11.class);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[] { 1, 2 });
        Test11 value = Codecs.decode(codec, buffer);
        assertEquals(2, value.elements.length);
        assertEquals(1, value.elements[0]);
        assertEquals(2, value.elements[1]);
    }

    public void testDynamicListSingleElement() throws DecodingException {
        Codec<Test3> codec = Codecs.create(Test3.class);
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[] { 1, 2, 3, 4, 5, 6 });
        Test3 result = Codecs.decode(codec, byteBuffer);
        assertNotNull(result);
        assertEquals(2, result.elements.size());
        assertEquals(1, result.elements.get(0).value1);
        assertEquals(2, result.elements.get(0).value2);
        assertEquals(3, result.elements.get(0).value3);
        assertEquals(4, result.elements.get(1).value1);
        assertEquals(5, result.elements.get(1).value2);
        assertEquals(6, result.elements.get(1).value3);
    }

    public void testEnclosingConstruction() throws DecodingException {
        Codec<Test13> codec = Codecs.create(Test13.class);
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[] { 3, 8 });
        Test13 result = Codecs.decode(codec, byteBuffer);
        assertEquals(3, result.size);
        assertEquals(8, result.value.value);
        assertNotNull(codec.getSize());
        assertFalse(codec.getSize().isParameterized());
        assertEquals(16, codec.getSize().eval(null).intValue());
    }

    public void testEnclosingReferences() throws DecodingException {
        Codec<Test15> codec = Codecs.create(Test15.class);
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[] { 3, (byte) 0xff });
        Test15 result = Codecs.decode(codec, byteBuffer);
        assertEquals(3, result.size);
        assertEquals(7, result.value.value);
        assertNotNull(codec.getSize());
        assertTrue(codec.getSize().isParameterized());
    }

    public void testEnclosingReferencesWithList() throws DecodingException {
        Codec<Test17> codec = Codecs.create(Test17.class);
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[] { 8, 0, (byte) 0xff });
        Test17 result = Codecs.decode(codec, byteBuffer);
        assertEquals(8, result.size);
        assertEquals(1, result.values.size());
        assertNotNull(result.values.get(0));
        assertNull(codec.getSize());
    }

    public void testDoubleNestedEnclosingReferencesWithList() throws DecodingException {
        Codec<Test21> codec = Codecs.create(Test21.class);
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[] { 8, 0, (byte) 0xff });
        Test21 result = Codecs.decode(codec, byteBuffer);
        assertEquals(8, result.size);
        assertEquals(1, result.values.size());
        Object value = result.values.get(0);
        assertNotNull(value);
        assertTrue(value instanceof Test23);
        Test23 castValue = (Test23) value;
        assertNotNull(castValue.value);
        assertEquals((byte) 0xff, castValue.value.value);
        assertNull(codec.getSize());
    }

    public void testUnboundedList() throws DecodingException {
        Codec<Test12> codec = Codecs.create(Test12.class);
        Test12 result = Codecs.decode(codec, new byte[] { 1, 2, 3, 4, 5, 6 });
        assertEquals(2, result.elements.size());
        assertEquals(1, result.elements.get(0).value1);
        assertEquals(2, result.elements.get(0).value2);
        assertEquals(3, result.elements.get(0).value3);
        assertEquals(4, result.elements.get(1).value1);
        assertEquals(5, result.elements.get(1).value2);
        assertEquals(6, result.elements.get(1).value3);
        assertNull(codec.getSize());
    }

    public void testUnboundedListMultipleElementTypes() throws DecodingException {
        Codec<Test22> codec = Codecs.create(Test22.class);
        Test22 result = Codecs.decode(codec, new byte[] { 1, 2, 3, 4, 2, (byte) 0xff, (byte) 0xff,
                7 });
        assertEquals(2, result.elements.size());
        assertEquals(2, ((Test5a) result.elements.get(0)).value1);
        assertEquals(3, ((Test5a) result.elements.get(0)).value2);
        assertEquals(4, ((Test5a) result.elements.get(0)).value3);
        assertEquals((short) 0xffff, ((Test5b) result.elements.get(1)).value1);
        assertEquals(7, ((Test5b) result.elements.get(1)).value2);
        assertNull(codec.getSize());
    }

    /**
     * Tests the situation in which the data contain an array and an item that
     * depends on the value of an element in that array.
     * 
     * @throws DecodingException
     */
    public void testSimpleLookup() throws DecodingException {
        Codec<Test26> codec = Codecs.create(Test26.class);
        Test26 result = Codecs.decode(codec, new byte[] { 1, 2, (byte) 255 });
        assertEquals(2, result.elements.length);
        assertEquals(1, result.elements[0]);
        assertEquals(2, result.elements[1]);
        assertEquals(3, result.number);
        assertNotNull(codec.getSize());
        assertTrue(codec.getSize().isParameterized());
    }

    /**
     * Tests the situation in which the data contains an array <em>as well</em>
     * as an index <em>and</em> the data that depends on the indexed array
     * element.
     * 
     * @throws DecodingException
     */
    public void testComplexLookup() throws DecodingException {
        Codec<Test27> codec = Codecs.create(Test27.class);
        Test27 result = Codecs.decode(codec, new byte[] { 1, 2, 0, (byte) 255 });
        assertEquals(2, result.elements.length);
        assertEquals(1, result.elements[0]);
        assertEquals(2, result.elements[1]);
        assertEquals(1, result.number);
    }

    public void testRecursion() throws DecodingException {
        Codec<Test29> codec = Codecs.create(Test29.class);

    }

    private static class TestResolver implements Resolver {

        public Object get(String name) {
            return null;
        }

        public Resolver getOuter() {
            return null;
        }

    }

    public static class Test1 {

        @Bound
        public byte value1;

        @Bound
        public byte value2;

        @Bound
        public byte value3;

    }

    public static class Test2 {

        @BoundNumber(endian = ByteOrder.BigEndian)
        public int value1;

        public int value2;

        @BoundNumber(endian = ByteOrder.BigEndian)
        public int value3;

    }

    public static class Test3 {

        @BoundList(type = Test1.class, size = "2")
        public List<Test1> elements;

    }

    public static class Test4 {

        @BoundList(size = "2", types = { Test5a.class, Test5b.class })
        public List<Object> elements;

    }

    @TypePrefix(size = 8, value = "1")
    public static class Test5a {

        @Bound
        public byte value1;

        @Bound
        public byte value2;

        @Bound
        public byte value3;

    }

    @TypePrefix(size = 8, value = "2")
    public static class Test5b {

        @BoundNumber(endian = ByteOrder.BigEndian)
        public short value1;

        @Bound
        public byte value2;
    }

    public static class Test6 {

        @Bound
        public byte value1;

        @If("value1 > 3")
        @Bound
        public byte value2;

    }

    public static class Test7 {

        @Bound
        public Test1 value1;

    }

    public static class Test8 {

        @Bound
        protected byte value1;

    }

    public static class Test9 extends Test8 {

        @Bound
        protected byte value2;

    }

    public static class Test10 {

        @BoundList(size = "2")
        public Test1[] elements;

    }

    public static class Test11 {

        @BoundList(size = "2")
        public byte[] elements;
    }

    public static class Test12 {

        @BoundList(type = Test1.class)
        public List<Test1> elements;

    }

    public static class Test13 {

        @Bound
        public byte size;

        @Bound
        public Test14 value;

        public class Test14 {

            @Bound
            public byte value;

        }
    }

    public static class Test15 {

        @Bound
        public byte size;

        @Bound
        public Test16 value;

        public class Test16 {

            @BoundNumber(size = "outer.size + 0")
            public byte value;

        }

    }

    public static class Test17 {

        @Bound
        public byte size;

        @BoundList(size = "1", types = { Test19.class, Test20.class })
        public List<Test18> values;

        public static class Test18 {

        }

        @TypePrefix(size = 8, value = "0")
        public class Test19 extends Test18 {

            @BoundNumber(size = "outer.size")
            public byte value;

        }

        @TypePrefix(size = 8, value = "1")
        public class Test20 extends Test18 {

            @BoundNumber(size = "outer.size")
            public byte value;

        }

    }

    public static class Test21 {

        @Bound
        public byte size;

        @BoundList(size = "1", types = { Test23.class, Test24.class })
        public List<Test22> values;

        public static class Test22 {

        }

        @TypePrefix(size = 8, value = "0")
        public class Test23 extends Test22 {

            @Bound
            public Test25 value;

            public class Test25 {

                @BoundNumber(size = "outer.outer.size")
                public byte value;

            }

        }

        @TypePrefix(size = 8, value = "1")
        public class Test24 extends Test22 {

            @BoundNumber(size = "outer.size")
            public byte value;

        }

    }

    public static class Test22 {

        @BoundList(types = { Test5a.class, Test5b.class })
        public List<Object> elements;

    }

    public static class Test26 {

        @BoundList(size = "2")
        public byte[] elements;

        @BoundNumber(size = "elements[1]")
        public byte number;

    }

    public static class Test27 {

        @BoundList(size = "2")
        public byte[] elements;

        @BoundNumber
        public byte item;

        @BoundNumber(size = "elements[item]")
        public byte number;

    }

    public static class Test28 {

        @BoundObject(types = { Test5a.class, Test5b.class })
        public Object value;

    }

    public static class Test29 {

        @Bound
        public int number;

        @If("number < 3")
        @Bound
        public Test29 value;

    }

}
