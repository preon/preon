package org.codehaus.preon.emitter;

import nl.flotsam.pecia.builder.xml.XmlWriter;

import java.io.IOException;

public class XmlWriterAppendable implements Appendable {

    private final XmlWriter writer;

    public XmlWriterAppendable(XmlWriter writer) {
        this.writer = writer;
    }

    public Appendable append(CharSequence csq) throws IOException {
        writer.writeCharacters(csq.toString());
        return this;
    }

    public Appendable append(CharSequence csq, int start, int end) throws IOException {
        writer.writeCharacters(csq.subSequence(start, end).toString());
        return this;
    }

    public Appendable append(char c) throws IOException {
        writer.writeCharacters(Character.toString(c));
        return this;
    }
}
