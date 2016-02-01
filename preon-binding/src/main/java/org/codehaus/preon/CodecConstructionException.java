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

import org.codehaus.preon.el.BindingException;
import org.codehaus.preon.el.InvalidExpressionException;


/**
 * The exception thrown when a {@link CodecFactory} fails to construct a {@link Codec}. See different constructors for
 * the situations in which this exception might be thrown.
 *
 * @author Wilfred Springer
 */
@SuppressWarnings("serial")
public class CodecConstructionException extends RuntimeException {

    /**
     * Constructs an exception for the case in which the {@link CodecFactory} fails to parse the expression.
     *
     * @param ice The exception containing the details about the reason why it turned out to be impossible to build an
     *            interpreter for an expression.
     */
    public CodecConstructionException(InvalidExpressionException ice) {
        super("Failed to construct codec.", ice);
    }

    /**
     * Constructs an exception for the case in which the {@link CodecFactory} fails to bind the expression to the
     * context.
     *
     * @param ice The exception containing the details about the reason why it turned out to be impossible to build an
     *            interpreter for an expression.
     */
    public CodecConstructionException(BindingException be) {
        super("Failed to construct codec.", be);
    }

    public CodecConstructionException(String message) {
        super(message);
    }

}
