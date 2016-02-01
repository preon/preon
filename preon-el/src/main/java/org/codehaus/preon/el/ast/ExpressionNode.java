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

import org.codehaus.preon.el.Document;
import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.ReferenceContext;

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
     * @see org.codehaus.preon.el.ast.Node#eval(java.lang.Object)
     */
    public T eval(E context) {
        return expression.eval(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.ast.Node#gather(java.util.Set)
     */
    public void gather(Set<Reference<E>> references) {
        references.addAll(expression.getReferences());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.ast.Node#getType()
     */
    public Class<T> getType() {
        return expression.getType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.ast.Node#simplify()
     */
    public Node<T, E> simplify() {
        return new ExpressionNode<T, E>(expression.simplify());
    }

    public Node<T, E> rescope(ReferenceContext<E> context) {
        return new ExpressionNode(expression.rescope(context));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.Expression#isParameterized()
     */
    public boolean isParameterized() {
        return expression.isParameterized();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.Descriptive#document(org.codehaus.preon.el.Document)
     */
    public void document(Document target) {
        expression.document(target);
    }

}
