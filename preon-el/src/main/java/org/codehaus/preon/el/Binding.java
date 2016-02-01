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
package org.codehaus.preon.el;

/**
 * The interface implemented by objects capable of creating a
 * {@link ReferenceContext} from a class passed in. The {@link ReferenceContext}
 * built will allow clients to access <em>attributes</em> and <em>items</em>
 * from instances of that class.
 * 
 * <p>
 * <em>Note:</em> this does not necessarily mean accessing bean properties or
 * fields. In fact, the {@link ReferenceContext} actually allows you to abstract
 * from <em>any</em> state associated to the instance.
 * </p>
 * 
 * @author Wilfred Springer
 * 
 */
public interface Binding {

    /**
     * Returns a {@link ReferenceContext} exposing data of the class.
     * 
     * @param <C>
     *            The type parameter of the {@link ReferenceContext} returned.
     * @param type
     *            The type of context to which the {@link ReferenceContext}
     *            applies.
     * @return A {@link ReferenceContext} providing a structured interface to
     *         data associated to instances of that class.
     */
    <C> ReferenceContext<C> create(Class<C> type);

}
