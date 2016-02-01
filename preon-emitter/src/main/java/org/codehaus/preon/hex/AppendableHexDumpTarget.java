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

import java.io.IOException;

public class AppendableHexDumpTarget implements HexDumpTarget {

    private final Appendable out;

    public AppendableHexDumpTarget(Appendable out) {
        this.out = out;
    }

    public void writeStartElement(String name) {
    }

    public void writeAttribute(String name, String value) {
    }

    public void writeText(String text) {
        try {
            out.append(text);
        } catch (IOException e) {
            throw new HexDumperException(e);
        }
    }

    public void writeEndElement() {
    }

    public void writeText(char c) throws HexDumperException {
        try {
            out.append(c);
        } catch (IOException e) {
            throw new HexDumperException(e);
        }
    }
}
