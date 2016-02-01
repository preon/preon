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
package org.codehaus.preon.el;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Test;
import static org.junit.Assert.*;

public class NarrowingTest {

    private ReferenceContext<Object> context = createMock(ReferenceContext.class);
    private Reference<Object> reference1 = createMock(Reference.class);
    private Reference<Object> reference2 = createMock(Reference.class);

    @Test
    public void testAssignable() {
        assertTrue(Number.class.isAssignableFrom(Long.class));
        assertFalse(Long.class.isAssignableFrom(Number.class));
    }

    @Test(expected = BindingException.class)
    public void testNoNarrowingPossible() {
        expect(context.selectAttribute("a")).andReturn(reference1);
        expect(reference1.getType()).andStubReturn(Integer.class);
        reference1.document((Document) anyObject());
        replay(context, reference1);
        try {
            Expression expr = Expressions.createBoolean(context, "a=='123'");
            fail("Expected binding exception.");
        } finally {
            verify(context, reference1);
        }
    }

    @Test
    public void testNoNarrowingNeeded() {
        expect(context.selectAttribute("a")).andReturn(reference1);
        expect(reference1.getType()).andStubReturn(String.class);
        replay(context, reference1);
        Expression expr = Expressions.createBoolean(context, "a=='123'");
        verify(context, reference1);
    }

    @Test
    public void testNarrowingPossibleAndNeeded() {
        expect(context.selectAttribute("a")).andReturn(reference1);
        expect(reference1.getType()).andStubReturn(Object.class);
        expect(reference1.narrow(String.class)).andReturn(reference2);
        expect(reference2.getType()).andStubReturn(String.class);
        replay(context, reference1, reference2);
        Expression expr = Expressions.createBoolean(context, "a=='123'");
        verify(context, reference1, reference2);
    }
}
