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
 * A representation of an arithmetic node in the tree representing an
 * expression, holding a left-hand value, an operator and a right-hand value.
 * 
 * @author Wilfred Springer
 * 
 */
public class ArithmeticNode<E> extends AbstractNode<Integer, E> {

    /**
     * The left-hand side of the expression.
     */
    private Node<Integer, E> lhs;

    /**
     * The right-hand side of the expression.
     */
    private Node<Integer, E> rhs;

    /**
     * The operator.
     */
    private Operator operator;

    /**
     * An enumeration of all operators allowed. (Note that every value also
     * implements the strategy for computing and describing the expression with
     * the infix operator.)
     * 
     */
    public enum Operator {
        pow {
            <E> int eval(E context, Node<Integer, E> a, Node<Integer, E> b) {
                return (int) Math.pow(a.eval(context).intValue(), b.eval(context).intValue());
            }

            <E> void document(Node<Integer, E> a, Node<Integer, E> b,
                    org.codehaus.preon.el.Document target) {
                a.document(target);
                target.text(" to the power of ");
                b.document(target);
            }
        },
        div {
            <E> int eval(E context, Node<Integer, E> a, Node<Integer, E> b) {
                return a.eval(context).intValue() / b.eval(context).intValue();
            }

            <E> void document(Node<Integer, E> a, Node<Integer, E> b,
                    org.codehaus.preon.el.Document target) {
                a.document(target);
                target.text(" divided by ");
                b.document(target);
            }
        },
        plus {
            <E> int eval(E context, Node<Integer, E> a, Node<Integer, E> b) {
                return a.eval(context).intValue() + b.eval(context).intValue();
            }

            <E> void document(Node<Integer, E> a, Node<Integer, E> b,
                    org.codehaus.preon.el.Document target) {
                target.text("the sum of ");
                a.document(target);
                target.text(" and ");
                b.document(target);
            }
        },
        minus {
            <E> int eval(E context, Node<Integer, E> a, Node<Integer, E> b) {
                return a.eval(context).intValue() - b.eval(context).intValue();
            }

            <E> void document(Node<Integer, E> a, Node<Integer, E> b,
                    org.codehaus.preon.el.Document target) {
                target.text("the difference between ");
                a.document(target);
                target.text(" and ");
                b.document(target);
            }
        },
        mult {
            <E> int eval(E context, Node<Integer, E> a, Node<Integer, E> b) {
                return a.eval(context).intValue() * b.eval(context).intValue();
            }

            <E> void document(Node<Integer, E> a, Node<Integer, E> b,
                    org.codehaus.preon.el.Document target) {
                a.document(target);
                target.text(" times ");
                b.document(target);
            }
        };

        /**
         * Evaluates application of the infix operator on the two terms passed
         * in.
         * 
         * @param resolver
         *            The object capable of resolving variable references.
         * @param lhs
         *            The left-hand side of the expression.
         * @param rhs
         *            The right-hand side of the expression.
         * @return An integer value.
         */
        abstract <E> int eval(E context, Node<Integer, E> lhs, Node<Integer, E> rhs);

        /**
         * writes the expression.
         * 
         * @param descriptor
         *            The object capable of describing variable references.
         * @param lhs
         *            The left-hand side of the expression.
         * @param rhs
         *            The right-hand side of the expression.
         * @param target
         *            The object receiving the description.
         */
        abstract <E> void document(Node<Integer, E> lhs, Node<Integer, E> rhs,
                org.codehaus.preon.el.Document target);
    }

    /**
     * Constructs a new ArithmeticNode, accepting the operator, the left-hand
     * side and the right-hand side.
     * 
     * @param operator
     * @param lhs
     * @param rhs
     */
    public ArithmeticNode(Operator operator, Node<Integer, E> lhs, Node<Integer, E> rhs) {
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    /**
     * Attempts to construct a new {@link ArithmeticNode} from the operator and
     * other nodes passed in.
     * 
     * @param <E>
     *            The type of context for evaluation of references.
     * @param operator
     *            The operator to apply.
     * @param lhs
     *            The left hand side of the operation.
     * @param rhs
     *            The right hand side of the operation.
     * @return The newly constructed {@link ArithmeticNode}.
     */
    public static <E> ArithmeticNode<E> create(Operator operator, Node<?, E> lhs, Node<?, E> rhs)
            throws BindingException {
        Node<Integer, E> lhsInteger = createIntegerNode(operator, lhs);
        Node<Integer, E> rhsInteger = createIntegerNode(operator, rhs);
        return new ArithmeticNode<E>(operator, lhsInteger, rhsInteger);
    }

    /**
     * Constructs a new
     * 
     * @param <E>
     * @param operator
     * @param node
     * @return
     * @throws BindingException
     */
    private static <E> Node<Integer, E> createIntegerNode(Operator operator, Node<?, E> node)
            throws BindingException {
        if (!Integer.class.isAssignableFrom(node.getType())
                && !int.class.isAssignableFrom(node.getType())) {
            StringBuilder builder = new StringBuilder();
            node.document(new StringBuilderDocument(builder));
            throw new BindingException("Reference " + builder.toString()
                    + " does not resolve to integer.");
        } else {
            return (Node<Integer, E>) node;
        }
    }

    // JavaDoc inherited
    public Class<Integer> getType() {
        return Integer.class;
    }

    // JavaDoc inherited
    public Node<Integer, E> simplify() {
        Node<Integer, E> simplifiedLhs = lhs.simplify();
        Node<Integer, E> simplifiedRhs = rhs.simplify();
        if (simplifiedLhs instanceof IntegerNode && simplifiedRhs instanceof IntegerNode) {
            return new IntegerNode<E>(this.eval(null));
        } else if (simplifiedLhs instanceof IntegerNode) {
            return new ArithmeticNode<E>(operator, simplifiedLhs, rhs);
        } else if (simplifiedRhs instanceof IntegerNode) {
            return new ArithmeticNode<E>(operator, lhs, simplifiedRhs);
        }
        return this;
    }

    public Node<Integer, E> rescope(ReferenceContext<E> context) {
        return new ArithmeticNode<E>(operator, lhs.rescope(context), rhs.rescope(context));
    }

    /**
     * Returns the left-hand side of the expression.
     * 
     * @return The left-hand side of the expression.
     */
    public Node<Integer, E> getLhs() {
        return lhs;
    }

    /**
     * Returns the right-hand side of the expression.
     * 
     * @return The right-hand side of the expression.
     */
    public Node<Integer, E> getRhs() {
        return rhs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.ast.Node#eval(java.lang.Object)
     */
    public Integer eval(E context) {
        return operator.eval(context, lhs, rhs);
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

    @Override
    public boolean isConstantFor(ReferenceContext<E> context) {
        return lhs.isConstantFor(context) && rhs.isConstantFor(context);
    }
}
