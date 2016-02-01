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