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
