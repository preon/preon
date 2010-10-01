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
