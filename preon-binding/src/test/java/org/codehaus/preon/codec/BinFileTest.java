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
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.annotation.BoundList;
import org.codehaus.preon.annotation.BoundNumber;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class BinFileTest {

    @Test
    public void parseBinFile() throws DecodingException {
        Codec<BinFile> codec = Codecs.create(BinFile.class);
        byte[] buffer = new byte[]{
                2, 0,
                3, 0,
                'a', 'b', 'c',
                4, 0,
                '1', '2', '3', '4'
        };
        BinFile b = Codecs.decode(codec, buffer);

        assertEquals(b.getNumberOfRecords(), 2);

        List<BinFile.Record> rL = b.getRecords();

        assertEquals(rL.size(), 2);

        BinFile.Record r0 = rL.get(0);
        assertEquals(r0.getRecordLength(), 3);
        assertEquals(new String(r0.getData()), "abc");

        BinFile.Record r1 = rL.get(1);
        assertEquals(r1.getRecordLength(), 4);
        assertEquals(new String(r1.getData()), "1234");
    }

    public static class BinFile {

        @BoundNumber(size = "16")
        int numberOfRecords;
        
        @BoundList(type = Record.class, size = "numberOfRecords")
        List<Record> records;

        public int getNumberOfRecords() {
            return numberOfRecords;
        }

        public List<Record> getRecords() {
            return records;
        }

        public class Record {
            @BoundNumber(size = "16")
            int recordLength;
            @BoundList(size = "recordLength")
            byte[] data;

            public int getRecordLength() {
                return recordLength;
            }

            public byte[] getData() {
                return data;
            }
        }
    }

}
