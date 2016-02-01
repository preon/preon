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

import org.codehaus.preon.el.ctx.MultiReference;

/**
 * A reference to an Object, based on an environment of type <code>E</type>.
 *
 * @author Wilfred Springer
 * @param <E>
 * The type of environment on which this reference is based.
 */
public interface Reference<E> extends ReferenceContext<E> {

    /**
     * Resolves the reference in a certain context.
     *
     * @param context The context in which references need to be resolved.
     * @return The object referenced by the Reference.
     */
    Object resolve(E context);

    /**
     * Returns the context in which this {@link Reference} is applicable.
     *
     * @return The {@link ReferenceContext} in which this reference is
     *         applicable.
     */
    ReferenceContext<E> getReferenceContext();

    /**
     * Returns a boolean indicating if the value referenced by this reference
     * can be assigned to a variable of the given type.
     *
     * @param type The type of variable that needs to receive the value.
     * @return A boolean indicating if the value reference by this reference can
     *         be assigned to a variable of the given type.
     */
    boolean isAssignableTo(Class<?> type);

    /**
     * Returns the type of value referenced by this reference.
     *
     * @return The type of value reference by this reference.
     */
    Class<?> getType();

//    /**
//     * Returns if the {@link Reference} supports the narrow
//     * {@link #narrow(Class)} operation; that is, if this operation returns
//     * <code>false</code>, then calling {@link #narrow(Class)} is pointless, so
//     * you might as well not do it.
//     * 
//     * @return A boolean indicating if it makes sense to call
//     *         {@link #narrow(Class)} on this reference: <code>true</code> if it
//     *         does, <code>false</code> if it doesn't.
//     */
//    boolean supportsNarrow();
//

    /**
     * Narrow the reference to a reference of the given type. The resulting
     * reference is expected to <em>always</em> evaluate to an instance of
     * <code>type</code>.
     * <p/>
     * <p/>
     * In order to understand the effect of this operation, it is important to
     * understand that there may sometimes be some ambiguity in a reference.
     * Take for instance the {@link MultiReference}. Calling
     * {@link MultiReference#selectAttribute(String)} will create another
     * {@link MultiReference}, pointing to different properties of different
     * types on different types of objects. Calling {@link #narrow(Class)} will
     * essentially drop all references not pointing to an instance of
     * <code>type</code>.
     *
     * @param type Narrows the reference to a reference that is guaranteed to
     *             point to a subclass of <code>type</code>.
     * @return A reference that is guaranteed to point to a subclass of
     *         <code>type</code>, or <code>null</code> if it cannot be narrowed.
     */
    Reference<E> narrow(Class<?> type);

    /**
     * Returns a boolean, indicating if the reference is based on the given context.
     *
     * @param context
     * @return <code>true</code> if the outcome of the reference is based on the given context; <code>false</code> otherwise.
     */
    boolean isBasedOn(ReferenceContext<E> context);

    Reference<E> rescope(ReferenceContext<E> context);

}
