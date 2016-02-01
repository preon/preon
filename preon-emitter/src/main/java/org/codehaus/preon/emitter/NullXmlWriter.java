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

import nl.flotsam.pecia.builder.xml.XmlWriter;
import nl.flotsam.pecia.builder.xml.XmlWriterException;

import javax.xml.namespace.NamespaceContext;

public class NullXmlWriter implements XmlWriter {

    public void writeStartElement(String s) throws XmlWriterException {
    }

    public void writeStartElement(String s, String s1) throws XmlWriterException {
    }

    public void writeStartElement(String s, String s1, String s2) throws XmlWriterException {
    }

    public void writeEmptyElement(String s, String s1) throws XmlWriterException {
    }

    public void writeEmptyElement(String s, String s1, String s2) throws XmlWriterException {
    }

    public void writeEmptyElement(String s) throws XmlWriterException {
    }

    public void writeEndElement() throws XmlWriterException {
    }

    public void writeEndDocument() throws XmlWriterException {
    }

    public void close() throws XmlWriterException {
    }

    public void flush() throws XmlWriterException {
    }

    public void writeAttribute(String s, String s1) throws XmlWriterException {
    }

    public void writeAttribute(String s, String s1, String s2, String s3) throws XmlWriterException {
    }

    public void writeAttribute(String s, String s1, String s2) throws XmlWriterException {
    }

    public void writeNamespace(String s, String s1) throws XmlWriterException {
    }

    public void writeDefaultNamespace(String s) throws XmlWriterException {
    }

    public void writeComment(String s) throws XmlWriterException {
    }

    public void writeProcessingInstruction(String s) throws XmlWriterException {
    }

    public void writeProcessingInstruction(String s, String s1) throws XmlWriterException {
    }

    public void writeCData(String s) throws XmlWriterException {
    }

    public void writeDTD(String s) throws XmlWriterException {
    }

    public void writeEntityRef(String s) throws XmlWriterException {
    }

    public void writeStartDocument() throws XmlWriterException {
    }

    public void writeStartDocument(String s) throws XmlWriterException {
    }

    public void writeStartDocument(String s, String s1) throws XmlWriterException {
    }

    public void writeCharacters(String s) throws XmlWriterException {
    }

    public void writeCharacters(char[] chars, int i, int i1) throws XmlWriterException {
    }

    public String getPrefix(String s) throws XmlWriterException {
        return null;
    }

    public void setPrefix(String s, String s1) throws XmlWriterException {
    }

    public void setDefaultNamespace(String s) throws XmlWriterException {
    }

    public void setNamespaceContext(NamespaceContext namespaceContext) throws XmlWriterException {
    }

    public NamespaceContext getNamespaceContext() {
        return null;
    }

    public Object getProperty(String s) throws IllegalArgumentException {
        return null;
    }
}
