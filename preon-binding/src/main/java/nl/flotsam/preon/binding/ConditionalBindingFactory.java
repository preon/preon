/*
 * Copyright (C) 2008 Wilfred Springer
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

package nl.flotsam.preon.binding;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Set;

import nl.flotsam.pecia.ParaContents;
import nl.flotsam.preon.Builder;
import nl.flotsam.preon.Codec;
import nl.flotsam.preon.DecodingException;
import nl.flotsam.preon.Resolver;
import nl.flotsam.preon.ResolverContext;
import nl.flotsam.preon.annotation.If;
import nl.flotsam.preon.buffer.BitBuffer;

import nl.flotsam.limbo.BindingException;
import nl.flotsam.limbo.Document;
import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Expressions;
import nl.flotsam.limbo.InvalidExpressionException;
import nl.flotsam.limbo.Reference;


import edu.emory.mathcs.backport.java.util.Collections;

/**
 * A {@link BindingFactory} that wraps another {@link BindingFactory}, creating
 * {@link Binding Bindings} that are bound conditionally, depending on the
 * conditions of the {@link If} metadata defined for the Fields that are bound.
 * 
 * @author Wilfred Springer
 * 
 */
public class ConditionalBindingFactory implements BindingFactory {

    /**
     * The {@link BindingFactory} creating the {@link Bindings} that will be
     * wrapped with {@link ConditionalBinding} instances.
     */
    private BindingFactory decorated;

    /**
     * Constructs a new instance, accepting the {@link BindingFactory} of the
     * {@link Binding Bindings} to decorate.
     * 
     * @param decorated
     *            The {@link BindingFactory} of the {@link Binding Bindings} to
     *            decorate.
     */
    public ConditionalBindingFactory(BindingFactory decorated) {
        this.decorated = decorated;
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.flotsam.preon.binding.BindingFactory#create(java.lang.reflect.
     * AnnotatedElement, java.lang.reflect.Field, nl.flotsam.preon.Codec,
     * nl.flotsam.preon.ResolverContext)
     */
    public Binding create(AnnotatedElement metadata, Field field, Codec<?> codec,
            ResolverContext context) {
        If condition = metadata.getAnnotation(If.class);
        if (condition != null) {
            Expression<Boolean, Resolver> expr = null;
            String value = condition.value();
            try {
                expr = Expressions.createBoolean(context, value);
            } catch (InvalidExpressionException iee) {
                expr = new Expression<Boolean, Resolver>() {

                    public Boolean eval(Resolver resolver) throws BindingException {
                        return false;
                    }

                    public Set<Reference<Resolver>> getReferences() {
                        return Collections.emptySet();
                    }

                    public void document(Document target) {
                        target.text("[invalid expression]");
                    }

                    public Expression<Boolean, Resolver> getSimplified() {
                        return this;
                    }

                    public boolean isParameterized() {
                        return false;
                    }

                    public Class<Boolean> getType() {
                        return Boolean.class;
                    }

                    public Expression<Boolean, Resolver> simplify() {
                        return this;
                    }

                };
            }
            return new ConditionalBinding(expr, decorated.create(metadata, field, codec, context));
        } else {
            return decorated.create(metadata, field, codec, context);
        }
    }

    private static class ConditionalBinding implements Binding {

        private Expression<Boolean, Resolver> expr;

        private Binding binding;

        public ConditionalBinding(Expression<Boolean, Resolver> expr, Binding binding) {
            this.expr = expr;
            this.binding = binding;
        }

        public int getSize(Resolver resolver) {
            if (expr.eval(resolver)) {
                return binding.getSize(resolver);
            } else {
                return 0;
            }
        }

        public void load(Object object, BitBuffer buffer, Resolver resolver, Builder builder)
                throws DecodingException {
            if (expr.eval(resolver)) {
                binding.load(object, buffer, resolver, builder);
            }
        }

        public <T, V extends ParaContents<T>> V describe(final V contents) {
            binding.describe(contents);
            contents.text(" Only if ");
            expr.document(new Document() {

                public Document detail(String summary) {
                    // TODO Auto-generated method stub
                    return null;
                }

                public void link(Object object, String text) {
                    contents.link(object, text);
                }

                public void text(String text) {
                    contents.text(text);
                }

            });
            contents.text(".");
            return contents;
        }

        public String getSizeAsText() {
            return binding.getSizeAsText();
        }

        public Class<?>[] getTypes() {
            return binding.getTypes();
        }

        public Object get(Object context) throws IllegalArgumentException, IllegalAccessException {
            return binding.get(context);
        }

        public String getName() {
            return binding.getName();
        }

        public <T, V extends ParaContents<T>> V writeReference(V contents) {
            return binding.writeReference(contents);
        }

        public Expression<Integer, Resolver> getSize() {
            // We really cannot guarantee that this we *will* read all of the bytes.
            return null;
        }

        public String getId() {
            return binding.getId();
        }

    }

}
