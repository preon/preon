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
