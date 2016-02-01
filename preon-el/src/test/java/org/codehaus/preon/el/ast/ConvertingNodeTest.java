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
package org.codehaus.preon.el.ast;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import static org.easymock.EasyMock.*;

/**
 * A number of tests for the {@link ConvertingNode}.
 * 
 * @author Wilfred Springer (wis)
 * 
 */
public class ConvertingNodeTest {

    /**
     * The source node.
     */
    private Node source;

    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() {
        source = createMock(Node.class);
    }

    /**
     * Tests a conversion from String to Integer.
     */
    @Test
    public void testNonConvertingInteger() {
        expect(source.getType()).andReturn(String.class);
        replay(source);
        Node result = ConvertingNode.tryConversionToIntegerNode(source);
        assertFalse(result instanceof ConvertingNode);
        verify(source);
    }

    /**
     * Tests a conversion from Byte to Integer.
     */
    @Test
    public void testConvertingInteger() {
        Object context = new Object();
        expect(source.getType()).andReturn(Byte.class).times(2);
        expect(source.eval(context)).andReturn(new Byte((byte) 3));
        replay(source);
        Node result = ConvertingNode.tryConversionToIntegerNode(source);
        assertTrue(result instanceof ConvertingNode);
        assertEquals(Integer.class, result.getType());
        Object value = result.eval(context);
        assertEquals(Integer.class, value.getClass());
        assertEquals(3, ((Integer) value).intValue());
        assertNotNull(value);
        verify(source);
    }

}
