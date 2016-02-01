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
