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

import java.util.Map;

import junit.framework.TestCase;
import org.codehaus.preon.annotation.BoundEnumOption;

public class EnumUtilsTest extends TestCase {

    public void testFullyDefinedEnum() {
        Map<Long, Direction> index = EnumUtils.getBoundEnumOptionIndex(Direction.class);
        assertEquals(Direction.Left, index.get(1L));
        assertEquals(Direction.Right, index.get(2L));
        assertEquals(null, index.get(3L));
    }

    public void testPartlyDefinedEnum() {
        Map<Long, Hours> index = EnumUtils.getBoundEnumOptionIndex(Hours.class);
        assertEquals(Hours.Working, index.get(1L));
        assertEquals(Hours.Leisure, index.get(2L));
        assertEquals(null, index.get(3L));
        assertEquals(Hours.Sleep, index.get(null));
    }

    public enum Direction {
        @BoundEnumOption(1)
        Left,

        @BoundEnumOption(2)
        Right;
    }

    public enum Hours {

        @BoundEnumOption(1)
        Working,

        @BoundEnumOption(2)
        Leisure,

        Sleep;


    }


}
