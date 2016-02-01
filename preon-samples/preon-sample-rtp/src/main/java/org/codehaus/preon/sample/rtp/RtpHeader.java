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

import org.codehaus.preon.annotation.Bound;
import org.codehaus.preon.annotation.BoundList;
import org.codehaus.preon.annotation.BoundNumber;
import org.codehaus.preon.buffer.ByteOrder;

public class RtpHeader {

    @BoundNumber(size = "2")
    public int version;

    @Bound
    public boolean padding;

    @Bound
    public boolean extension;

    @BoundNumber(size="4")
    public int csrcCount;

    @Bound
    public boolean marker;

    @BoundNumber(size="7")
    public int payloadType;

    @BoundNumber(size="16", byteOrder = ByteOrder.BigEndian)
    public int sequenceNumber;

    @BoundNumber(size="32", byteOrder = ByteOrder.BigEndian)
    public int timestamp;

    @BoundNumber(size="32", byteOrder = ByteOrder.BigEndian)
    public int synchronizationSource;

    @BoundList(size="csrcCount")
    public int[] csrcs; 

}
