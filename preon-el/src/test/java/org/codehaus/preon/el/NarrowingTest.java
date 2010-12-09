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
