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

import org.codehaus.preon.Codec;
import org.codehaus.preon.Codecs;
import org.codehaus.preon.annotation.Bound;
import org.codehaus.preon.annotation.BoundObject;
import org.codehaus.preon.annotation.TypePrefix;
import org.codehaus.preon.buffer.ByteOrder;
import org.junit.Assert;
import org.junit.Test;


public class TypePrefixByteOrderTest {

    public static class BigEndianStruct {
        @BoundObject(types = {Alpha.class, Beta.class})
        public Object sub;

        @TypePrefix(byteOrder = ByteOrder.BigEndian, size = 16, value = "0xFF00")
        public static class Alpha {
            @Bound
            private byte value;
        }

        @TypePrefix(byteOrder = ByteOrder.BigEndian, size = 16, value = "0x00FF")
        public static class Beta {
            @Bound
            private byte value;
        }

    }

    public static class LittleEndianStruct {
        @BoundObject(types = {Alpha.class, Beta.class})
        public Object sub;

        @TypePrefix(byteOrder = ByteOrder.LittleEndian, size = 16, value = "0xFF00")
        public static class Alpha {
            @Bound
            private byte value;
        }

        @TypePrefix(byteOrder = ByteOrder.LittleEndian, size = 16, value = "0x00FF")
        public static class Beta {
            @Bound
            private byte value;
        }

    }


    @Test
    public void testTypePrefixBigEndian() throws Exception {

        Codec<BigEndianStruct> codec = Codecs.create(BigEndianStruct.class);

        byte[] data_alpha = new byte[]{
                (byte) 0x00,
                (byte) 0xFF,
                (byte) 0xFF
        };

        Assert.assertEquals(BigEndianStruct.Beta.class, Codecs.decode(codec, data_alpha).sub.getClass());

    }

    
    @Test
    public void testTypePrefixLittleEndian() throws Exception {

        Codec<LittleEndianStruct> codec = Codecs.create(LittleEndianStruct.class);

        byte[] data_beta = new byte[]{
                (byte) 0x00,
                (byte) 0xFF,
                (byte) 0xFF
        };
        Assert.assertEquals(LittleEndianStruct.Alpha.class, Codecs.decode(codec, data_beta).sub.getClass());
    }
}
