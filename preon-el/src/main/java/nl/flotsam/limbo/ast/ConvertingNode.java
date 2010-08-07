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

import java.util.HashSet;
import java.util.Set;

import nl.flotsam.limbo.Document;
import nl.flotsam.limbo.Reference;
import nl.flotsam.limbo.util.Converter;
import nl.flotsam.limbo.util.Converters;

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
