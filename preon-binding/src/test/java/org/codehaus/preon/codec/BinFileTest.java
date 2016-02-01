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
