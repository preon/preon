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

import java.nio.ByteBuffer;
import java.util.List;

import org.codehaus.preon.Codec;
import org.codehaus.preon.Codecs;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.annotation.BoundList;
import org.codehaus.preon.annotation.BoundNumber;
import org.codehaus.preon.annotation.Slice;

import junit.framework.TestCase;


public class AgregatedListsTestObsolete extends TestCase {

    public void testVariableResolutionInClassReferedByAnotherClass()
            throws DecodingException {

        Codec<Test2> codec = Codecs.create(Test2.class);

        // 2, {8, 255}, {8, 254}
        // 2 - the number of test3 items in the test3List list
        // 8 - the bits size of the 'value' in the first test3 item
        // 255 - the content of the first item's test3.value
        // 8 - the bits size of the test3.value filed in the second test3 item
        // 254 - the content of the second item's test3.value
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{2, 8, (byte) 255,
                8, (byte) 254});

        Test2 result = Codecs.decode(codec, byteBuffer);

        assertEquals(2, result.t3ListSize);
        assertEquals(2, result.test3List.size());
        assertEquals(8, result.test3List.get(0).valueBitSize);
        assertEquals(255, result.test3List.get(0).value);
        assertEquals(8, result.test3List.get(1).valueBitSize);
        assertEquals(254, result.test3List.get(1).value);
    }

    public void testSlicedUnboundedListOfLists() throws DecodingException {

        Codec<Test1> codec = Codecs.create(Test1.class);

        // 40, 2, {8, 255}, {8, 254}
        // 40 - total amount of bits for the slice that contains the 2 item list
        // 2 - the number of test3 items in the test3List list
        // 8 - the bits size of the 'value' in the first test3 item
        // 255 - the content of the first item's test3.value
        // 8 - the bits size of the test3.value filed in the second test3 item
        // 254 - the content of the second item's test3.value

        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[]{5, 2, 8,
                (byte) 255, 8, (byte) 254});
        Test1 result = Codecs.decode(codec, byteBuffer);

        assertEquals(5, result.sliceByteSize);

        assertEquals(1, result.test2List.size());

        List<Test3> test3List = result.test2List.get(0).test3List;

        assertEquals(2, test3List.size());
        assertEquals(8, test3List.get(0).valueBitSize);
        assertEquals(255, test3List.get(0).value);
        assertEquals(8, test3List.get(1).valueBitSize);
        assertEquals(254, test3List.get(1).value);

    }

    public static class Test1 {

        @BoundNumber(size = "8")
        public int sliceByteSize;

        // convert in bits
        @Slice(size = "sliceByteSize * 8")
        @BoundList(type = Test2.class)
        public List<Test2> test2List;
    }

    public static class Test2 {

        @BoundNumber(size = "8")
        public int t3ListSize;

        @BoundList(size = "t3ListSize", type = Test3.class)
        public List<Test3> test3List;
    }

    public static class Test3 {

        @BoundNumber(size = "8")
        public int valueBitSize;

        @BoundNumber(size = "valueBitSize")
        // @BoundNumber(size = "8")
        public int value;
    }
}
