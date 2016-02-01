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
package org.codehaus.preon.emitter;

import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.Para;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.builder.base.NullLifecycleListener;
import nl.flotsam.pecia.builder.html.HtmlDocumentBuilder;
import nl.flotsam.pecia.builder.xml.StreamingXmlWriter;
import nl.flotsam.pecia.builder.xml.XmlWriter;
import org.codehaus.preon.Codec;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.hex.*;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;

public class XmlEmitter implements Emitter {

    private final OutputStreamFactory outputStreamFactory;
    private int depth = 0;
    private XmlWriter current;
    private static final String ROOT_ELEMENT = "emitter";
    private static final HexDumper contentsAsBytesDumper =
            new HexDumper(16,
                    new LinePosFragment(8),
                    new LiteralFragment("  "),
                    new BytesAsHexFragment(8, true),
                    new LiteralFragment("\n"));

    public XmlEmitter(OutputStreamFactory outputStreamFactory) {
        this.outputStreamFactory = outputStreamFactory;
    }

    public void markStart(Codec<?> codec, long position, BitBuffer buffer) {
        if (depth == 0) {
            current = createWriter();
            writeStart(buffer);
        }
        current.writeStartElement("fragment");
        current.writeStartElement("start");
        current.writeAttribute("position", Long.toString(position));
        current.writeStartElement("desc");
        Para<?> para = new HtmlDocumentBuilder.HtmlParaBuilder(new HtmlDocumentBuilder(current), null, new NullLifecycleListener(), current);
        Documenter<ParaContents<?>> documenter = codec.getCodecDescriptor().summary();
        documenter.document(para);
        para.end();
        current.writeEndElement();
        current.writeEndElement();
        current.writeStartElement("contents");
        depth += 1;
    }

    private XmlWriter createWriter() {
        try {
            OutputStream out = outputStreamFactory.create();
            if (out == null) {
                return new NullXmlWriter();
            } else {
                XMLStreamWriter writer =
                        XMLOutputFactory.newInstance().createXMLStreamWriter(outputStreamFactory.create(), "UTF-8");
                return new StreamingXmlWriter(writer);
            }
        } catch (XMLStreamException e) {
            return new NullXmlWriter();
        } catch (IOException e) {
            return new NullXmlWriter();
        }
    }

    public void markEnd(Codec<?> codec, long position, long read, Object result) {
        current.writeEndElement(); // end of contents element
        current.writeStartElement("end");
        current.writeAttribute("position", Long.toString(position));
        current.writeAttribute("type", result.getClass().getSimpleName());
        current.writeAttribute("value", result.toString());
        current.writeEndElement();
        current.writeEndElement();
        depth -= 1;
        if (depth == 0) {
            writeEnd();
        }
    }

    private void writeStart(BitBuffer buffer) {
        current.writeStartDocument();
        current.writeStartElement(ROOT_ELEMENT);
        current.writeStartElement("bytes");
        contentsAsBytesDumper.dump(buffer.readAsByteBuffer(), new XmlWriterHexDumpTarget(current));
        buffer.readAsByteBuffer();
        current.writeEndElement();
    }

    private void writeEnd() {
        current.writeEndElement();
        current.writeEndDocument();
        current.close();
    }

    public void markFailure() {
        current.writeEndElement();
        current.writeEmptyElement("failure");
        current.writeEndElement();
        depth -= 1;
        if (depth == 0) {
            writeEnd();
        }
    }

    public void markStartLoad(String name, Object object) {
        current.writeStartElement("slot");
        current.writeAttribute("name", name);
    }

    public void markEndLoad() {
        current.writeEndElement();
    }
}
