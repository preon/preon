/**
 * Copyright (C) 2009-2010 Wilfred Springer
 *
 * This file is part of Preon.
 *
 * Preon is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * Preon is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Preon; see the file COPYING. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
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
