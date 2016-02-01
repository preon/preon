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
import org.codehaus.preon.el.*;
import org.codehaus.preon.el.ast.ArithmeticNode.Operator;
import org.codehaus.preon.el.util.StringBuilderDocument;

/**
 * A number of tests for expression simplification.
 * 
 * @author Wilfred Springer (wis)
 * 
 */
public class NodeSimplificationTest {

    @Test
    public void testSimplifySimple() {
        IntegerNode<Object> node1 = new IntegerNode<Object>(12);
        IntegerNode<Object> node2 = new IntegerNode<Object>(5);
        ArithmeticNode<Object> node3 = new ArithmeticNode<Object>(Operator.plus, node1, node2);
        node3.simplify();
        StringBuilderDocument doc = new StringBuilderDocument();
        node3.document(doc);
        assertEquals("the sum of 12 and 5", doc.toString());
        Node<Integer, Object> node4 = node3.simplify();
        doc = new StringBuilderDocument();
        node4.document(doc);
        assertEquals("17", doc.toString());
    }

    @Test
    public void testSimplifyTriple() {
        IntegerNode<Object> node1 = new IntegerNode<Object>(12);
        IntegerNode<Object> node2 = new IntegerNode<Object>(5);
        IntegerNode<Object> node3 = new IntegerNode<Object>(5);
        ArithmeticNode<Object> node4 = new ArithmeticNode<Object>(Operator.plus, node1, node2);
        node4 = new ArithmeticNode<Object>(Operator.plus, node4, node3);
        Node<Integer, Object> result = node4.simplify();
        StringBuilderDocument doc = new StringBuilderDocument();
        result.document(doc);
        assertEquals("22", doc.toString());
    }

    @Test
    public void testTripleWithVarible() {
        IntegerNode<Object> node1 = new IntegerNode<Object>(12);
        IntegerNode<Object> node2 = new IntegerNode<Object>(5);
        IntegerReferenceNode<Object> node3 = new IntegerReferenceNode<Object>(
                new TestReference("a"));
        ArithmeticNode<Object> node4 = new ArithmeticNode<Object>(Operator.plus, node1, node2);
        node4 = new ArithmeticNode<Object>(Operator.plus, node4, node3);
        Node<Integer, Object> result = node4.simplify();
        StringBuilderDocument doc = new StringBuilderDocument();
        result.document(doc);
        assertEquals("the sum of 17 and a", doc.toString());
    }

    private class TestReference implements Reference<Object> {

        private String name;

        public TestReference(String name) {
            this.name = name;
        }

        public ReferenceContext<Object> getReferenceContext() {
            return null;
        }

        public boolean isAssignableTo(Class<?> type) {
            return false;
        }

        public java.lang.Object resolve(Object context) {
            return null;
        }

        public Reference<Object> selectAttribute(String name) throws BindingException {
            return null;
        }

        public Reference<Object> selectItem(String index) throws BindingException {
            return null;
        }

        public Reference<Object> selectItem(Expression<Integer, Object> index)
                throws BindingException {
            return null;
        }

        public void document(Document target) {
            target.text(name);
        }

        public Class<?> getType() {
            return null;
        }

        public Reference<Object> narrow(Class<?> type) {
            return null;
        }

        public boolean isBasedOn(ReferenceContext<java.lang.Object> objectReferenceContext) {
            return false;
        }

        public Reference<Object> rescope(ReferenceContext<Object> objectReferenceContext) {
            return this;
        }

    }

}
