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
