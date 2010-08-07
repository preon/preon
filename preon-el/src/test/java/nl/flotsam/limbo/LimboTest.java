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

package nl.flotsam.limbo;

import nl.flotsam.limbo.ctx.VariableContext;
import nl.flotsam.limbo.ctx.VariableDefinitions;
import nl.flotsam.limbo.ctx.VariableResolver;
import org.antlr.runtime.RecognitionException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LimboTest {

    private VariableResolver resolver;

    private VariableDefinitions defs;

    private VariableContext context;

    @Before
    public void setUp() throws Exception {
        resolver = EasyMock.createMock(VariableResolver.class);
        defs = EasyMock.createMock(VariableDefinitions.class);
        context = new VariableContext(defs);
    }

    @Test
    public void testUnderscores() {
        EasyMock.expect(resolver.get("FOO_BAR")).andReturn(12).anyTimes();
        EasyMock.expect(defs.getType("FOO_BAR")).andReturn(Integer.class).anyTimes();
        EasyMock.replay(resolver, defs);
        assertEquals(12, arithmetic(context, resolver, "FOO_BAR"));
        EasyMock.verify(resolver, defs);
    }

    @Test
    public void testBinaryNotation() throws RecognitionException, InvalidExpressionException {
        assertEquals(3, arithmetic(context, resolver, "0b11"));
        assertEquals(5, arithmetic(context, resolver, "0b101"));
    }

    @Test
    public void testHexidecimalNotation() throws RecognitionException, InvalidExpressionException {
        assertEquals(255, arithmetic(context, resolver, "0xff"));
        assertEquals(16, arithmetic(context, resolver, "0x10"));
    }

    @Test
    public void testPow() throws RecognitionException, InvalidExpressionException {
        EasyMock.expect(resolver.get("a")).andReturn(Integer.valueOf(3)).anyTimes();
        EasyMock.expect(defs.getType("a")).andReturn(Integer.class).anyTimes();
        EasyMock.replay(resolver, defs);
        assertEquals(8, arithmetic(context, resolver, "2^a"));
        assertEquals(9, arithmetic(context, resolver, "a^2"));
        assertEquals(27, arithmetic(context, resolver, "a^a"));
        assertEquals(9, arithmetic(context, resolver, "a^(a-1)"));
        assertEquals(26, arithmetic(context, resolver, "a ^ a -1"));
        EasyMock.verify(resolver, defs);
    }

    @Test
    public void testSimpleCondition() throws RecognitionException, InvalidExpressionException {
        assertTrue(condition(context, resolver, "3 > 2"));
    }

    @Test
    public void testComplexConditions() throws RecognitionException, InvalidExpressionException {
        assertTrue(condition(context, resolver, "3 > 2 && 2 > 1"));
        assertFalse(condition(context, resolver, "3 > 2 && 1 > 2"));
        assertTrue(condition(context, resolver, "3 > 2 || 1 > 2"));
        assertTrue(condition(context, resolver, "1 > 2 || 2 > 3 || 4 > 3"));
        assertFalse(condition(context, resolver, "1 > 2 || (4 > 3 && 2 < 2)"));
    }

    @Test
    public void testDivision() throws RecognitionException, InvalidExpressionException {
        assertEquals(2, arithmetic(context, resolver, "4/2"));
        assertEquals(2, arithmetic(context, resolver, "5/2"));
        assertEquals(3, arithmetic(context, resolver, "6/2"));
    }

    @Test
    public void testSimpleAddition() throws RecognitionException, InvalidExpressionException {
        assertEquals(8, arithmetic(context, resolver, "5 + 3"));
    }

    @Test
    public void testOperatorPrecedence() throws RecognitionException, InvalidExpressionException {
        assertEquals(arithmetic(context, resolver, "5 + 3 * 4"), arithmetic(context, resolver,
                "3 * 4 + 5"));
    }

    @Test
    public void testParserProblemArithmetic() {
        EasyMock.expect(defs.getType("a")).andReturn(Integer.class);
        EasyMock.expect(defs.getType("b")).andReturn(Integer.class);
        EasyMock.replay(defs);
        try {
            Expression<Integer, VariableResolver> expr = Expressions.createInteger(context,
                    "a - * b");
            fail("Expection parser problem.");
        } catch (InvalidExpressionException e) {
            // Ah, this is what we expected
        }
        EasyMock.verify(defs);
    }

    @Test
    public void testWithSimpleReference() throws RecognitionException, InvalidExpressionException {
        EasyMock.expect(resolver.get("a")).andReturn(Integer.valueOf(3)).anyTimes();
        EasyMock.expect(defs.getType("a")).andReturn(Integer.class).anyTimes();
        EasyMock.replay(resolver, defs);
        assertEquals(8, arithmetic(context, resolver, "5 + a"));
        EasyMock.verify(resolver, defs);
    }

    @Test
    public void testWithComplexReference() throws RecognitionException, InvalidExpressionException {
        Sample test = new Sample();
        test.b = new Sample();
        test.b.c = 3;
        EasyMock.expect(resolver.get("a")).andReturn(test);
        EasyMock.expect(defs.getType("a")).andReturn(Sample.class);
        EasyMock.replay(resolver, defs);
        assertEquals(8, arithmetic(context, resolver, "5 + a.b.c"));
        EasyMock.verify(resolver, defs);
    }

    @Test
    public void testIntegerComparison() {
        Expression<Boolean, VariableResolver> expr = Expressions.createBoolean(context, "5 == 5");
        assertTrue(expr.eval(resolver));
    }

    @Test
    public void testStringComparison() {
        Expression<Boolean, VariableResolver> expr = Expressions.createBoolean(context,
                "'abc' == 'abc'");
        assertTrue(expr.eval(resolver));
        assertFalse(Expressions.createBoolean(context, "'abc' == 'cba'").eval(resolver));
    }

    @Test
    public void testExpressionsCreate() {
        Expression<Object, VariableResolver> expr = Expressions.create(context, "23");
        assertEquals(23, expr.eval(resolver));
    }

    @Test
    public void testStringLiterals() {
        Expression<Object, VariableResolver> expr = Expressions.create(context, "'abc'");
        assertEquals("abc", expr.eval(resolver));
    }
    
    @Test
    public void testStringReferencesResolution() {
        EasyMock.expect(resolver.get("a")).andReturn("Whatever");
        EasyMock.replay(resolver);
        Expression<Object, VariableResolver> expr = Expressions.create(context, "a");
        assertEquals("Whatever", expr.eval(resolver));
        EasyMock.verify(resolver);
    }
    
    @Test
    public void testAddingStrings() {
        EasyMock.expect(defs.getType("a")).andReturn(String.class).anyTimes();
        EasyMock.replay(resolver, defs);
        try {
            Expression<Object, VariableResolver> expr = Expressions.create(context, "a > 3");
            fail("Expecting BindingException because of incompatible types.");
        } catch (BindingException e) {
            // Allright!
        }
        EasyMock.verify(resolver, defs);
    }

    @Test
    public void testComparingEnums() {
    	EasyMock.expect(defs.getType("a")).andReturn(Direction.class).anyTimes();
    	EasyMock.expect(defs.getType("b")).andReturn(Direction.class).anyTimes();
    	EasyMock.expect(resolver.get("a")).andReturn(Direction.LEFT).anyTimes();
    	EasyMock.expect(resolver.get("b")).andReturn(Direction.RIGHT).anyTimes();
    	EasyMock.replay(defs, resolver);
    	assertTrue(condition(context, resolver, "a == a"));
    	assertFalse(condition(context, resolver, "a == b"));
    	assertTrue(condition(context, resolver, "b == b"));
    }
    
    private <E> int arithmetic(ReferenceContext<VariableResolver> context,
            VariableResolver resolver, String exprString) throws InvalidExpressionException {
        Expression<Integer, VariableResolver> expr = Expressions.createInteger(context, exprString);
        return expr.eval(resolver);
    }

    private <E> boolean condition(ReferenceContext<VariableResolver> context,
            VariableResolver resolver, String exprString) throws InvalidExpressionException {
        Expression<Boolean, VariableResolver> expr = Expressions.createBoolean(context, exprString);
        return expr.eval(resolver);
    }

    private static class Sample {

        Sample b;

        int c;

    }
    
    public static enum Direction {
    	
    	LEFT, 
    	RIGHT;
    	
    }

}
