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
