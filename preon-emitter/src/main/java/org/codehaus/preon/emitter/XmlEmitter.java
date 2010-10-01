/**
 * Copyright (C) 2009-2010 Wilfred Springer
 *
 * This file is part of Preon.
 *
 * Preon is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * Preon is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Preon; see the file COPYING. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
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
