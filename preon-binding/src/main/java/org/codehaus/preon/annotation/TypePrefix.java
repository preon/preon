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
package org.codehaus.preon.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.codehaus.preon.Codec;
import org.codehaus.preon.buffer.ByteOrder;


/**
 * The annotation used to indicate the discriminator used to recognize an instance of this class.
 *
 * @author Wilfred Springer
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TypePrefix {

    /**
     * The type of endianness: either {@link ByteOrder#LittleEndian} or {@link ByteOrder#BigEndian}.
     *
     * @return The type of endianness. Defaults to {@link ByteOrder#LittleEndian}.
     */
    ByteOrder byteOrder() default ByteOrder.LittleEndian;

    /**
     * The number of bits used to represent the numeric value. Defaults to 0, allowing the {@link Codec} to make its own
     * decision on the number of bits to be used; however, they are expected to respect the following defaults:
     * <p/>
     * <table> <tr> <th>Type</th> <th>Number of bits</th> </tr> <tr> <td>Long, long</td> <td>64</td> </tr> <tr>
     * <td>Integer, int</td> <td>32</td> </tr> <tr> <td>Short, short</td> <td>16</td> </tr> <tr> <td>Byte, byte</td>
     * <td>8</td> </tr> </table>
     * <p/>
     * <p> The type of this annotation might be turned into a String in the future, to allow the size to be based on an
     * expression instead of a fixed value. </p>
     *
     * @return The number of bits used to represent the numeric value.
     */
    int size() default 0;

    /**
     * The value that will be used to match this particular record.
     *
     * @return The value, as a Limbo expression. (This is done to allow future versions of Limbo to be extended with a
     *         convenience notation for binary notation.)
     */
    String value() default "0";

}
