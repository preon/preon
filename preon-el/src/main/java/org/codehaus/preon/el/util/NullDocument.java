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
package org.codehaus.preon.el.util;

import org.codehaus.preon.el.Document;


/**
 * A null implementation of {@link Document}.
 * 
 */
public class NullDocument implements Document {

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.el.Document#detail(java.lang.String)
     */
    public Document detail(String text) {
        return this;
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.el.Document#link(java.lang.Object, java.lang.String)
     */
    public void link(Object object, String text) {
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.el.Document#text(java.lang.String)
     */
    public void text(String text) {
    }

}
