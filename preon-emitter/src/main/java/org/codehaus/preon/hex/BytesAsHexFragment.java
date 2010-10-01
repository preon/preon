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
package org.codehaus.preon.hex;

/**
 * A {@link org.codehaus.preon.hex.DumpFragment} that translates every byte to a two-character hex represenation, with
 * support for grouping.
 */
public class BytesAsHexFragment implements DumpFragment {

    private final int groupSize;
    private final boolean insertSpan;

    private static final char[] symbols = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public BytesAsHexFragment(int groupSize) {
        this(groupSize, false);
    }

    /**
     * Constructs a new instance, accepting the number of bytes that together make a group.
     *
     * @param groupSize
     */
    public BytesAsHexFragment(int groupSize, boolean insertSpan) {
        this.groupSize = groupSize;
        this.insertSpan = insertSpan;
    }

    public int getSize(int numberOfBytes) {
        return numberOfBytes * 2 + (numberOfBytes - 1) + ((numberOfBytes - 1) % groupSize);
    }

    public void dump(long lineNumber, byte[] buffer, int length, HexDumpTarget out) throws HexDumperException {
        for (int i = 0; i < length; i++) {
            append(i, symbols[hiword(buffer[i])], symbols[loword(buffer[i])], out, i + lineNumber * length);
        }
        for (int i = length; i < buffer.length; i++) {
            append(i, ' ', ' ', out, i + lineNumber * length);
        }
    }

    private void append(int i, char first, char second, HexDumpTarget out, long bytePos) {
        if (i != 0) {
            out.writeText(' ');
            if (i % groupSize == 0) {
                out.writeText(' ');
            }
        }
        if (insertSpan) {
            out.writeStartElement("span");
        }
        out.writeText(first);
        out.writeText(second);
        if (insertSpan) {
            out.writeEndElement();            
        }
    }

    /**
     * Returns the value of the first 4 bits of a byte.
     *
     * @param b The byte for which we need the first 4 bits.
     * @return The value of the first 4 bits of a byte.
     */
    private static int hiword(byte b) {
        return (0xf0 & b) >> 4;
    }

    /**
     * Returns the value of the last 4 bits of a byte.
     *
     * @param b The byte for which we need the last 4 bits.
     * @return The value of the last 4 bits of a byte.
     */
    private static int loword(byte b) {
        return (0x0f & b);
    }
}
