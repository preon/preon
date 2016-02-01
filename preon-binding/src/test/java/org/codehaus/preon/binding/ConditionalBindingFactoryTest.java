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

import junit.framework.TestCase;
import org.codehaus.preon.*;
import org.codehaus.preon.annotation.If;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.el.*;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

import static org.easymock.EasyMock.*;


public class ConditionalBindingFactoryTest extends TestCase {

    private BindingFactory decorated;

    private ConditionalBindingFactory factory;

    private BitBuffer buffer;

    private Resolver resolver;

    private Field field;

    private Codec<?> codec;

    private AnnotatedElement metadata;

    private If condition;

    private Binding binding;

    private Builder builder;

    private ResolverContext context;

    private Expression<Integer, Resolver> sizeExpr;

    public void setUp() {
        binding = createMock(Binding.class);
        decorated = createMock(BindingFactory.class);
        buffer = createMock(BitBuffer.class);
        resolver = createMock(Resolver.class);
        field = getTestField();
        codec = createMock(Codec.class);
        condition = createMock(If.class);
        metadata = createMock(AnnotatedElement.class);
        factory = new ConditionalBindingFactory(decorated);
        builder = createMock(Builder.class);
        context = createMock(ResolverContext.class);
        sizeExpr = createMock(Expression.class);
    }

    private static Field getTestField() {
        try {
            return Test.class.getDeclaredField("value");
        } catch (SecurityException e) {
        } catch (NoSuchFieldException e) {
        }
        return null;
    }

    public void testConditionals() throws CodecException {
        testConditionalLoad("a > b", 3, 2, true, false);
        testConditionalLoad("a > b", 1, 1, false, false);
        testConditionalLoad("a > b", 2, 3, false, false);
    }

    public void testConditionalLoad(String expr, int a, int b, boolean bindingAction,
                                    boolean compilationFailure) throws DecodingException {
        Test test = new Test();
        test.value = "whatever";
        expect(metadata.getAnnotation(If.class)).andReturn(condition);
        expect(condition.value()).andReturn(expr);
        expect(decorated.create(metadata, field, codec, context, null)).andReturn(binding);
        if (!compilationFailure) {
            expect(context.selectAttribute("a")).andReturn(new SimpleIntegerReference("a")); // Reference not used
            expect(context.selectAttribute("b")).andReturn(new SimpleIntegerReference("b")); // Reference not used
            expect(resolver.get("a")).andReturn(Integer.valueOf(a)).anyTimes();
            expect(resolver.get("b")).andReturn(Integer.valueOf(b)).anyTimes();
        }
        if (bindingAction) {
            expect(binding.getSize()).andReturn(sizeExpr);
            expect(sizeExpr.eval(resolver)).andReturn(6);
            binding.load(test, buffer, resolver, builder);
        } else {
            expect(binding.getSize()).andReturn(sizeExpr);
        }
        replay(decorated, buffer, resolver, metadata, codec, condition, binding, builder, context, sizeExpr);
        Binding conditionalBinding = factory.create(metadata, field, codec, context, null);
        if (bindingAction) {
            assertEquals(Integer.valueOf(6), conditionalBinding.getSize().eval(resolver));
        } else {
            assertEquals(Integer.valueOf(0), conditionalBinding.getSize().eval(resolver));
        }
        conditionalBinding.load(test, buffer, resolver, builder);
        verify(decorated, buffer, resolver, metadata, codec, condition, binding, builder, context, sizeExpr);
        reset(decorated, buffer, resolver, metadata, codec, condition, binding, builder, context, sizeExpr);
    }

    public static class Test {

        public String value;

    }

    private static class SimpleIntegerReference implements Reference<Resolver> {

        private String name;

        public SimpleIntegerReference(String name) {
            this.name = name;
        }

        public ReferenceContext<Resolver> getReferenceContext() {
            return null;
        }

        public boolean isAssignableTo(Class<?> type) {
            // TODO Auto-generated method stub
            return false;
        }

        public Object resolve(Resolver context) {
            return context.get(name);
        }

        public Reference<Resolver> selectAttribute(String name) {
            throw new BindingException("Attribute selection not allowed.");
        }

        public Reference<Resolver> selectItem(String index) {
            throw new BindingException("Item selection not allowed.");
        }

        public Reference<Resolver> selectItem(Expression<Integer, Resolver> index) {
            throw new BindingException("Item selection not allowed.");
        }

        public void document(Document target) {
            // No way
        }

        public Class<?> getType() {
            return Integer.class;
        }

        public Reference<Resolver> narrow(Class<?> type) {
            if (type.isAssignableFrom(Integer.class)) {
                return this;
            } else {
                return null;
            }
        }

        public boolean isBasedOn(ReferenceContext<Resolver> resolverReferenceContext) {
            return false;
        }

        public Reference<Resolver> rescope(ReferenceContext<Resolver> resolverReferenceContext) {
            return this;
        }

    }

}
