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

/**
 * A {@link org.codehaus.preon.hex.DumpFragment} that translates every byte to a two-character hex represenation, with
 * support for grouping.
 */
public class BytesAsHexFragment implements DumpFragment {

    private final int groupSize;
    private final boolean insertSpan;

    private static final char[] symbols = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public BytesAsHexFragment(int groupSize) {
        this(groupSize, false);
    }

    /**
     * Constructs a new instance, accepting the number of bytes that together make a group.
     *
     * @param groupSize
     */
    public BytesAsHexFragment(int groupSize, boolean insertSpan) {
        this.groupSize = groupSize;
        this.insertSpan = insertSpan;
    }

    public int getSize(int numberOfBytes) {
        return numberOfBytes * 2 + (numberOfBytes - 1) + ((numberOfBytes - 1) % groupSize);
    }

    public void dump(long lineNumber, byte[] buffer, int length, HexDumpTarget out) throws HexDumperException {
        for (int i = 0; i < length; i++) {
            append(i, symbols[hiword(buffer[i])], symbols[loword(buffer[i])], out, i + lineNumber * length);
        }
        for (int i = length; i < buffer.length; i++) {
            append(i, ' ', ' ', out, i + lineNumber * length);
        }
    }

    private void append(int i, char first, char second, HexDumpTarget out, long bytePos) {
        if (i != 0) {
            out.writeText(' ');
            if (i % groupSize == 0) {
                out.writeText(' ');
            }
        }
        if (insertSpan) {
            out.writeStartElement("span");
        }
        out.writeText(first);
        out.writeText(second);
        if (insertSpan) {
            out.writeEndElement();            
        }
    }

    /**
     * Returns the value of the first 4 bits of a byte.
     *
     * @param b The byte for which we need the first 4 bits.
     * @return The value of the first 4 bits of a byte.
     */
    private static int hiword(byte b) {
        return (0xf0 & b) >> 4;
    }

    /**
     * Returns the value of the last 4 bits of a byte.
     *
     * @param b The byte for which we need the last 4 bits.
     * @return The value of the last 4 bits of a byte.
     */
    private static int loword(byte b) {
        return (0x0f & b);
    }
}
