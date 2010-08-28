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
package org.codehaus.preon.sample.rtp;

import org.codehaus.preon.Codec;
import org.codehaus.preon.Codecs;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RtpHeaderTest {

    @Test
    public void shouldRenderCorrectly() throws IOException {
        Codec<RtpHeader> codec = Codecs.create(RtpHeader.class);
        RtpHeader header = new RtpHeader();
        header.version = 1;
        header.padding = true;
        header.extension = false;
        header.csrcCount = 2;
        header.marker = false;
        header.payloadType = 8; // PCMA
        header.sequenceNumber = 12301;
        header.timestamp = 1298301;
        header.synchronizationSource = 1209182;
        header.csrcs = new int[header.csrcCount];
        header.csrcs[0] = 123;
        header.csrcs[1] = 321;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Codecs.encode(header, codec, out);
    }

}
