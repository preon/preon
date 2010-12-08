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
