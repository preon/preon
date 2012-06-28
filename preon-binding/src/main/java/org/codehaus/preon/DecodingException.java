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
package org.codehaus.preon;

import java.io.UnsupportedEncodingException;

import org.codehaus.preon.el.BindingException;
import org.codehaus.preon.buffer.BitBuffer;

/**
 * The exception thrown when the {@link Codec} fails to decode a value from the {@link BitBuffer}. See JavaDoc comments
 * of the constructors for more information on the typical circumstances causing this exception to be thrown.
 *
 * @author Wilfred Springer
 */
@SuppressWarnings("serial")
public class DecodingException extends CodecException {

    /**
     * Constructs an exception to be thrown when the {@link Codec} fails to instantiate the value.
     *
     * @param ie
     */
    public DecodingException(InstantiationException ie) {
        super(ie);
    }

    public DecodingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DecodingException(IllegalAccessException iae) {
        super(iae);
    }

    public DecodingException(BindingException be) {
        super("Failed to decode data ", be);
    }

    public DecodingException(UnsupportedEncodingException uee) {
        super(uee);
    }

    public DecodingException(IllegalStateException ise) {
        super(ise);
    }

    public DecodingException(String message) {
        super(message);
    }

    public DecodingException(Class<?> type, InstantiationException ie) {
        super("Failed to create instance of " + type.getSimpleName(), ie);
    }

}
