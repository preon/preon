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

package nl.flotsam.limbo.ast;

import junit.framework.TestCase;

import static org.easymock.EasyMock.*;

/**
 * A number of tests for the {@link ConvertingNode}.
 * 
 * @author Wilfred Springer (wis)
 * 
 */
public class ConvertingNodeTest extends TestCase {

    /**
     * The source node.
     */
    private Node source;

    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() {
        source = createMock(Node.class);
    }

    /**
     * Tests a conversion from String to Integer.
     */
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
