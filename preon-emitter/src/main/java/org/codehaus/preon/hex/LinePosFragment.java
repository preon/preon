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
 * A {@link org.codehaus.preon.hex.DumpFragment} that will generate the line position, with the given number of
 * characters, with trailing zeroes if required.
 */
public class LinePosFragment implements DumpFragment {

    private final int size;

    /**
     * Constructs a new instance, accepting the number of character positions reserved for the line number.
     *
     * @param size
     */
    public LinePosFragment(int size) {
        this.size = size;
    }

    public int getSize(int numberOfBytes) {
        return size;
    }

    public void dump(long lineNumber, byte[] buffer, int length, HexDumpTarget out) throws HexDumperException {
        String position = Long.toHexString(lineNumber);
        if (position.length() > size) {
            position = position.substring(position.length() - size);
        } else {
            for (int i = position.length(); i < size; i++) {
                out.writeText('0');
            }
        }
        out.writeText(position);
    }
}
