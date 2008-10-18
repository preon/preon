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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

import junit.framework.TestCase;
import nl.flotsam.limbo.BindingException;
import nl.flotsam.limbo.Document;
import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Reference;
import nl.flotsam.limbo.ReferenceContext;
import nl.flotsam.preon.Builder;
import nl.flotsam.preon.Codec;
import nl.flotsam.preon.CodecException;
import nl.flotsam.preon.DecodingException;
import nl.flotsam.preon.Resolver;
import nl.flotsam.preon.ResolverContext;
import nl.flotsam.preon.annotation.If;
import nl.flotsam.preon.binding.Binding;
import nl.flotsam.preon.binding.BindingFactory;
import nl.flotsam.preon.binding.ConditionalBindingFactory;
import nl.flotsam.preon.buffer.BitBuffer;


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
        testConditionalLoad("a - - b", 2, 3, false, true);
    }

    public void testConditionalLoad(String expr, int a, int b, boolean bindingAction,
            boolean compilationFailure) throws DecodingException {
        Test test = new Test();
        test.value = "whatever";
        expect(metadata.getAnnotation(If.class)).andReturn(condition);
        expect(condition.value()).andReturn(expr);
        expect(decorated.create(metadata, field, codec, context)).andReturn(binding);
        if (!compilationFailure) {
            expect(context.selectAttribute("a")).andReturn(new SimpleIntegerReference("a")); // Reference not used
            expect(context.selectAttribute("b")).andReturn(new SimpleIntegerReference("b")); // Reference not used
            expect(resolver.get("a")).andReturn(Integer.valueOf(a)).anyTimes();
            expect(resolver.get("b")).andReturn(Integer.valueOf(b)).anyTimes();
        }
        if (bindingAction) {
            expect(binding.getSize(resolver)).andReturn(6);
            binding.load(test, buffer, resolver, builder);
        }
        replay(decorated, buffer, resolver, metadata, codec, condition, binding, builder, context);
        Binding conditionalBinding = factory.create(metadata, field, codec, context);
        if (bindingAction) {
            assertEquals(6, conditionalBinding.getSize(resolver));
        } else {
            assertEquals(0, conditionalBinding.getSize(resolver));
        }
        conditionalBinding.load(test, buffer, resolver, builder);
        verify(decorated, buffer, resolver, metadata, codec, condition, binding, builder, context);
        reset(decorated, buffer, resolver, metadata, codec, condition, binding, builder, context);
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

    }

}
