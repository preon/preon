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

import nl.flotsam.limbo.BindingException;
import nl.flotsam.limbo.ast.ArithmeticNode.Operator;
import junit.framework.TestCase;

public class ArithmeticNodeTest extends TestCase {

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
    
    public void testAddNonIntegers() {
        Node<String, Object> node1 = new StringNode<Object>("Whatever");
        Node<Integer, Object> node2 = new IntegerNode<Object>(5);
        Node<Integer, Object> sum = null;
        try {
            sum = ArithmeticNode.create(Operator.plus, node1, node2);
            fail("Expected binding exception.");
        } catch(BindingException be) {
            // Right on.
        }
        try {
            sum = ArithmeticNode.create(Operator.plus, node2, node1);
            fail("Expected binding exception.");
        } catch(BindingException be) {
            // Right on.
        }

    }

}
