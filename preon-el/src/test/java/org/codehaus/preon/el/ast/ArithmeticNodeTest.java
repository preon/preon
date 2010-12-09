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
