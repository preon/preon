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
package org.codehaus.preon.annotation;

import org.codehaus.preon.buffer.ByteOrder;

/**
 * The annotation allowing you to define a number of choices, based a prefix of a certain {@link #prefixSize() size}.
 *
 * @author Wilfred Springer (wis)
 */
public @interface Choices {

    /**
     * The number of bits to be read for determining the prefix.
     *
     * @return The number of bits to be read for determining the prefix.
     */
    int prefixSize() default 0;

    /**
     * The byte order to take into account when returning a representation of the first {@link #prefixSize() size} bits
     * read as a prefix.
     *
     * @return The byte order to take into account when returning a representation of the first {@link #prefixSize()
     *         size} bits read as a prefix.
     */
    ByteOrder byteOrder() default ByteOrder.BigEndian;

    /**
     * The choices to select from.
     *
     * @return The choices to select from.
     */
    Choice[] alternatives() default {};

    /** The default type, if any. */
    Class<?> defaultType() default Void.class;

    /** The annotation holding a single choice. */
    public @interface Choice {

        /**
         * The condition that needs to hold, if an instance of {@link #type() type} is to be decoded. A Limbo expression
         * exposing at least one variable: the variable prefix, representing the value read using the {@link
         * Choices#prefixSize()} and {@link Choices#byteOrder()} specifications.
         *
         * @return The condition that needs to hold, if an instance of {@link #type() type} is to be decoded.
         */
        String condition();

        /**
         * The type to decode in case the {@link #condition()} holds.
         *
         * @return The type to decode in case the {@link #condition()} holds.
         */
        Class<?> type();

    }

}
