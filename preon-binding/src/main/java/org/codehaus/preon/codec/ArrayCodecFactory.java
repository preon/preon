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
package org.codehaus.preon.codec;

import org.codehaus.preon.el.BindingException;
import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Expressions;
import org.codehaus.preon.el.InvalidExpressionException;
import org.codehaus.preon.*;
import org.codehaus.preon.annotation.Bound;
import org.codehaus.preon.annotation.BoundList;
import org.codehaus.preon.annotation.BoundObject;
import org.codehaus.preon.annotation.Choices;
import org.codehaus.preon.util.AnnotationWrapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

/**
 * A {@link CodecFactory} that will be triggered by {@link Bound} or {@link BoundList} annotations on arrays. Note that
 * {@link Codec}s created by this class will always read all data eagerly. (Unlike {@link List}s, you cannot implement
 * arrays yourself.)
 *
 * @author Wilfred Springer (wis)
 */
public class ArrayCodecFactory implements CodecFactory {

    /**
     * The {@link CodecFactory} that will be used for constructing the {@link Codecs} to construct elements in the
     * List.
     */
    private CodecFactory factory;

    /**
     * Constructs a new instance, accepting the {@link CodecFactory} creating the {@link Codec Codecs} that will
     * reconstruct elements in the List.
     *
     * @param delegate The {@link CodecFactory} creating the {@link Codec Codecs} that will reconstruct elements in the
     *                 List.
     */
    public ArrayCodecFactory(CodecFactory delegate) {
        this.factory = delegate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.codehaus.preon.CodecFactory#create(java.lang.reflect.AnnotatedElement,
     * java.lang.Class, org.codehaus.preon.ResolverContext)
     */

    @SuppressWarnings("unchecked")
    public <T> Codec<T> create(AnnotatedElement metadata, Class<T> type,
                               ResolverContext context) {
        BoundList settings = null;
        if (metadata != null
                && (settings = metadata.getAnnotation(BoundList.class)) != null
                && type.isArray()
                && settings.size() != null
                && settings.size().length() != 0) {
            Expression<Integer, Resolver> expr = getSizeExpression(settings,
                    context);
            Codec<Object> elementCodec = null;
            if (type.getComponentType().isPrimitive()) {
                elementCodec = (Codec<Object>) factory.create(null, type
                        .getComponentType(), context);
            } else {
                BoundObject objectSettings = getObjectSettings(settings);
                elementCodec = (Codec<Object>) factory.create(
                        new AnnotationWrapper(objectSettings), type
                                .getComponentType(), context);
            }
            return (Codec<T>) new ArrayCodec(expr, elementCodec, type);
        } else {
            return null;
        }

    }

    private BoundObject getObjectSettings(final BoundList settings) {
        return new BoundObject() {

            public Class<?> type() {
                return settings.type();
            }

            public Class<?>[] types() {
                return settings.types();
            }

            public Class<? extends Annotation> annotationType() {
                return BoundObject.class;
            }

            public boolean ommitTypePrefix() {
                return settings.ommitTypePrefix();
            }

            public Choices selectFrom() {
                return settings.selectFrom();
            }

        };
    }

    /**
     * Returns the {@link Expression} that will be evaluated to the array's size.
     *
     * @param listSettings The annotation, holding the expression.
     * @return An {@link Expression} instance, representing the expression.
     */
    private Expression<Integer, Resolver> getSizeExpression(
            BoundList listSettings, ResolverContext context)
            throws CodecConstructionException {
        try {
            return Expressions.createInteger(context, listSettings.size());
        } catch (InvalidExpressionException ece) {
            throw new CodecConstructionException(ece);
        } catch (BindingException be) {
            throw new CodecConstructionException(be);
        }
    }
}
