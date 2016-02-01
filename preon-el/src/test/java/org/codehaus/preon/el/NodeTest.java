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
package org.codehaus.preon.el;

import org.codehaus.preon.el.ast.ArithmeticNode;
import org.codehaus.preon.el.ast.IntegerNode;
import org.codehaus.preon.el.ast.Node;
import org.codehaus.preon.el.ast.RelationalNode;
import org.codehaus.preon.el.ast.ArithmeticNode.Operator;
import org.codehaus.preon.el.ast.RelationalNode.Relation;
import org.junit.Test;
import static org.junit.Assert.*;


public class NodeTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testJustArithmetic() {
        Node<Integer,?> a = new IntegerNode(12);
        Node<Integer,?> b = new IntegerNode(13);
        Node<Integer,?> expr = new ArithmeticNode(Operator.plus, a, b);
        assertEquals(25, expr.eval(null).intValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testJustRelation() {
        Node<Integer,?> a = new IntegerNode(12);
        Node<Integer,?> b = new IntegerNode(13);
        Node<Boolean,?> expr = new RelationalNode(Relation.GT, a, b);
        assertEquals(false, expr.eval(null).booleanValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCompound() {
        Node<Integer,?> a = new IntegerNode(12);
        Node<Integer,?> b = new IntegerNode(13);
        Node<Integer,?> c = new IntegerNode(14);
        Node<Integer,?> d = new IntegerNode(15);
        Node<Integer,?> expr1 = new ArithmeticNode(Operator.plus, a, b);
        Node<Integer,?> expr2 = new ArithmeticNode(Operator.minus, c, d);
        Node<Boolean,?> expr = new RelationalNode(Relation.GT, expr1, expr2);
        assertEquals(true, expr.eval(null).booleanValue());
    }

}
