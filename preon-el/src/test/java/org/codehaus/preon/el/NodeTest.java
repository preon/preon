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
