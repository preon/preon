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
