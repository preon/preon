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
public @interface LengthPrefix {

    /**
     * The type of endianness: either {@link ByteOrder#LittleEndian} or {@link ByteOrder#BigEndian}.
     *
     * @return The type of endianness. Defaults to {@link ByteOrder#LittleEndian}.
     */
    ByteOrder endian() default ByteOrder.LittleEndian;

    /**
     * The number of bits used to represent the numeric value. Defaults to 0, allowing the {@link Codec} to make its own
     * decision on the number of bits to be used; however, they are expected to respect the following defaults:
     */
    String size() default "";

}
