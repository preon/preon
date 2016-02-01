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

import java.util.HashSet;
import java.util.Set;

import org.codehaus.preon.el.Document;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.ReferenceContext;
import org.codehaus.preon.el.util.Converter;
import org.codehaus.preon.el.util.Converters;

/**
 * A {@link Node} with the ability to convert to other types of {@link Node}s.
 * Use the static {@link #tryConversion(Node, Class)} and
 * {@link #tryConversionToIntegerNode(Node, Class)} to convert Nodes in some
 * other type of node.
 * 
 * @author Wilfred Springer (wis)
 * 
 * @param <T>
 *            The target type of node.
 * @param <E>
 *            The type of context.
 * @param <S>
 *            The source type of node.
 */
public class ConvertingNode<T extends Comparable<T>, E, S> implements Node<T, E> {

    /**
     * The source {@link Node}.
     */
    private Node<S, E> source;

    /**
     * The {@link Converter} used to convert types from S to N.
     */
    private Converter<S, T> converter;

    /**
     * Constructs a new instance, accepting the {@link Converter} to be applied
     * and the source {@link Node}.
     * 
     * @param converter The {@link Converter}.
     * @param source The source {@link Node}.
     */
    public ConvertingNode(Converter<S, T> converter, Node<S, E> source) {
        this.source = source;
        this.converter = converter;
    }

    public int compareTo(E context, Node<T, E> other) {
        return this.eval(context).compareTo(other.eval(context));
    }

    public T eval(E context) {
        return converter.convert(source.eval(context));
    }

    public void gather(Set<Reference<E>> references) {
        source.gather(references);
    }

    public Class<T> getType() {
        return converter.getTargetType();
    }

    public Node<T, E> simplify() {
        return new ConvertingNode<T, E, S>(converter, source.simplify());
    }

    public Node<T, E> rescope(ReferenceContext<E> context) {
        return this;
    }

    public boolean isConstantFor(ReferenceContext<E> context) {
        return source.isConstantFor(context);
    }

    public Set<Reference<E>> getReferences() {
        Set<Reference<E>> references = new HashSet<Reference<E>>();
        gather(references);
        return references;
    }

    public boolean isParameterized() {
        return source.isParameterized();
    }

    public void document(Document target) {
        source.document(target);
    }

    public static <T extends Comparable<T>, E, S> Node<?, E> tryConversion(Node<S, E> source, Class<T> targetType) {
        Converter<S, T> converter = Converters.get(source.getType(), targetType);
        if (converter != null) {
            return new ConvertingNode<T, E, S>(converter, source);
        } else {
            return source;
        }
    }

    public static <T, E, S> Node<?, E> tryConversionToIntegerNode(Node<S, E> source) {
        Class<?> type = source.getType();
        if (Byte.class == type || Short.class == type || Long.class == type) {
            return tryConversion(source, Integer.class);
        } else {
            return source;
        }
    }

}
