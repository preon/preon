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

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A homegrown hexdump tool. Different from the others out there for taking an
 * {@link Appendable}, allowing you to use it directly on System.out or
 * System.in, use a StringBuilder, or whatever.
 * 
 */
public class HexUtils {

    /**
     * Hexidecimal symbols.
     */
    private static final char[] symbols = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private static final int ADDRESS_LENGTH = 6;

    /**
     * Characters to be printed, indexed by byte.
     */
    private static final char[] asc = new char[256];
    private static final char SPACE = ' ';
    private static final char EXTRA_SPACE = SPACE;

    static {
        for (int i = 0; i <= 255; i++) {
            if (Character.isLetter((char) i)) {
                asc[i] = (char) i;
            } else {
                asc[i] = '.';
            }
        }
    }

    /**
     * Dumps the byte array to the {@link Appendable} passed in. Think
     * {@link #dump(byte[], Appendable, boolean)} with third parameter set to
     * <code>false</code>.
     * 
     * @param buffer
     *            The buffer to be dumped.
     * @param out
     *            The object receiving the dump.
     * @throws IOException
     *             If we cannot write to the {@link Appendable}.
     */
    public static void dump(byte[] buffer, Appendable out) throws IOException {
        dump(buffer, out, false);
    }

    /**
     * Dumps the byte array to the {@link Appendable} passed in.
     * 
     * @param buffer
     *            The buffer to be dumped.
     * @param out
     *            The object receiving the dump.
     * @param prependAddress
     *            Whether or not the starting address should be included in the
     *            output.
     * @throws IOException
     *             If we cannot write to the {@link Appendable}.
     */
    public static void dump(byte[] buffer, Appendable out,
            boolean prependAddress) throws IOException {
        dump(ByteBuffer.wrap(buffer), out, 8, prependAddress);
    }

    /**
     * Dumps the byte array to the {@link Appendable} passed in.
     * 
     * @param buffer
     *            The buffer to be dumped.
     * @param out
     *            The object receiving the dump.
     * @param bytesPerLine
     *            The number of bytes per line.
     * @throws IOException
     *             If we cannot write to the {@link Appendable}.
     */
    public static void dump(byte[] buffer, Appendable out, int bytesPerLine)
            throws IOException {
        dump(ByteBuffer.wrap(buffer), out, bytesPerLine, false);
    }

    public static void dump(byte[] buffer, Appendable out, int bytesPerLine, boolean prependAddress)
            throws IOException {
        dump(ByteBuffer.wrap(buffer), out, bytesPerLine, true);
    }


    /**
     * Dumps the byte array to the {@link Appendable} passed in.
     * 
     * @param buffer
     *            The buffer to be dumped.
     * @param out
     *            The object receiving the dump.
     * @param bytesPerLine
     *            The number of bytes per line.
     * @param prependAddress
     *            Whether or not the starting address should be included in the
     *            output.
     * @throws IOException
     *             If we cannot write to the {@link Appendable}.
     */
    public static void dump(ByteBuffer buffer, Appendable out, int bytesPerLine,
            boolean prependAddress) throws IOException {
        byte[] current = new byte[bytesPerLine];
        int pos = 0;
        int line = 0;
        prependAddress(prependAddress, line, bytesPerLine, out);
        while (buffer.hasRemaining()) {
            current[pos] = buffer.get();
            if (pos != 0 && pos % 8 == 0) {
                out.append(EXTRA_SPACE);
            }
            out.append(symbols[hiword(current[pos])]);
            out.append(symbols[loword(current[pos])]);
            out.append(SPACE);
            if (pos == bytesPerLine - 1) {
                appendAscii(current, bytesPerLine, out, bytesPerLine);
                prependAddress(prependAddress, line, bytesPerLine, out);
                pos = 0;
                line += 1;
            } else {
                pos++;
            }
        }
        if (pos != 0) {
            for (int i = pos; i < bytesPerLine; i++) {
                out.append("  ");
                out.append(SPACE);
                if (i % 8 == 0) {
                    out.append(EXTRA_SPACE);
                }
            }
            appendAscii(current, pos, out, bytesPerLine);
        }
    }

    private static void prependAddress(boolean prependAddress, int line, int bytesPerLine, Appendable out) throws IOException {
        if (prependAddress) {
            String address = Integer.toString(line * bytesPerLine);
            for (int i = ADDRESS_LENGTH - address.length(); i >= 0; i--) {
                out.append('0');
            }
            out.append(address);
            out.append(EXTRA_SPACE);
        }
    }

    /**
     * Appends the ASCII representation of a byte to the line.
     * 
     * @param buffer
     * @param nrBytes
     * @param out
     * @param bytesPerLine
     * @throws IOException
     */
    private static void appendAscii(byte[] buffer, int nrBytes, Appendable out, int bytesPerLine)
            throws IOException {
        out.append(" |");
        for (int i = 0; i < nrBytes; i++) {
            out.append(asc[0xff & buffer[i]]);
        }
        for (int i = nrBytes; i < bytesPerLine; i++) {
            out.append(EXTRA_SPACE);
        }
        out.append("|\n");
    }

    /**
     * Returns the value of the first 4 bits of a byte.
     * 
     * @param b
     *            The byte for which we need the first 4 bits.
     * @return The value of the first 4 bits of a byte.
     */
    private static int hiword(byte b) {
        return (0xf0 & b) >> 4;
    }

    /**
     * Returns the value of the last 4 bits of a byte.
     * 
     * @param b
     *            The byte for which we need the last 4 bits.
     * @return The value of the last 4 bits of a byte.
     */
    private static int loword(byte b) {
        return (0x0f & b);
    }

}
