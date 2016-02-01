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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for adding metadata to String fields, informing the framework how to decode and encode the data.
 *
 * @author Wilfred Springer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BoundString {

    class Encoding {
		/* This is a compatibility stub. Encodings are strings now, and this ensures
		 * that anyone using the old syntax will get the same results.
		 * */
        public static final String ASCII = "US-ASCII";
        public static final String ISO_8859_1 = "ISO-8859-1";

    }

    /**
     * Returns the number of bytes to be interpreted as a String.
     *
     * @return The number of bytes to be interpreted as a String. (Can be a Limbo expression.)
     */
    String size() default "";

    /**
     * Returns the type of encoding used for the String.
     *
     * @return The type of encoding used for the String.
     */
    String encoding() default "US-ASCII";

    /**
     * The String that needs to be matched.
     *
     * @return The String that needs to be matched. Or the empty String if matching is not important.
     */
     
    String match() default "";
    
    /* I've left this in, but I don't use this code anywhere in the actual
     * factory. It might be possible to alter the factories to use
     * ByteConverters, by wrapping around ByteBuffer in some clever way, 
     * but my feeling is that the aims of this code would be better
     * achieved by Charsets.
     * */

    Class<? extends ByteConverter> converter() default NullConverter.class;

    public interface ByteConverter {

        byte convert(byte in);

        byte revert(byte in);

        String getDescription();

    }

    public class NullConverter implements ByteConverter {

        public byte convert(byte in) {
            return in;
        }

        public byte revert(byte in) {
            return in;
        }

        public String getDescription() {
            return "";
        }

    }

}
