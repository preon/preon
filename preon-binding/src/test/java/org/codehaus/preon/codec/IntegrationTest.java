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

import org.codehaus.preon.*;
import org.codehaus.preon.Codecs.DocumentType;
import org.codehaus.preon.annotation.*;
import org.codehaus.preon.annotation.Choices.Choice;
import org.codehaus.preon.binding.BindingFactory;
import org.codehaus.preon.binding.ConditionalBindingFactory;
import org.codehaus.preon.binding.StandardBindingFactory;
import org.codehaus.preon.codec.IntegrationTest.Test21.Test23;
import org.codehaus.preon.el.ImportStatic;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import static org.codehaus.preon.buffer.ByteOrder.BigEndian;
import static org.junit.Assert.*;

public class IntegrationTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private CompoundCodecFactory factory;

    private ObjectCodecFactory objectCodecFactory;

    @Before
    public void setUp() {
        factory = new CompoundCodecFactory();
        BindingFactory bindingFactory = new StandardBindingFactory();
        bindingFactory = new ConditionalBindingFactory(bindingFactory);
        objectCodecFactory = new ObjectCodecFactory(factory, bindingFactory);
        factory.add(new NumericCodec.Factory());
        factory.add(new ListCodecFactory(factory));
        factory.add(objectCodecFactory);
    }

    @Test
    public void testAllFieldsBound() throws DecodingException, FileNotFoundException {
        Codec<Test1> codec = Codecs.create(Test1.class);
        Test1 result = Codecs.decode(codec, new byte[]{1, 2, 3});
        assertNotNull(result);
        assertEquals(1, result.value1);
        assertEquals(2, result.value2);
        assertEquals(3, result.value3);
        assertFalse(codec.getSize().isParameterized());
        assertEquals(24, codec.getSize().eval(null).intValue());
    }

    @Test
    public void testEncodingBytes() throws IOException {
        Codec<Test1> codec = Codecs.create(Test1.class);
        Test1 object = new Test1();
        object.value1 = 12;
        object.value2 = 13;
        object.value3 = 14;
        byte[] encoded = Codecs.encode(object, codec);
        assertNotNull(encoded);
        assertEquals(3, encoded.length);
        assertEquals(12, encoded[0]);
        assertEquals(13, encoded[1]);
        assertEquals(14, encoded[2]);
    }

    @Test
    public void testSomeFieldsTransient() throws DecodingException, FileNotFoundException {
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

    @Test
    public void testEncodingSomeFieldsTransient() throws IOException, DecodingException {
        Codec<Test2> codec = Codecs.create(Test2.class);
        Test2 object = new Test2();
        object.value1 = 1;
        object.value2 = 2;
        object.value3 = 3;
        byte[] encoded = Codecs.encode(object, codec);
        Test2 replica = Codecs.decode(codec, encoded);
        assertEquals(1, replica.value1);
        assertEquals(0, replica.value2);
        assertEquals(3, replica.value3);
    }

    @Test
    public void testChoice() throws DecodingException, FileNotFoundException {
        Codec<Test28> codec = Codecs.create(Test28.class);
        Test28 result = Codecs.decode(codec, new byte[]{1, 2, 3, 4});
        assertNotNull(result);
        assertTrue(result.value instanceof Test5a);
        assertEquals(2, ((Test5a) result.value).value1);
        assertEquals(3, ((Test5a) result.value).value2);
        assertEquals(4, ((Test5a) result.value).value3);
        assertNotNull(codec.getSize());
        assertFalse(codec.getSize().isParameterized());
        assertEquals(32, codec.getSize().eval(null).intValue());
    }

    @Test
    public void testEncodingChoice() throws DecodingException, IOException {
        Codec<Test28> codec = Codecs.create(Test28.class);
        Test5a embedded = new Test5a();
        embedded.value1 = 2;
        embedded.value2 = 3;
        embedded.value3 = 4;
        Test28 object = new Test28();
        object.value = embedded;
        byte[] buffer = Codecs.encode(object, codec);
        assertEquals(4, buffer.length);
        assertEquals(1, buffer[0]);
        assertEquals(2, buffer[1]);
        assertEquals(3, buffer[2]);
        assertEquals(4, buffer[3]);
    }

    @Test
    public void testListSingleElement() throws DecodingException, FileNotFoundException {
        Codec<Test3> codec = Codecs.create(Test3.class);
        ByteBuffer byteBuffer = ByteBuffer
                .wrap(new byte[]{1, 2, 3, 4, 5, 6});
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
        assertEquals(Integer.valueOf(48), codec.getSize().eval(null));
    }

    @Test
    public void testListMultipleElements() throws DecodingException,
            FileNotFoundException {
        Codec<Test4> codec = Codecs.create(Test4.class);
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{1, 1, 2, 3, 2, 0,
                16, 2});
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

    @Test
    public void testConditionals() throws DecodingException, FileNotFoundException {
        Codec<Test6> codec = Codecs.create(Test6.class);
        ByteBuffer buffer = null;
        Test6 result = null;
        buffer = ByteBuffer.wrap(new byte[]{4, 1});
        result = Codecs.decode(codec, buffer);
        assertEquals(4, result.value1);
        assertEquals(1, result.value2);
        buffer = ByteBuffer.wrap(new byte[]{1, 4});
        result = Codecs.decode(codec, buffer);
        assertEquals(1, result.value1);
        assertEquals(0, result.value2);
        assertNotNull(codec.getSize());
    }

    @Test
    public void testCompoundObject() throws DecodingException, FileNotFoundException {
        Codec<Test7> codec = Codecs.create(Test7.class);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{1, 2, 3});
        Test7 result = Codecs.decode(codec, buffer);
        assertNotNull(result.value1);
        assertEquals(1, result.value1.value1);
        assertEquals(2, result.value1.value2);
        assertEquals(3, result.value1.value3);
    }

    @Test
    public void testInheritance() throws DecodingException {
        Codec<Test9> codec = Codecs.create(Test9.class);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{7, 8});
        Test9 result = Codecs.decode(codec, buffer);
        assertNotNull(result);
        assertEquals(7, result.value1);
        assertEquals(8, result.value2);
        assertNotNull(codec.getSize());
        assertEquals(16, codec.getSize().eval(null).intValue());
    }

    @Test
    public void testArrayOfObjects() throws DecodingException, FileNotFoundException {
        Codec<Test10> codec = Codecs.create(Test10.class);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{1, 2, 3, 4, 5, 6});
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

    @Test
    public void testArrayOfBytes() throws DecodingException, FileNotFoundException {
        Codec<Test11> codec = Codecs.create(Test11.class);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{1, 2});
        Test11 value = Codecs.decode(codec, buffer);
        assertEquals(2, value.elements.length);
        assertEquals(1, value.elements[0]);
        assertEquals(2, value.elements[1]);
    }

    @Test
    public void testDynamicListSingleElement() throws DecodingException, FileNotFoundException {
        Codec<Test3> codec = Codecs.create(Test3.class);
        ByteBuffer byteBuffer = ByteBuffer
                .wrap(new byte[]{1, 2, 3, 4, 5, 6});
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

    @Test
    public void testEnclosingConstruction() throws DecodingException, FileNotFoundException {
        Codec<Test13> codec = Codecs.create(Test13.class);
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{3, 8});
        Test13 result = Codecs.decode(codec, byteBuffer);
        assertEquals(3, result.size);
        assertEquals(8, result.value.value);
        assertNotNull(codec.getSize());
        assertFalse(codec.getSize().isParameterized());
        assertEquals(16, codec.getSize().eval(null).intValue());
    }

    @Test
    public void testEnclosingReferences() throws DecodingException {
        Codec<Test15> codec = Codecs.create(Test15.class);
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{3, (byte) 0xff});
        Test15 result = Codecs.decode(codec, byteBuffer);
        assertEquals(3, result.size);
        assertEquals(7, result.value.fooBar);
        assertNotNull(codec.getSize());
        assertTrue(codec.getSize().isParameterized());
    }

    @Test
    public void testEnclosingReferencesWithList() throws DecodingException, FileNotFoundException {
        Codec<Test17> codec = Codecs.create(Test17.class);
        ByteBuffer byteBuffer = ByteBuffer
                .wrap(new byte[]{8, 0, (byte) 0xff});
        Test17 result = Codecs.decode(codec, byteBuffer);
        assertEquals(8, result.size);
        assertEquals(1, result.values.size());
        assertNotNull(result.values.get(0));
        assertNull(codec.getSize());
    }

    @Test
    public void testDoubleNestedEnclosingReferencesWithList()
            throws DecodingException, FileNotFoundException {
        Codec<Test21> codec = Codecs.create(Test21.class);
        ByteBuffer byteBuffer = ByteBuffer
                .wrap(new byte[]{8, 0, (byte) 0xff});
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

    @Test
    public void testUnboundedList() throws DecodingException, FileNotFoundException {
        Codec<Test12> codec = Codecs.create(Test12.class);
        Test12 result = Codecs.decode(codec, new byte[]{1, 2, 3, 4, 5, 6});
        assertEquals(2, result.elements.size());
        assertEquals(1, result.elements.get(0).value1);
        assertEquals(2, result.elements.get(0).value2);
        assertEquals(3, result.elements.get(0).value3);
        assertEquals(4, result.elements.get(1).value1);
        assertEquals(5, result.elements.get(1).value2);
        assertEquals(6, result.elements.get(1).value3);
        assertNull(codec.getSize());
    }

    @Test
    public void testUnboundedListMultipleElementTypes()
            throws DecodingException, FileNotFoundException {
        Codec<Test22> codec = Codecs.create(Test22.class);
        Test22 result = Codecs.decode(codec, new byte[]{1, 2, 3, 4, 2,
                (byte) 0xff, (byte) 0xff, 7});
        assertEquals(2, result.elements.size());
        assertEquals(2, ((Test5a) result.elements.get(0)).value1);
        assertEquals(3, ((Test5a) result.elements.get(0)).value2);
        assertEquals(4, ((Test5a) result.elements.get(0)).value3);
        assertEquals((short) 0xffff, ((Test5b) result.elements.get(1)).value1);
        assertEquals(7, ((Test5b) result.elements.get(1)).value2);
        assertNull(codec.getSize());
    }

    /**
     * Tests the situation in which the data contain an array and an item that depends on the value of an element in
     * that array.
     *
     * @throws DecodingException
     * @throws FileNotFoundException
     */
    @Test
    public void testSimpleLookup() throws DecodingException, FileNotFoundException {
        Codec<Test26> codec = Codecs.create(Test26.class);
        Test26 result = Codecs.decode(codec, new byte[]{1, 2, (byte) 255});
        assertEquals(2, result.elements.length);
        assertEquals(1, result.elements[0]);
        assertEquals(2, result.elements[1]);
        assertEquals(3, result.number);
        assertNotNull(codec.getSize());
        assertTrue(codec.getSize().isParameterized());
    }

    /**
     * Tests the situation in which the data contains an array <em>as well</em> as an index <em>and</em> the data that
     * depends on the indexed array element.
     *
     * @throws DecodingException
     * @throws FileNotFoundException
     */
    @Test
    public void testComplexLookup() throws DecodingException, FileNotFoundException {
        Codec<Test27> codec = Codecs.create(Test27.class);
        Test27 result = Codecs
                .decode(codec, new byte[]{1, 2, 0, (byte) 255});
        assertEquals(2, result.elements.length);
        assertEquals(1, result.elements[0]);
        assertEquals(2, result.elements[1]);
        assertEquals(1, result.number);
    }

    @Test
    public void testRecursion() throws DecodingException, FileNotFoundException {
        Codec<Test29> codec = Codecs.create(Test29.class);
    }

    @Test
    public void testSelectFrom() throws DecodingException, FileNotFoundException {
        Codec<Test30> codec = Codecs.create(Test30.class);
        Test30 value = Codecs.decode(codec, new byte[]{2, 3, 4, 5, 6, 7, 8});
        assertNotNull(value.value);
        assertEquals(Test5b.class, value.value.getClass());
        assertEquals(0x304, ((Test5b) value.value).value1);
    }

    @Test
    public void testSelectFromUsingLookup() throws DecodingException, FileNotFoundException {
        Codec<Test31> codec = Codecs.create(Test31.class);
        Test31 value = Codecs.decode(codec, new byte[]{5, 6, 1, 3, 4, 5, 6,
                7, 8});
        assertNotNull(value.value);
        assertEquals(Test5b.class, value.value.getClass());
    }

    @Test
    public void testSelectFromCompareString() throws DecodingException, FileNotFoundException {
        Codec<Test32> codec = Codecs.create(Test32.class);
        Test32 value = Codecs.decode(codec, new byte[]{(byte) 'a',
                (byte) 'a', 1, 3, 4, 5, 6, 7, 8});
        assertNotNull(value.value);
        assertEquals(Test5a.class, value.value.getClass());
    }

    @Test
    public void testSelectFromCompareStringFromArray() throws DecodingException, FileNotFoundException {
        Codec<Test33> codec = Codecs.create(Test33.class);
        Test33 value = Codecs.decode(codec, new byte[]{(byte) 'a',
                (byte) 'b', 1, 3, 4, 5, 6, 7, 8});
        assertNotNull(value.value);
        assertEquals(Test5b.class, value.value.getClass());
    }

    @Test
    public void testSelectFromCompareStringFromArrayAndOuter()
            throws DecodingException, FileNotFoundException {
        Codec<Test35> codec = Codecs.create(Test35.class);
        Test35 value = Codecs.decode(codec, new byte[]{(byte) 'a',
                (byte) 'b', 1, 3, 4, 5, 6, 7, 8});
        assertNotNull(value.values[0].value);
    }

    @Test
    public void testOuterReferencesFromArray() throws DecodingException, FileNotFoundException {
        Codec<Test37> codec = Codecs.create(Test37.class);
        Test37 value = Codecs.decode(codec, new byte[]{1, (byte) 'a'});
        assertNotNull(value.values);
        assertEquals(1, value.values.length);
    }

    @Test
    public void testOuterReferencesFromArrayIncludingLocal()
            throws DecodingException, FileNotFoundException {
        Codec<Test39> codec = Codecs.create(Test39.class);
        Test39 value = Codecs.decode(codec, new byte[]{2, 1, (byte) 'a', 'b',
                'c', 'd'});
        assertNotNull(value.values);
        assertEquals(1, value.values.length);
        assertEquals(1, value.values[0].secondSize);
        assertNotNull(value.values[0].value);
        assertEquals("abcd", value.values[0].value);
    }

    @Test
    public void testOuterIndexReferences() throws DecodingException {
        Codec<Test41> codec = Codecs.create(Test41.class);
        Test41 value = Codecs.decode(codec, new byte[]{1, 2, 'a', 'b', 'c',
                'd'});
        assertNotNull(value.values);
        assertEquals(1, value.values.length);
        assertNotNull(value.values[0].value);
        assertEquals("a", value.values[0].value);
    }

    @Test
    public void testOuterIndexReferencesIndexedByLocal()
            throws DecodingException, FileNotFoundException {
        Codec<Test43> codec = Codecs.create(Test43.class);
        Test43 value = Codecs.decode(codec, new byte[]{1, 2, 1, 'a', 'b',
                'c', 'd'});
        assertNotNull(value.values);
        assertEquals(1, value.values.length);
        assertNotNull(value.values[0].value);
        assertEquals("ab", value.values[0].value);
    }

    @Test
    public void testReferencesPartiallyResolvable() throws DecodingException, IOException {
        Codec<Test45> codec = Codecs.create(Test45.class);
        File file = folder.newFile("test.html");
        Codecs.document(codec, DocumentType.Html, file);
        // TODO: Add some tests on the contents of this file.
    }

    @Test
    public void testStaticReferences() throws DecodingException {
        Codec<Test48> codec = Codecs.create(Test48.class);
        Test48 value = Codecs.decode(codec, new byte[]{1, 4});
        assertEquals(4, value.value);
        value = Codecs.decode(codec, new byte[]{2, 4});
        assertEquals(0, value.value);
    }

    @Test
    public void testDefaultBigEndian() throws DecodingException {
        Codec<Test49> codec = Codecs.create(Test49.class);
        Test49 value = Codecs.decode(codec, new byte[]{0, 0, 0, 1});
        assertEquals(1, value.value);
    }

    @Test
    public void testNoBoundFields1() {
        try {
            Codec<Test50> codec = Codecs.create(Test50.class);
            fail("Codec class without annotated fields passed");
        } catch (CodecConstructionException expected) {
        }
    }

    @Test
    public void testNoBoundFields2() {
        try {
            Codec<Test51> codec = Codecs.create(Test51.class);
            fail("Codec class without annotated fields passed");
        } catch (CodecConstructionException expected) {
        }
    }

    @Test
    public void testListWithUnboundField() throws DecodingException {
        Codec<Test52> codec = Codecs.create(Test52.class);
        byte[] data = new byte[9];
        data[0] = 2;
        Test52 out = Codecs.decode(codec, data);
    }

    @Test
    public void testInitMethod() throws Exception {
        Codec<Test54> codec = Codecs.create(Test54.class);
        byte[] data = new byte[]{1, 1, 1};
        Test54 out = Codecs.decode(codec, data);
        assertEquals(0, out.b);
    }

    @Test
    public void testInitMethodInChildInstance() throws Exception {
        Codec<Test54> codec = Codecs.create(Test54.class);
        byte[] data = new byte[]{1, 1, 1};
        Test54 out = Codecs.decode(codec, data);
        assertEquals(0, out.instance.b);
    }

    @Test
    public void testInitMethodInChildArray() throws Exception {
        Codec<Test54> codec = Codecs.create(Test54.class);
        byte[] data = new byte[]{1, 1, 1};
        Test54 out = Codecs.decode(codec, data);
        assertEquals(0, out.array[0].b);
    }

    @Test
    public void testInitMethodInChildList() throws Exception {
        Codec<Test54> codec = Codecs.create(Test54.class);
        byte[] data = new byte[]{1, 1, 1, 1};
        Test54 out = Codecs.decode(codec, data);
        assertEquals(0, out.list.get(0).b);
    }


    private static class TestResolver implements Resolver {

        public Object get(String name) {
            return null;
        }

        public Resolver getOuter() {
            return null;
        }

        public Resolver getOriginalResolver() {
            return this;
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

        @BoundNumber(byteOrder = BigEndian)
        public int value1;

        public int value2;

        @BoundNumber(byteOrder = BigEndian)
        public int value3;

    }


    public static class Test3 {

        @BoundList(type = Test1.class, size = "2")
        public List<Test1> elements;

    }

    public static class Test4 {

        @BoundList(size = "2", types = {Test5a.class, Test5b.class})
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

        @BoundNumber(byteOrder = BigEndian)
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
            public byte fooBar;

        }

    }

    public static class Test17 {

        @Bound
        public byte size;

        @BoundList(size = "1", types = {Test19.class, Test20.class})
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

        @BoundList(size = "1", types = {Test23.class, Test24.class})
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

        @BoundList(types = {Test5a.class, Test5b.class})
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

        @BoundObject(types = {Test5a.class, Test5b.class})
        public Object value;

    }

    public static class Test29 {

        @Bound
        public int number;

        @If("number < 3")
        @Bound
        public Test29 value;

    }

    public static class Test30 {

        @BoundObject(selectFrom = @Choices(prefixSize = 8, alternatives = {
                @Choice(condition = "prefix==1", type = Test5a.class),
                @Choice(condition = "prefix==2", type = Test5b.class)}))
        public Object value;

    }

    public static class Test31 {

        @BoundList(size = "2")
        public byte[] index;

        @BoundObject(selectFrom = @Choices(prefixSize = 8, alternatives = {
                @Choice(condition = "index[prefix]==5", type = Test5a.class),
                @Choice(condition = "index[prefix]==6", type = Test5b.class)}))
        public Object value;

    }

    public static class Test32 {

        @BoundString(size = "2")
        public String key;

        @BoundObject(selectFrom = @Choices(prefixSize = 0, alternatives = {
                @Choice(condition = "key=='aa'", type = Test5a.class),
                @Choice(condition = "key=='bb'", type = Test5b.class)}))
        public Object value;

    }

    public static class Test33 {

        @BoundList(size = "2", type = Test34.class)
        Test34[] index;

        @BoundObject(selectFrom = @Choices(prefixSize = 8, alternatives = {
                @Choice(condition = "index[prefix].value=='a'", type = Test5a.class),
                @Choice(condition = "index[prefix].value=='b'", type = Test5b.class)}))
        public Object value;

    }

    public static class Test34 {

        @BoundString(size = "1")
        private String value;

    }

    public static class Test35 {

        @BoundList(size = "2", type = Test34.class)
        Test34[] index;

        @BoundList(size = "1", type = Test36.class)
        Test36[] values;

        public static class Test36 {

            @BoundObject(selectFrom = @Choices(prefixSize = 8, alternatives = {
                    @Choice(condition = "outer.index[prefix].value=='a'", type = Test5a.class),
                    @Choice(condition = "outer.index[prefix].value=='b'", type = Test5b.class)}))
            public Object value;

        }

    }

    public static class Test37 {

        @BoundNumber(size = "8")
        int size;

        @BoundList(size = "1")
        Test38[] values;

        public class Test38 {

            @BoundString(size = "outer.size")
            String value;

        }

    }

    public static class Test39 {

        @BoundNumber(size = "8")
        int firstSize;

        @BoundList(size = "1")
        Test40[] values;

        public class Test40 {

            @BoundNumber(size = "8")
            int secondSize;

            @BoundString(size = "outer.firstSize + 2 * secondSize")
            String value;

        }

    }

    public static class Test41 {

        @BoundList(size = "2")
        byte[] size;

        @BoundList(size = "1")
        Test42[] values;

        public class Test42 {

            @BoundString(size = "outer.size[0]")
            String value;

        }

    }

    public static class Test43 {

        @BoundList(size = "2")
        byte[] size;

        @BoundList(size = "1")
        Test44[] values;

        public class Test44 {

            @BoundNumber
            byte item;

            @BoundString(size = "outer.size[item]")
            String value;

        }

    }

    public static class Test45 {
        @BoundObject(selectFrom = @Choices(prefixSize = 8, alternatives = {
                @Choice(condition = "prefix==0", type = Test46.class),
                @Choice(condition = "prefix==1", type = Test47.class)}))
        Object object;

        @If("object.value >= 0")
        @Bound
        boolean booleanValue;
    }

    public static class Test46 {
        @Bound
        int value;
    }

    public static class Test47 {
        @Bound
        byte value1;
        @Bound
        byte value2;
        @Bound
        byte value3;
        @Bound
        byte value4;
    }

    @ImportStatic(Direction.class)
    public static class Test48 {

        @BoundNumber(size = "8")
        public Direction direction;

        @If("direction == Direction.LEFT")
        @BoundNumber(size = "8")
        public int value;

    }

    public static class Test49 {

        @BoundNumber(byteOrder = BigEndian)
        int value;

    }

    public static enum Direction {

        @BoundEnumOption(1)
        LEFT,

        @BoundEnumOption(2)
        RIGHT

    }

    public static class Test50 {
        int a;
    }

    public static class Test51 {
        int a;
        int b;
    }

    public static class Test52 {
        int a;

        @BoundList(size = "1", type = Test53.class)
        List<Test53> l;
    }

    public static class Test53 {
        private int a;
        @Bound
        private int b;
    }

    public static class Test54 {

        @Bound
        byte b;

        @BoundObject
        Test54_1 instance;

        @BoundList(size = "1")
        Test54_1[] array;

        @BoundList(size = "1", type = Test54_1.class)
        List<Test54_1> list;


        @Init
        public void init() {
            b = 0;
        }

        public static class Test54_1 {
            @Bound
            byte b;

            @Init
            public void init() {
                b = 0;
            }
        }
    }


}
