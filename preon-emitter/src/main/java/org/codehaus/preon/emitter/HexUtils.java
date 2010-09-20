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
