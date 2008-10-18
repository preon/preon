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

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

import nl.flotsam.preon.Codec;
import nl.flotsam.preon.Codecs;
import nl.flotsam.preon.DefaultBuilder;
import nl.flotsam.preon.annotation.Bound;
import nl.flotsam.preon.annotation.BoundList;
import nl.flotsam.preon.annotation.BoundNumber;
import nl.flotsam.preon.annotation.LengthPrefix;
import nl.flotsam.preon.annotation.TypePrefix;
import nl.flotsam.preon.buffer.DefaultBitBuffer;

import junit.framework.TestCase;


public class UnboundedListsTest extends TestCase {

	public void testNestedArrayWithNotFixedLengthElements() throws Exception {

		ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[] { 66, 56, 01, 01, 11,
				02, 02, 22, 22, 99 });

		Codec<Test0> codec = Codecs.create(Test0.class);

		Test0 test0 = codec.decode(new DefaultBitBuffer(byteBuffer), null,
				new DefaultBuilder());

		assertNotNull(test0);

		assertEquals(66, test0.value1);

		assertEquals(2, test0.value2.size());

		assertEquals(99, test0.value3);

		Iterator<TestBase> iterator = test0.value2.iterator();
		Test1 test1 = (Test1) iterator.next();
		assertEquals(1, test1.arrayLength);
		assertEquals(11, test1.value[0]);

		Test2 test2 = (Test2) iterator.next();
		assertEquals(2, test2.arrayLength);
		assertEquals(22, test2.value[0]);
		assertEquals(22, test2.value[1]);
	}

	public static class Test0 {

		@Bound
		byte value1;// 66

		// 7 = total number of bytes in the array. Following table shows
		// expected distribution of bytes:
		// | ClassTypePrefix|ArrayLength|ArrayContent|
		// | 01 | 01 | 11 |
		// | 02 | 02 | 22 22 |

		@LengthPrefix(size = "8")
		@BoundList(types = { Test1.class, Test2.class })
		List<TestBase> value2;

		@Bound
		byte value3;// 99
	}

	public static class TestBase {

	    @BoundNumber(size = "8")
        public int arrayLength;

        @BoundList(size = "arrayLength")
        public byte[] value;
	    
	}

	@TypePrefix(size = 8, value = "1")
	public static class Test1 extends TestBase {

	}

	@TypePrefix(size = 8, value = "2")
	public static class Test2 extends TestBase {

	}
}
