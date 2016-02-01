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
package org.codehaus.preon.util;


/*package org.codehaus.preon.util;

import junit.framework.TestCase;

public class DefaultReferenceResolverTest extends TestCase {

    public void testResolverReference() {
        DefaultReferenceResolver resolver = new DefaultReferenceResolver(Test1.class);
        final Test1 value = new Test1();
        Object result = resolver.resolve(value, "value1");
        assertNotNull(result);
        assertEquals(Test2.class, result.getClass());
        result = resolver.resolve(value, "value1", "value1");
        assertNotNull(result);
        assertEquals(String.class, result.getClass());
        assertEquals("foobar", result);
    }

    public static class Test1 {

        public Test2 value1 = new Test2();

    }

    public static class Test2 {

        public String value1 = "foobar";

    }

}
*/