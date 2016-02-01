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
 * A context for building references to elements in an environment of E. The
 * {@link ReferenceContext} interface allows us to build references relying on
 * different types of environments.
 * 
 * <p>
 * The main reason for having the {@link ReferenceContext} interface is to have
 * a better abstraction from the environment in which a Limbo expression is
 * executed. With these abstractions, the component <em>using</em> Limbo in a
 * certain context is still able to define the meaning of the selectors for that
 * particular context. This allows Quarks to build meaningful descriptions of
 * references by taking Codec information into account.
 * </p>
 * 
 * @author Wilfred Springer
 * 
 * @param <E>
 *            The type of environment that needs to be passed in in order to
 *            resolve a {@link Reference} created from this
 *            {@link ReferenceContext}.
 */
public interface ReferenceContext<E> extends Descriptive {

    /**
     * Creates a new {@link Reference}; the new {@link Reference} will be
     * resolved by first resolving the current reference, and then use the name
     * to navigate to another object associated to that object.
     * 
     * @param name
     *            The name to be used to resolve the Reference.
     * @return A new Reference, pointing to the object that is related to the
     *         original object by the given name.
     */
    Reference<E> selectAttribute(String name)
            throws BindingException;

    /**
     * Creates a new {@link Reference}; the new {@link Reference} will be
     * resolved by first resolving the current reference, and then use the index
     * to navigate to the associated object.
     * 
     * @param index
     *            The index to be used. (A Limbo expression.)
     * @return A new Reference, pointing to the object that is related to the
     *         original object by the given index.
     */
    Reference<E> selectItem(String index)
            throws BindingException;

    /**
     * Creates a new {@link Reference}; the new {@link Reference} will be
     * resolved by first resolving the current reference, and then use the index
     * to navigate to the associated object.
     * 
     * @param index
     *            The index to be used. (An object representation of an
     *            Expression on the same context.)
     * @return A new Reference, pointing to the object that is related to the
     *         original object by the given index.
     */
    Reference<E> selectItem(Expression<Integer, E> index)
            throws BindingException;

}
