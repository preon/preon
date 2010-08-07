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

/**
 * The interface to be implemented by objects capable of describing itself using
 * an object passed in. This object could be a {@link StringBuilder}, a
 * {@link StringBuffer}, or whatever you prefer.
 * 
 * @author Wilfred Springer
 * 
 */
public interface Descriptive {

    /**
     * Documents the object, targeting the object passed in.
     * 
     * @param target
     *            The receiving object.
     */
    void document(Document target);

}
