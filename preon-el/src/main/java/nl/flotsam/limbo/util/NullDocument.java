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

package nl.flotsam.limbo.util;

import nl.flotsam.limbo.Document;


/**
 * A null implementation of {@link Document}.
 * 
 */
public class NullDocument implements Document {

    /*
     * (non-Javadoc)
     * @see nl.flotsam.limbo.Document#detail(java.lang.String)
     */
    public Document detail(String text) {
        return this;
    }

    /*
     * (non-Javadoc)
     * @see nl.flotsam.limbo.Document#link(java.lang.Object, java.lang.String)
     */
    public void link(Object object, String text) {
    }

    /*
     * (non-Javadoc)
     * @see nl.flotsam.limbo.Document#text(java.lang.String)
     */
    public void text(String text) {
    }

}
