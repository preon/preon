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
                    nl.flotsam.limbo.Document target) {
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
                    nl.flotsam.limbo.Document target) {
                lhs.document(target);
                target.text(" or ");
                rhs.document(target);
            }
        };

        abstract <E> boolean holds(E context, Node<Boolean, E> lhs, Node<Boolean, E> rhs);

        abstract <E> void document(Node<Boolean, E> lhs, Node<Boolean, E> rhs,
                nl.flotsam.limbo.Document target);

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
     * @see nl.flotsam.limbo.ast.Node#eval(java.lang.Object)
     */
    public Boolean eval(E context) {
        return operator.holds(context, lhs, rhs);
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

}
