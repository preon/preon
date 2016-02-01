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

import nl.flotsam.pecia.builder.xml.XmlWriter;
import nl.flotsam.pecia.builder.xml.XmlWriterException;

public class XmlWriterHexDumpTarget implements HexDumpTarget {

    private final XmlWriter writer;

    public XmlWriterHexDumpTarget(XmlWriter writer) {
        this.writer = writer;
    }

    public void writeStartElement(final String name) throws HexDumperException {
        handle(new XmlWriterCallback() {
            public void execute(XmlWriter writer) {
                writer.writeStartElement(name);
            }
        });
    }

    public void writeAttribute(final String name, final String value) throws HexDumperException {
        handle(new XmlWriterCallback() {
            public void execute(XmlWriter writer) {
                writer.writeAttribute(name, value);
            }
        });
    }

    public void writeText(final String text) throws HexDumperException {
        handle(new XmlWriterCallback() {
            public void execute(XmlWriter writer) {
                writer.writeCharacters(text);
            }
        });
    }

    public void writeEndElement() throws HexDumperException {
        handle(new XmlWriterCallback() {
            public void execute(XmlWriter writer) {
                writer.writeEndElement();
            }
        });
    }

    public void writeText(final char c) throws HexDumperException {
        handle(new XmlWriterCallback() {
            public void execute(XmlWriter writer) {
                writer.writeCharacters(Character.toString(c));
            }
        });
    }

    private void handle(XmlWriterCallback callback) {
        try {
            callback.execute(writer);
        } catch (XmlWriterException e) {
            throw new HexDumperException(e);
        }
    }

    private interface XmlWriterCallback {

        void execute(XmlWriter writer);

    }
}
