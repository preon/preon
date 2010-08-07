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

import junit.framework.TestCase;

public class ClassUtilsTest extends TestCase {

    public void testCommonSuperType() {
        assertEquals(Number.class, ClassUtils.calculateCommonSuperType(Byte.class, Integer.class));
        assertEquals(Number.class, ClassUtils.calculateCommonSuperType(double.class, Integer.class));
        assertEquals(Integer.class, ClassUtils.calculateCommonSuperType(int.class, Integer.class));
        assertEquals(Object.class, ClassUtils.calculateCommonSuperType(Byte.class, String.class));
    }
    

}
