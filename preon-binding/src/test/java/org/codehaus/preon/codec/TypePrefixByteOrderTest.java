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

        @TypePrefix(endian = ByteOrder.BigEndian, size = 16, value = "0xFF00")
        public static class Alpha {
            @Bound
            private byte value;
        }

        @TypePrefix(endian = ByteOrder.BigEndian, size = 16, value = "0x00FF")
        public static class Beta {
            @Bound
            private byte value;
        }

    }

    public static class LittleEndianStruct {
        @BoundObject(types = {Alpha.class, Beta.class})
        public Object sub;

        @TypePrefix(endian = ByteOrder.LittleEndian, size = 16, value = "0xFF00")
        public static class Alpha {
            @Bound
            private byte value;
        }

        @TypePrefix(endian = ByteOrder.LittleEndian, size = 16, value = "0x00FF")
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
