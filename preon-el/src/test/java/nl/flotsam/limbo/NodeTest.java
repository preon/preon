/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.flotsam.limbo;

import nl.flotsam.limbo.ast.ArithmeticNode;
import nl.flotsam.limbo.ast.IntegerNode;
import nl.flotsam.limbo.ast.Node;
import nl.flotsam.limbo.ast.RelationalNode;
import nl.flotsam.limbo.ast.ArithmeticNode.Operator;
import nl.flotsam.limbo.ast.RelationalNode.Relation;

import junit.framework.TestCase;

public class NodeTest extends TestCase {

    @SuppressWarnings("unchecked")
    public void testJustArithmetic() {
        Node<Integer,?> a = new IntegerNode(12);
        Node<Integer,?> b = new IntegerNode(13);
        Node<Integer,?> expr = new ArithmeticNode(Operator.plus, a, b);
        assertEquals(25, expr.eval(null).intValue());
    }

    @SuppressWarnings("unchecked")
    public void testJustRelation() {
        Node<Integer,?> a = new IntegerNode(12);
        Node<Integer,?> b = new IntegerNode(13);
        Node<Boolean,?> expr = new RelationalNode(Relation.GT, a, b);
        assertEquals(false, expr.eval(null).booleanValue());
    }

    @SuppressWarnings("unchecked")
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
