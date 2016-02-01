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
package org.codehaus.preon.binding;

import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import org.codehaus.preon.*;
import org.codehaus.preon.annotation.If;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.descriptor.Documenters;
import org.codehaus.preon.el.*;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * A {@link BindingFactory} that wraps another {@link BindingFactory}, creating {@link Binding Bindings} that are bound
 * conditionally, depending on the conditions of the {@link If} metadata defined for the Fields that are bound.
 *
 * @author Wilfred Springer
 */
public class ConditionalBindingFactory implements BindingFactory {

    /**
     * The {@link BindingFactory} creating the {@link Binding}s that will be wrapped with {@link ConditionalBinding}
     * instances.
     */
    private BindingFactory decorated;

    /**
     * Constructs a new instance, accepting the {@link BindingFactory} of the {@link Binding Bindings} to decorate.
     *
     * @param decorated The {@link BindingFactory} of the {@link Binding Bindings} to decorate.
     */
    public ConditionalBindingFactory(BindingFactory decorated) {
        this.decorated = decorated;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.binding.BindingFactory#create(java.lang.reflect.
     * AnnotatedElement, java.lang.reflect.Field, org.codehaus.preon.Codec,
     * org.codehaus.preon.ResolverContext)
     */

    public Binding create(AnnotatedElement metadata, Field field, Codec<?> codec,
                          ResolverContext context, Documenter<ParaContents<?>> containerReference) {
        If condition = metadata.getAnnotation(If.class);
        if (condition != null) {
            Expression<Boolean, Resolver> expr = null;
            String value = condition.value();
            try {
                expr = Expressions.createBoolean(context, value);
                return new ConditionalBinding(expr, decorated.create(metadata, field, codec, context, containerReference));
            } catch (InvalidExpressionException e) {
                System.err.println("All wrong");
                throw e;
            }
        } else {
            return decorated.create(metadata, field, codec, context, containerReference);
        }
    }

    private static class ConditionalBinding implements Binding {

        private Expression<Boolean, Resolver> expr;

        private Binding binding;

        public ConditionalBinding(Expression<Boolean, Resolver> expr, Binding binding) {
            this.expr = expr;
            this.binding = binding;
        }

        public void load(Object object, BitBuffer buffer, Resolver resolver, Builder builder)
                throws DecodingException {
            if (expr.eval(resolver)) {
                binding.load(object, buffer, resolver, builder);
            }
        }

        public <T, V extends ParaContents<T>> V describe(final V contents) {
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
            return new ConditionalValue(expr, binding.getSize());
        }

        public String getId() {
            return binding.getId();
        }

        public Class<?> getType() {
            return binding.getType();
        }

        public void save(Object value, BitChannel channel, Resolver resolver) throws IOException {
            if (expr.eval(resolver)) {
                binding.save(value, channel, resolver);
            }
        }

        public <V extends SimpleContents<?>> V describe(V contents) {
            binding.describe(contents);
            contents.para().text("Only if ").document(Documenters.forExpression(expr)).text(".").end();
            return contents;
        }

    }

    private static class ConditionalValue implements Expression<Integer, Resolver> {

        private Expression<Boolean, Resolver> condition;

        private Expression<Integer, Resolver> expr;

        public ConditionalValue(Expression<Boolean, Resolver> condition, Expression<Integer, Resolver> expr) {
            this.condition = condition;
            this.expr = expr;
        }

        public Integer eval(Resolver resolver) throws BindingException {
            if (condition.eval(resolver)) {
                return expr.eval(resolver);
            } else {
                return 0;
            }
        }

        public Set<Reference<Resolver>> getReferences() {
            return expr.getReferences();
        }

        public Class<Integer> getType() {
            return Integer.class;
        }

        public boolean isParameterized() {
            return condition.isParameterized() || expr.isParameterized();
        }

        public Expression<Integer, Resolver> simplify() {
            return new ConditionalValue(condition.simplify(), expr.simplify());
        }

        public boolean isConstantFor(ReferenceContext<Resolver> context) {
            return condition.isConstantFor(context) && expr.isConstantFor(context);
        }

        public Expression<Integer, Resolver> rescope(ReferenceContext<Resolver> context) {
            return new ConditionalValue(condition.rescope(context), expr.rescope(context));
        }

        public void document(Document document) {
            expr.document(document);
            document.text(" if ");
            condition.document(document);
            document.text(" or else 0");
        }


    }

}
