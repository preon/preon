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
