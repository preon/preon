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
package org.codehaus.preon.sample.snoop;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.codehaus.preon.Codec;
import org.codehaus.preon.Codecs;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.sample.snoop.SnoopFile.DatalinkType;
import org.codehaus.preon.sample.snoop.SnoopFile.PacketRecord;
import org.codehaus.preon.sample.snoop.SnoopFile.PacketRecord.EthernetFrame;

import org.junit.Test;


public class SnoopFileTest {

    @Test
    public void shouldBeAbleToReadPoo() throws FileNotFoundException, DecodingException, IOException {
        File file = new File(getBaseDir(), "src/test/resources/poo.cap");
        assertTrue(file.exists());
        Codec<SnoopFile> codec = Codecs.create(SnoopFile.class);
        SnoopFile snoopFile = Codecs.decode(codec, file);
        assertNotNull(snoopFile.getHeader());
        assertNotNull(snoopFile.getRecords());
        assertEquals(2, snoopFile.getHeader().getVersionNumber());
        assertEquals(DatalinkType.ETHERNET, snoopFile.getHeader().getDatalinkType());
        for (PacketRecord record : snoopFile.getRecords()) {
            System.out.println(record.getTimestampSeconds() + " : " + record.getTimestampMicroseconds());
            assertTrue(record.getPacketData() instanceof EthernetFrame);
            System.out.println(((EthernetFrame) record.getPacketData()).getSourceAddress());
        }
    }

    private File getBaseDir() {
        if (System.getProperty("basedir") != null) {
            return new File(System.getProperty("basedir"));
        } else {
            return new File(System.getProperty("user.dir"));
        }
    }

}
