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

/**
 * The base exception class for any kind of problem that might occur <em>while</em> encoding/decoding.
 *
 * @author Wilfred Springer
 */
@SuppressWarnings("serial")
public class CodecException extends Exception {

    /**
     * Constructs a new instance, accepting the cause of the problem.
     *
     * @param cause The cause of the problem.
     */
    public CodecException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new instance, accepting a message explaining the problem.
     *
     * @param message The message explaining the problem.
     */
    public CodecException(String message) {
        super(message);
    }

    /**
     * Constructs a new instance, accepting the cause of the problem.
     *
     * @param message A message explaining the problem.
     * @param cause   The cause of the problem.
     */
    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }

}
