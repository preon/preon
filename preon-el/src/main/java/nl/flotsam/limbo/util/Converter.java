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

package nl.flotsam.limbo.util;

/**
 * The interface defining an object capable of converting instances of {@code T} to {@code V}.
 * 
 * @param <T> The type of object to be converted.
 * @param <V> The target type.
 */
public interface Converter<T, V> {

    /**
     * Accepts an instance of {@code T} and turns it into the target type.
     * 
     * @param instance The object to be converted.
     * @return A new instance of {@code V}.
     */
    V convert(T instance);

    /**
     * The target type.
     * 
     * @return The target type.
     */
    Class<V> getTargetType();

}
