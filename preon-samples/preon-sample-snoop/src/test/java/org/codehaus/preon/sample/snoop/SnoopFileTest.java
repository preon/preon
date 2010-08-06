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
