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

import org.junit.Test;
import static org.junit.Assert.*;
import org.codehaus.preon.el.BindingException;
import org.codehaus.preon.el.ast.ArithmeticNode.Operator;
import org.junit.Test;

public class ArithmeticNodeTest {

    @Test
    public void testAddIntegers() {
        Node<Integer, Object> node1 = new IntegerNode<Object>(5);
        Node<Integer, Object> node2 = new IntegerNode<Object>(5);
        Node<Integer, Object> sum = null;
        try {
            sum = ArithmeticNode.create(Operator.plus, node1, node2);
        } catch (BindingException be) {
            fail("Binding exception not expected for two integers.");
        }
        assertNotNull(sum.getType());
        assertEquals(Integer.class, sum.getType());
    }

    @Test(expected = BindingException.class)
    public void testAddNonIntegers1() {
        Node<String, Object> node1 = new StringNode<Object>("Whatever");
        Node<Integer, Object> node2 = new IntegerNode<Object>(5);
        Node<Integer, Object> sum = ArithmeticNode.create(Operator.plus, node1, node2);
    }
    
    @Test(expected = BindingException.class)
    public void testAddNonIntegers2() {
        Node<String, Object> node1 = new StringNode<Object>("Whatever");
        Node<Integer, Object> node2 = new IntegerNode<Object>(5);
        Node<Integer, Object> sum = ArithmeticNode.create(Operator.plus, node2, node1);
    }
}
