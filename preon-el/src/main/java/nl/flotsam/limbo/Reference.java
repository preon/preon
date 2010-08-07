/*
 * Copyright 2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package nl.flotsam.limbo;

import nl.flotsam.limbo.ctx.MultiReference;

/**
 * A reference to an Object, based on an environment of type <code>E</type>.
 * 
 * @author Wilfred Springer
 * 
 * @param <E>
 *            The type of environment on which this reference is based.
 */
public interface Reference<E> extends ReferenceContext<E> {

    /**
     * Resolves the reference in a certain context.
     * 
     * @param context
     *            The context in which references need to be resolved.
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
     * @param type
     *            The type of variable that needs to receive the value.
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
     * 
     * <p>
     * In order to understand the effect of this operation, it is important to
     * understand that there may sometimes be some ambiguity in a reference.
     * Take for instance the {@link MultiReference}. Calling
     * {@link MultiReference#selectAttribute(String)} will create another
     * {@link MultiReference}, pointing to different properties of different
     * types on different types of objects. Calling {@link #narrow(Class)} will
     * essentially drop all references not pointing to an instance of
     * <code>type</code>.
     * 
     * @param type
     *            Narrows the reference to a reference that is guaranteed to
     *            point to a subclass of <code>type</code>.
     * @return A reference that is guaranteed to point to a subclass of
     *         <code>type</code>, or <code>null</code> if it cannot be narrowed.
     */
    Reference<E> narrow(Class<?> type);

}
