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

public class ConvertersTest extends TestCase {

    public void testConversions() {
        assertNull(Converters.get(String.class, Boolean.class));
        Converter<Byte, Integer> converter1 = Converters.get(Byte.class, Integer.class);
        assertNotNull(converter1);
        assertEquals(new Integer(3), converter1.convert(new Byte((byte) 3)));
        Converter<Short, Integer> converter2 = Converters.get(Short.class, Integer.class);
        assertNotNull(converter2);
        assertEquals(new Integer(3), converter2.convert(new Short((byte) 3)));
        Converter<Long, Integer> converter3 = Converters.get(Long.class, Integer.class);
        assertNotNull(converter3);
        assertEquals(new Integer(3), converter3.convert(new Long(3)));
    }

}
