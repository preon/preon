package org.codehaus.preon.hex;

public interface HexDumpTarget {

    void writeStartElement(String name) throws HexDumperException;

    void writeAttribute(String name, String value) throws HexDumperException;

    void writeText(String text) throws HexDumperException;

    void writeEndElement() throws HexDumperException;

    void writeText(char c) throws HexDumperException;
    
}
