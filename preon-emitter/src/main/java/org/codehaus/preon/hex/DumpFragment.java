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
 * A fixed-size framgment of output.
 */
public interface DumpFragment {

    /**
     * The number of characters this fragment will occupy, as a function of the number of bytes that <em>might</em> be
     * presented.
     *
     * @return The number of characters this fragment will occupy.
     */
    int getSize(int numberOfBytes);

    /**
     * Dumps the contents of this fragment to <code>out</code>.
     *
     * @param lineNumber The line number.
     * @param buffer     The bytes to be rendered. (Assume that the number of bytes normally to be printed to every line
     *                   is the length of this buffer.
     * @param out The object receiving output.
     */
    void dump(long lineNumber, byte[] buffer, int length, HexDumpTarget out) throws HexDumperException;

}
