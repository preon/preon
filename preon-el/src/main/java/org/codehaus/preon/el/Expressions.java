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
import org.codehaus.preon.el.ast.ExpressionNode;
import org.codehaus.preon.el.ast.IntegerNode;
import org.codehaus.preon.el.ast.Node;
import org.codehaus.preon.el.ast.ArithmeticNode.Operator;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

/**
 * A convenience class for creating Expressions.
 * 
 * @author Wilfred Springer
 * 
 */
public class Expressions {

    /**
     * Creates an {@link Expression} from the Limbo expression passed in. (Will
     * fail if the expression passed in does not return a boolean value.)
     * 
     * @param <E>
     *            The type of environment that will be passed in when evaluating
     *            the expression.
     * @param context
     *            The context for this expression.
     * @param expr
     *            The Limbo expression.
     * @return An {@link Expression} object that can be evaluated against
     *         instances of <code>E</code>.
     * @throws InvalidExpressionException
     *             If the expression cannot be created. (Either because
     *             references can not be bound to the context, or because the
     *             parser fails to parse the Limbo expression.)
     */
    public static <E> Expression<Boolean, E> createBoolean(ReferenceContext<E> context, String expr)
            throws InvalidExpressionException {
        return condition(context, expr);
    }

    public static <E> Expression<Object, E> create(ReferenceContext<E> context, String expr) {
        return any(context, expr);
    }

    /**
     * Creates an {@link Expression} from the Limbo expression passed in. (Will
     * fail if the expression passed in does not return a integer value.)
     * 
     * @param <E>
     *            The type of environment that will be passed in when evaluating
     *            the expression.
     * @param context
     *            The context for this expression.
     * @param expr
     *            The Limbo expression.
     * @return An {@link Expression} object that can be evaluated against
     *         instances of <code>E</code>.
     * @throws InvalidExpressionException
     *             If the expression cannot be created. (Either because
     *             references can not be bound to the context, or because the
     *             parser fails to parse the Limbo expression.)
     */
    public static <E> Expression<Integer, E> createInteger(ReferenceContext<E> context, String expr)
            throws InvalidExpressionException {
        return arithmetic(context, expr);
    }

    /**
     * Creates an {@link Expression} wrapping around a number.
     * 
     * @param <E>
     *            The type of context to which this expression should be
     *            applied.
     * @param value
     *            The value to be wrapped.
     * @param cl
     *            The type of context expected by the Expression.
     * @return An {@link Expression} evaluating to <code>value</code>.
     */
    public static <E> Expression<Integer, E> createInteger(int value, Class<E> cl) {
        return new IntegerNode<E>(value);
    }

    @SuppressWarnings("unchecked")
    private static <E> Node<Integer, E> arithmetic(ReferenceContext<E> context, String expr)
            throws InvalidExpressionException {
        try {
            return (Node<Integer, E>) buildWalker(context, expr).vexpr();
        } catch (RecognitionException re) {
            throw new InvalidExpressionException(re);
        }
    }

    @SuppressWarnings("unchecked")
    private static <E> Node<Boolean, E> condition(ReferenceContext<E> context, String expr)
            throws InvalidExpressionException {
        try {
            return (Node<Boolean, E>) buildWalker(context, expr).zexpr();
        } catch (RecognitionException re) {
            throw new InvalidExpressionException(re);
        }
    }

    @SuppressWarnings("unchecked")
    private static <E> Node<Object, E> any(ReferenceContext<E> context, String expr)
            throws InvalidExpressionException {
        try {
            return (Node<Object, E>) buildWalker(context, expr).fexpr();
        } catch (RecognitionException re) {
            throw new InvalidExpressionException(re);
        }
    }

    private static <E> LimboWalker buildWalker(ReferenceContext<E> context, String expr)
            throws RecognitionException {
        ANTLRStringStream in = new ANTLRStringStream(expr);
        LimboLexer lexer = new LimboLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LimboParser parser = new LimboParser(tokens);
        CommonTree tree = (CommonTree) parser.condExpression().getTree();
        CommonTreeNodeStream nodes = new CommonTreeNodeStream(tree);
        nodes.setTokenStream(tokens);
        LimboWalker walker = new org.codehaus.preon.el.LimboWalker(nodes, new ImplicitsContext(context));
        return walker;
    }

    public static <C> ContextualizedExpressionBuilder<C> from(Class<C> contextType) {
        return new ContextualizedExpressionBuilderImpl<C>(contextType);
    }

    public static <C> BoundExpressionBuilder<C> from(ReferenceContext<C> contextType) {
        return new BoundExpressionBuilderImpl<C>(contextType);
    }

    public interface ContextualizedExpressionBuilder<C> extends BoundExpressionBuilder<C> {

        BoundExpressionBuilder<C> using(Binding binding);

    }

