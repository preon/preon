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

import java.util.Set;

import org.codehaus.preon.el.BindingException;
import org.codehaus.preon.el.Document;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.ReferenceContext;
import org.codehaus.preon.el.util.StringBuilderDocument;

/**
 * A {@link Node} representing a combinatorial boolean operator.
 * 
 * @author Wilfred Springer
 * 
 * @param <E>
 *            The type of context on which this node would be applied.
 */
public class BooleanOperatorNode<E> extends AbstractNode<Boolean, E> {

    /**
     * The left-hand side of the operation.
     */
    private Node<Boolean, E> lhs;

    /**
     * The right-hand side of the operation.
     */
    private Node<Boolean, E> rhs;

    /**
     * The operator to apply.
     */
    private BooleanOperator operator;

    public enum BooleanOperator {

        AND {
            <E> boolean holds(E context, Node<Boolean, E> lhs, Node<Boolean, E> rhs) {
                return lhs.eval(context) && rhs.eval(context);
            }

            <E> void document(Node<Boolean, E> lhs, Node<Boolean, E> rhs,
                    org.codehaus.preon.el.Document target) {
                lhs.document(target);
                target.text(" and ");
                rhs.document(target);
            }
        },

        OR {
            <E> boolean holds(E context, Node<Boolean, E> lhs, Node<Boolean, E> rhs) {
                return lhs.eval(context) || rhs.eval(context);
            }

            <E> void document(Node<Boolean, E> lhs, Node<Boolean, E> rhs,
                    org.codehaus.preon.el.Document target) {
                lhs.document(target);
                target.text(" or ");
                rhs.document(target);
            }
        };

        abstract <E> boolean holds(E context, Node<Boolean, E> lhs, Node<Boolean, E> rhs);

        abstract <E> void document(Node<Boolean, E> lhs, Node<Boolean, E> rhs,
                org.codehaus.preon.el.Document target);

    }

    /**
     * Constructs a new instance, accepting the operator, and the left-hand and
     * right-hand side.
     * 
     * @param operator
     *            The operator to apply.
     * @param lhs
     *            The left-hand side of the operation.
     * @param rhs
     *            The right-hand side of the operation.
     */
    public BooleanOperatorNode(BooleanOperator operator, Node<Boolean, E> lhs, Node<Boolean, E> rhs) {
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.ast.Node#eval(java.lang.Object)
     */
    public Boolean eval(E context) {
        return operator.holds(context, lhs, rhs);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.ast.Node#getType()
     */
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.ast.Node#simplify()
     */
    public Node<Boolean, E> simplify() {
        return this;
    }

    public Node<Boolean, E> rescope(ReferenceContext<E> context) {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.Descriptive#document(org.codehaus.preon.el.Document)
     */
    public void document(Document target) {
        operator.document(lhs, rhs, target);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.ast.Node#gather(java.util.Set)
     */
    public void gather(Set<Reference<E>> references) {
        lhs.gather(references);
        rhs.gather(references);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.Expression#isParameterized()
     */
    public boolean isParameterized() {
        return lhs.isParameterized() || rhs.isParameterized();
    }

    /**
     * Constructs a new {@link BooleanOperatorNode}.
     * 
     * @param <E>
     * @param operator
     * @param lhs
     * @param rhs
     * @return
     */
    public static <E> BooleanOperatorNode<E> create(BooleanOperator operator, Node<?, E> lhs,
            Node<?, E> rhs) {
        Node<Boolean, E> booleanLhs = createBooleanNode(lhs);
        Node<Boolean, E> booleanRhs = createBooleanNode(rhs);
        return new BooleanOperatorNode<E>(operator, booleanLhs, booleanRhs);
    }

    /**
     * Creates a boolean {@link Node} from the untyped {@link Node} passed in.
     * 
     * @param <E> The type of context on which the node will be applied.
     * @param node The untyped node.
     * @return A boolean node.
     */
    public static <E> Node<Boolean, E> createBooleanNode(Node<?, E> node) {
        if (!boolean.class.isAssignableFrom(node.getType())
                && !Boolean.class.isAssignableFrom(node.getType())) {
            StringBuilder builder = new StringBuilder();
            node.document(new StringBuilderDocument(builder));
            throw new BindingException("Reference " + builder.toString()
                    + " does not resolve to boolean.");
        } else {
            return (Node<Boolean, E>) node;
        }
    }

    @Override
    public boolean isConstantFor(ReferenceContext<E> eReferenceContext) {
        return lhs.isConstantFor(eReferenceContext) && rhs.isConstantFor(eReferenceContext);

    }
}
