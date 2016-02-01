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

/**
 * A simple interface for resolving variable values. The interface is introduced to have a flexible bridge between Limbo
 * and Preon. With this interface, we can still have different ways of retrieving values.
 *
 * @author Wilfred Springer
 */
public interface Resolver {

    /**
     * Returns the value for a named variable.
     *
     * @param name The name.
     * @return The value.
     * @throws BindingException If the name does not happen to be bound to a variable at runtime.
     */
    Object get(String name) throws BindingException;

    /**
     * Returns a reference to the original Resolver for an expression.
     *
     * @return The original {@link Resolver}.
     */
    Resolver getOriginalResolver();

}
