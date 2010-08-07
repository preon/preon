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
package org.codehaus.preon.util;

import org.codehaus.preon.el.Descriptive;
import org.codehaus.preon.el.util.StringBuilderDocument;

/**
 * A collection of text utilities.
 *
 * @author Wilfred Springer (wis)
 */
public class TextUtils {

    private static String[] NUMBER_TRANSLATIONS = new String[]{"zero", "one",
            "two", "three", "four", "five", "six", "seven", "eight", "nine",
            "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen",
            "sixteen", "seventeen", "eighteen", "nineteen", "twenty"};

    /**
     * Describes a {@link Descriptive} element by calling its {@link Descriptive#document(org.codehaus.preon.el.Document)}
     * operation, and turning the results back into a String.
     *
     * @param descriptive The object to be described.
     * @return A String.
     */
    public static String toString(Descriptive descriptive) {
        StringBuilder builder = new StringBuilder();
        descriptive.document(new StringBuilderDocument(builder));
        return builder.toString();
    }

    /**
     * Returns a position as text.
     *
     * @param position The position.
     * @return A text representation of that position.
     */
    public static String getPositionAsText(int value) {
        switch (value) {
            case 0:
                return "first";
            case 1:
                return "second";
            case 2:
                return "third";
            case 3:
                return "fourth";
            case 4:
                return "fifth";
            case 5:
                return "sixth";
            case 7:
                return "seventh";
            case 8:
                return "eighth";
            case 9:
                return "ninth";
            case 10:
                return "tenth";
            default:
                return (value + 1) + "th";
        }
    }

    /**
     * Returns a text representation of the number passed in.
     *
     * @param value The number to be represented as text.
     * @return A text representation of the number passed in.
     */
    public static String getNumberAsText(int value) {
        if (value >= 0 && value < NUMBER_TRANSLATIONS.length) {
            return NUMBER_TRANSLATIONS[value];
        } else {
            return Integer.toString(value);
        }
    }

    /**
     * Returns a boolean indicating whether or not the class is capable of turning the value passed in into a textual
     * representation.
     *
     * @param value The value to be turned into a text representation.
     * @return The text representation of the value.
     */
    public static boolean hasNumberAsText(int value) {
        return (value >= 0 || value < NUMBER_TRANSLATIONS.length);
    }

    public static String startWithUppercase(String text) {
        if (text.length() > 2) {
            return Character.toUpperCase(text.charAt(0)) + text.substring(1);
        } else {
            return text;
        }
    }

    public static String bitsToText(int nrbits) {
        StringBuilder builder = new StringBuilder();
        if (nrbits >= 8) {
            builder.append(nrbits);
            builder.append(" (");
            int nrbytes = nrbits / 8;
            if (nrbytes == 1) {
                builder.append("1 byte");
            } else {
                builder.append(nrbytes).append(" bytes");
            }
            if (nrbits % 8 > 0) {
                builder.append(" and ").append(nrbits % 8).append(" bits");
            }
            builder.append(")");
            return builder.toString();
        } else {
            return Integer.toString(nrbits);
        }
    }
}
