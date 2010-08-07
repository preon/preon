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
