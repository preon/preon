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
package org.codehaus.preon.hex;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * A {@link org.codehaus.preon.hex.DumpFragment} that translates every byte to a character representation - as long as
 * it makes sense.
 */
public class BytesAsCharFragment implements DumpFragment {

    private static final char[] asc = new char[256];

    static {
        Charset charset = Charset.forName("ASCII");
        byte[] bytes = new byte[256];
        for (int i = 0; i <= 255; i++) {
            asc[i] = '.';
            bytes[i] = (byte) (0xff & i);
        }
        CharBuffer buffer = charset.decode(ByteBuffer.wrap(bytes));
        for (int i = 0x20; i <= 0x7E; i++) {
            asc[i] = buffer.get(i);
        }
    }

    public int getSize(int numberOfBytes) {
        return numberOfBytes;
    }

    public void dump(long lineNumber, byte[] buffer, int length, HexDumpTarget out) throws HexDumperException {
        for (int i = 0; i < length; i++) {
            out.writeText(asc[0xff & buffer[i]]);
        }
        for (int i = length; i < buffer.length; i++) {
            out.writeText(' ');
        }
    }
}
