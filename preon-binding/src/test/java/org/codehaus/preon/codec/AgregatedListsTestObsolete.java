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
