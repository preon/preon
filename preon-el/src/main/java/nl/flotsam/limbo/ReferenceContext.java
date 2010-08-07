/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.flotsam.limbo;

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
