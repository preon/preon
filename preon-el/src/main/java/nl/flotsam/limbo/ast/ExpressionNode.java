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

import nl.flotsam.limbo.Document;
import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Reference;

/**
 * A {@link Node} wrapping around an {@link Expression}. Relevant in cases in
 * which we don't have access to the AST representation of the expression.
 * 
 * @author Wilfred Springer (wis)
 * 
 * @param <T>
 *            The type of value to which this node evaluates.
 * @param <E>
 *            The type of context required to evaluate this node.
 */
public class ExpressionNode<T extends Comparable<T>, E> extends AbstractNode<T, E> {

    /**
     * The expression held by this node.
     */
    private Expression<T, E> expression;

    /**
     * Constructs a new instance.
     * 
     * @param expression
     *            The expression to be represented by this node.
     */
    public ExpressionNode(Expression<T, E> expression) {
        this.expression = expression;
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.flotsam.limbo.ast.Node#eval(java.lang.Object)
     */
    public T eval(E context) {
        return expression.eval(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.flotsam.limbo.ast.Node#gather(java.util.Set)
     */
    public void gather(Set<Reference<E>> references) {
        references.addAll(expression.getReferences());
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.flotsam.limbo.ast.Node#getType()
     */
    public Class<T> getType() {
        return expression.getType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.flotsam.limbo.ast.Node#simplify()
     */
    public Node<T, E> simplify() {
        return new ExpressionNode<T, E>(expression.simplify());
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.flotsam.limbo.Expression#isParameterized()
     */
    public boolean isParameterized() {
        return expression.isParameterized();
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.flotsam.limbo.Descriptive#document(nl.flotsam.limbo.Document)
     */
    public void document(Document target) {
        expression.document(target);
    }

}
