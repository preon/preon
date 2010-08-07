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

import java.util.Set;

import nl.flotsam.limbo.BindingException;
import nl.flotsam.limbo.Document;
import nl.flotsam.limbo.Reference;
import nl.flotsam.limbo.util.StringBuilderDocument;

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
                    nl.flotsam.limbo.Document target) {
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
                    nl.flotsam.limbo.Document target) {
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
                    nl.flotsam.limbo.Document target) {
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
                    nl.flotsam.limbo.Document target) {
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
                    nl.flotsam.limbo.Document target) {
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
                nl.flotsam.limbo.Document target);
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
     * @see nl.flotsam.limbo.ast.Node#eval(java.lang.Object)
     */
    public Integer eval(E context) {
        return operator.eval(context, lhs, rhs);
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.flotsam.limbo.Descriptive#document(nl.flotsam.limbo.Document)
     */
    public void document(Document target) {
        operator.document(lhs, rhs, target);
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.flotsam.limbo.ast.Node#gather(java.util.Set)
     */
    public void gather(Set<Reference<E>> references) {
        lhs.gather(references);
        rhs.gather(references);
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.flotsam.limbo.Expression#isParameterized()
     */
    public boolean isParameterized() {
        return lhs.isParameterized() || rhs.isParameterized();
    }

}
