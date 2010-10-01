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

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * A utility for generating hexdumps. The {@link org.codehaus.preon.hex.HexDumper} is quite a flexible class. It
 * basically allows itself to be configured with any combination of {link DumpFragment}s. Each {@link
 * org.codehaus.preon.hex.DumpFragment} represents a part of the line getting generated.
 */
public class HexDumper {

    /**
     * The number of bytes that will be fed to every line.
     */
    private final int bytesPerLine;

    /**
     * The fragments responsible for rendering the output.
     */
    private final DumpFragment[] fragments;

    /**
     * Constructs a new instance, accepting the number of bytes to printed on every line, and the fragments that should
     * be included in every line.
     *
     * @param bytesPerLine
     * @param fragments
     */
    public HexDumper(int bytesPerLine, DumpFragment... fragments) {
        this.bytesPerLine = bytesPerLine;
        this.fragments = fragments;
    }

    public long dump(ByteBuffer in, Appendable out) throws HexDumperException {
        return dump(in, new AppendableHexDumpTarget(out));        
    }

    /**
     * Dumps a representation of the {@link ByteBuffer} to the given output object.
     *
     * @param in  The in containing the bytes.
     * @param out The object receiving the output.
     * @return The number of <em>bytes</em> generated
     * @throws IOException If the operation fails while writing.
     */
    public long dump(ByteBuffer in, HexDumpTarget out) throws HexDumperException {
        long lineNumber = 0;
        byte[] buffer = new byte[this.bytesPerLine];
        int written = 0;
        in.rewind();
        while (in.hasRemaining()) {
            written = fillBuffer(in, buffer);
            for (DumpFragment fragment : fragments) {
                fragment.dump(lineNumber, buffer, written, out);
            }
            lineNumber += 1;
        }
        return lineNumber * bytesPerLine + written;
    }

    /**
     * Fills a buffer with {@link #bytesPerLine} from a {@link ByteBuffer} passed in.
     *
     * @param in
     * @param buffer
     * @return The number of bytes pushed into the buffer. (Might be less than {@link #bytesPerLine} in case of a buffer
     *         underflow.
     */
    private int fillBuffer(ByteBuffer in, byte[] buffer) {
        try {
            in.get(buffer, 0, bytesPerLine);
            return bytesPerLine;
        } catch (BufferUnderflowException bue) {
            int i = 0;
            while (in.hasRemaining()) {
                buffer[i++] = in.get();
            }
            return i;
        }
    }

}