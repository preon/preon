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
package org.codehaus.preon.el.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class ConvertersTest {

    @Test
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
