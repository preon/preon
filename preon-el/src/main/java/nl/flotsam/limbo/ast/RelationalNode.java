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
import nl.flotsam.limbo.util.ClassUtils;
import nl.flotsam.limbo.util.StringBuilderDocument;

/**
 * The node representing (part of) an expression that translates to a boolean
 * value, based on two integer-type of nodes passed in.
 * 
 * @author Wilfred Springer
 * 
 */
public class RelationalNode<T extends Comparable<T>, E> extends
        AbstractNode<Boolean, E> {

    public enum Relation {
        GT {
            <T, E> boolean holds(E context, Node<T, E> a, Node<T, E> b) {
                return a.compareTo(context, b) > 0;
            }

            <T, E> void document(Node<T, E> a, Node<T, E> b,
                    nl.flotsam.limbo.Document target) {
                a.document(target);
                target.text(" is greater than ");
                b.document(target);
            }
        },
        GTE {
            <T, E> boolean holds(E context, Node<T, E> a, Node<T, E> b) {
                return a.compareTo(context, b) >= 0;
            }

            <T, E> void document(Node<T, E> a, Node<T, E> b,
                    nl.flotsam.limbo.Document target) {
                a.document(target);
                target.text(" is greater than or equal to ");
                b.document(target);
            }
        },
        EQ {
            <T, E> boolean holds(E context, Node<T, E> a, Node<T, E> b) {
                return a.compareTo(context, b) == 0;
            }

            <T, E> void document(Node<T, E> a, Node<T, E> b,
                    nl.flotsam.limbo.Document target) {
                a.document(target);
                target.text(" equals ");
                b.document(target);
            }
        },
        LT {
            <T, E> boolean holds(E context, Node<T, E> a, Node<T, E> b) {
                return a.compareTo(context, b) < 0;
            }

            <T, E> void document(Node<T, E> a, Node<T, E> b,
                    nl.flotsam.limbo.Document target) {
                a.document(target);
                target.text(" is less than ");
                b.document(target);
            }
        },
        LTE {
            <T, E> boolean holds(E context, Node<T, E> a, Node<T, E> b) {
                return a.compareTo(context, b) <= 0;
            }

            <T, E> void document(Node<T, E> a, Node<T, E> b,
                    nl.flotsam.limbo.Document target) {
                a.document(target);
                target.text(" is less than or equal to ");
                b.document(target);
            }
        };

        abstract <T, E> boolean holds(E context, Node<T, E> lhs, Node<T, E> rhs);

        abstract <T, E> void document(Node<T, E> lhs, Node<T, E> rhs,
                nl.flotsam.limbo.Document target);
    }

    /**
     * The relationship that needs to be evaluated.
     */
    private Relation relation;

    /**
     * The left-hand side of the expression.
     */
    private Node<T, E> lhs;

    /**
     * The right-hand side of the expression.
     */
    private Node<T, E> rhs;

    /**
     * Constructs a new instance.
     * 
     * @param relation
     *            The relationship that needs to be evaluated.
     * @param lhs
     *            The left-hand side of the expression.
     * @param rhs
     *            The right-hand side of the expression.
     */
    public RelationalNode(Relation relation, Node<T, E> lhs, Node<T, E> rhs) {
        this.relation = relation;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    // JavaDoc inherited
    public Boolean eval(E context) {
        return relation.holds(context, lhs, rhs);
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.flotsam.limbo.ast.Node#getType()
     */
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.flotsam.limbo.ast.Node#simplify()
     */
    public Node<Boolean, E> simplify() {
        return this;
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
     * @see nl.flotsam.limbo.Descriptive#document(nl.flotsam.limbo.Document)
     */
    public void document(Document target) {
        relation.document(lhs, rhs, target);
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.flotsam.limbo.Expression#isParameterized()
     */
    public boolean isParameterized() {
        return lhs.isParameterized() || rhs.isParameterized();
    }

    public static <E, T extends Comparable<T>> RelationalNode<T, E> create(
            Relation operator, Node<?, E> lhs, Node<?, E> rhs) {

        // Warning, highly experimental piece of code following.
        // This is to 'reduce' mult references to their corresponding types
        Class<?> rhsType = rhs.getType();
        Class<?> lhsType = lhs.getType();
        if (rhsType != lhsType) {
            if (rhs instanceof ReferenceNode && rhsType.isAssignableFrom(lhsType)) {
                rhs = ((ReferenceNode) rhs).narrow(lhsType);
            } else if (lhs instanceof ReferenceNode && lhsType.isAssignableFrom(rhsType)) {
                lhs = ((ReferenceNode) lhs).narrow(rhsType);
            }
        }
        Class<?> common = ClassUtils.calculateCommonSuperType(lhs.getType(),
                rhs.getType());
        if (Comparable.class.isAssignableFrom(common)) {
            Node<T, E> comparableLhs = createComparableNode(lhs);
            Node<T, E> comparableRhs = createComparableNode(rhs);
            return new RelationalNode<T, E>(operator, comparableLhs,
                    comparableRhs);
        } else {
            StringBuilder builder = new StringBuilder();
            lhs.document(new StringBuilderDocument(builder));
            builder.append(" and ");
            rhs.document(new StringBuilderDocument(builder));
            builder.append(" are incompatible.");
            throw new BindingException(builder.toString());
        }
    }

    public static <T extends Comparable<T>, E> Node<T, E> createComparableNode(
            Node<?, E> node) {
        if (!Comparable.class.isAssignableFrom(ClassUtils
                .getGuaranteedBoxedVersion(node.getType()))) {
            StringBuilder builder = new StringBuilder();
            node.document(new StringBuilderDocument(builder));
            throw new BindingException("Reference " + builder.toString()
                    + " does not resolve to Comparable.");
        } else {
            return ((Node<T, E>) node);
        }
    }

}
