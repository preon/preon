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
