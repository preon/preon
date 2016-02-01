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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for marking fields to be bound to the BitBuffer. Used when the default decoding approach (which is to
 * examine the type of field, and guess a decoder from that) doesn't fit your needs.
 *
 * @author Wilfred Springer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BoundObject {

    /**
     * The type of object to be decoded. (Use this when you want to override the type detected at runtime.)
     *
     * @return The type of object to be decoded. (Use this when you want to override the type detected at runtime.)
     */
    Class<?> type() default Void.class;

    /**
     * The types of objects to be decoded. Use this if you want the framework to select a certain class based on a
     * couple of leading bits prefixing the actual data. Note that it expects every type in the array to have the {@link
     * TypePrefix} annotation.
     *
     * @return The types of object to be decoded.
     */
    Class<?>[] types() default {};

    /** Indicates that the type prefix must be ignored. Note that this is fairly experimental. Use this with cause. */
    boolean ommitTypePrefix() default false;

    /**
     * The choices to select from, based on a prefix of a certain size.
     *
     * @return The choices to select from, based on a prefix of a certain size.
     */
    Choices selectFrom() default @Choices(alternatives = {});

}
