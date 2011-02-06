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

import org.antlr.runtime.RecognitionException;
import org.codehaus.preon.el.ctx.VariableContext;
import org.codehaus.preon.el.ctx.VariableDefinitions;
import org.codehaus.preon.el.ctx.VariableResolver;
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

    @Test(expected=InvalidExpressionException.class)
    public void testParserProblemArithmetic() {
        EasyMock.expect(defs.getType("a")).andReturn(Integer.class);
        // Previously asserted that "b" would also be checked, but a different
        // ANTLR generated code path negates that now.
        EasyMock.replay(defs);
        try {
            Expression<Integer, VariableResolver> expr = Expressions.createInteger(context,
                    "a - * b");
            fail("Expection parser problem.");
        } finally {
            EasyMock.verify(defs);
        }
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
    
    @Test(expected=BindingException.class)
    public void testAddingStrings() {
        EasyMock.expect(defs.getType("a")).andReturn(String.class).anyTimes();
        EasyMock.replay(resolver, defs);
        try {
            Expression<Object, VariableResolver> expr = Expressions.create(context, "a > 3");
            fail("Expecting BindingException because of incompatible types.");
        } finally {
            EasyMock.verify(resolver, defs);
        }
        
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
