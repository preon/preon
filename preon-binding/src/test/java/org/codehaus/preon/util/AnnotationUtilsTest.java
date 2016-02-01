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

import junit.framework.TestCase;
import org.codehaus.preon.annotation.Purpose;
import org.codehaus.preon.annotation.TypePrefix;

/**
 * Just checking the walls for some Annotation properties.
 *
 * @author Wilfred Springer (wis)
 */
public class AnnotationUtilsTest extends TestCase {

    public void testEquals() {
        assertTrue(AnnotationUtils.equivalent(Test1.class, Test2.class));
        assertFalse(AnnotationUtils.equivalent(Test2.class, Test3.class));
        assertFalse(AnnotationUtils.equivalent(Test3.class, Test4.class));
        assertFalse(AnnotationUtils.equivalent(Test4.class, Test5.class));
        assertFalse(AnnotationUtils.equivalent(Test5.class, Test4.class));
    }

    @TypePrefix(size = 2, value = "blaat")
    @Purpose("whatever")
    public static class Test1 {
    }

    @TypePrefix(size = 2, value = "blaat")
    @Purpose("whatever")
    public static class Test2 {
    }

    @TypePrefix(size = 2, value = "blaat")
    @Purpose("foobar")
    public static class Test3 {
    }

    @Purpose("foobar")
    public static class Test4 {
    }

    @TypePrefix(size = 2, value = "blaat")
    public static class Test5 {
    }

}