    /**
     * An expression builder that has already been tied to a
     * {@link ReferenceContext}.
     * 
     * @author Wilfred Springer
     * 
     * @param <C>
     *            The type of context to which the expressions will be bound.
     */
    public interface BoundExpressionBuilder<C> {

        /**
         * Constructs an expression that is supposed to evaluate to a boolean.
         * 
         * @param expr
         *            The Limbo expression, as text.
         * @return An {@link Expression} object, encapsulating the expression
         *         passed in as a String.
         * @throws BindingException
         *             If the expression contains references that cannot be
         *             bound to the {@link ReferenceContext}.
         * @throws InvalidExpressionException
         *             If the expression is not a valid Limbo expression.
         */
        Expression<Boolean, C> toBoolean(String expr) throws BindingException,
                InvalidExpressionException;

        /**
         * Constructs an expression that is supposed to evaluate to a integer.
         * 
         * @param expr
         *            The Limbo expression, as text.
         * @return An {@link Expression} object, encapsulating the expression
         *         passed in as a String.
         * @throws BindingException
         *             If the expression contains references that cannot be
         *             bound to the {@link ReferenceContext}.
         * @throws InvalidExpressionException
         *             If the expression is not a valid Limbo expression.
         */
        Expression<Integer, C> toInteger(String expr) throws BindingException,
                InvalidExpressionException;

    }

    private static class BoundExpressionBuilderImpl<C> implements BoundExpressionBuilder<C> {

        private ReferenceContext<C> context;

        public BoundExpressionBuilderImpl(ReferenceContext<C> context) {
            this.context = context;
        }

        public Expression<Boolean, C> toBoolean(String expr) throws BindingException,
                InvalidExpressionException {
            return createBoolean(context, expr);
        }

        public Expression<Integer, C> toInteger(String expr) throws BindingException,
                InvalidExpressionException {
            return createInteger(context, expr);
        }

    }

    private static class ContextualizedExpressionBuilderImpl<C> implements
            ContextualizedExpressionBuilder<C> {

        private static Binding DEFAULT = Bindings.EarlyBinding;

        private Class<?> type;

        public ContextualizedExpressionBuilderImpl(Class<?> type) {
            this.type = type;
        }

        @SuppressWarnings("unchecked")
        public BoundExpressionBuilder<C> using(Binding binding) {
            return new BoundExpressionBuilderImpl(DEFAULT.create(type));
        }

        @SuppressWarnings("unchecked")
        public Expression<Boolean, C> toBoolean(String expr) throws BindingException,
                InvalidExpressionException {
            return (Expression<Boolean, C>) createBoolean(DEFAULT.create(type), expr);
        }

        @SuppressWarnings("unchecked")
        public Expression<Integer, C> toInteger(String expr) throws BindingException,
                InvalidExpressionException {
            return (Expression<Integer, C>) createInteger(DEFAULT.create(type), expr);
        }

    }

    /**
     * Returns a new expression, representing the multiplication of the two
     * {@link Expression}s passed in.
     * 
     * @param <C>
     *            The type of context of the expressions.
     * @param first
     *            The first expression.
     * @param second
     *            The second expression.
     * @return A new {@link Expression}, representing the multiplication of the
     *         two arguments passed in. Returns <code>null</code> if either one
     *         of the arguments is <code>null</code>.
     */
    public static <C> Expression<Integer, C> multiply(Expression<Integer, C> first,
            Expression<Integer, C> second) {
        return combine(Operator.mult, first, second);
    }

    /**
     * Returns a new expression, representing the sum of the two
     * {@link Expression}s passed in.
     * 
     * @param <C>
     *            The type of context of the expressions.
     * @param first
     *            The first expression.
     * @param second
     *            The second expression.
     * @return A new {@link Expression}, representing the sum of the two
     *         arguments passed in. Returns <code>null</code> if either one of
     *         the arguments is <code>null</code>.
     */
    public static <C> Expression<Integer, C> add(Expression<Integer, C> first,
            Expression<Integer, C> second) {
        return combine(Operator.plus, first, second);
    }

    /**
     * Returns a new {@link Node}.
     * 
     * @param <C>
     *            The type of context to be passed in into the Node in order to
     *            calculate a result.
     * @param operator
     *            The operator to be applied.
     * @param first
     *            The first operator.
     * @param second
     *            The second operator.
     * @return A new {@link Node}.
     */
    private static <C> Node<Integer, C> combine(Operator operator, Expression<Integer, C> first,
            Expression<Integer, C> second) {
        if (first == null || second == null) {
            return null;
        }
        Node<Integer, C> firstNode;
        Node<Integer, C> secondNode;
        if (first instanceof Node) {
            firstNode = (Node<Integer, C>) first;
        } else {
            firstNode = new ExpressionNode<Integer, C>(first);
        }
        if (second instanceof Node) {
            secondNode = (Node<Integer, C>) second;
        } else {
            secondNode = new ExpressionNode<Integer, C>(second);
        }
        return new ArithmeticNode<C>(operator, firstNode, secondNode);
    }

}
