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
package org.codehaus.preon.el.ctx;

import static org.junit.Assert.*;
import org.codehaus.preon.el.BindingException;
import org.codehaus.preon.el.Document;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.ReferenceContext;
import org.codehaus.preon.el.util.StringBuilderDocument;

import java.util.Date;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;

/**
 * A collection of tests for the {@link MultiReference}.
 * 
 * @author Wilfred Springer (wis)
 * 
 */
public class MultiReferenceTest {

    private Reference reference1;

    private Reference reference2;

    private Reference reference3;

    private ReferenceContext context;

    @Before
    public void setUp() {
        this.reference1 = createMock(Reference.class);
        this.reference2 = createMock(Reference.class);
        this.reference3 = createMock(Reference.class);
        this.context = createMock(ReferenceContext.class);
    }

    @Test
    public void testResolution() {
        Object runtimeContext = new Object();
        Object result = new Object();
        expect(reference1.getReferenceContext()).andReturn(context);
        expect(reference2.getReferenceContext()).andReturn(context);
        expect(reference1.resolve(runtimeContext)).andReturn(result);
        expect(reference1.getType()).andReturn(String.class);
        expect(reference2.getType()).andReturn(Date.class);
        replay(reference1, reference2);
        MultiReference multi = new MultiReference(reference1, reference2);
        assertEquals(result, multi.resolve(runtimeContext));
        verify(reference1, reference2);
    }

    @Test
    public void testResolution2ndAttempt() {
        Object runtimeContext = new Object();
        Object result = new Object();
        expect(reference1.getReferenceContext()).andReturn(context);
        expect(reference2.getReferenceContext()).andReturn(context);
        expect(reference1.resolve(runtimeContext)).andThrow(new BindingException("Not found."));
        expect(reference2.resolve(runtimeContext)).andReturn(result);
        expect(reference1.getType()).andReturn(String.class);
        expect(reference2.getType()).andReturn(String.class);
        replay(reference1, reference2);
        MultiReference multi = new MultiReference(reference1, reference2);
        assertEquals(result, multi.resolve(runtimeContext));
        verify(reference1, reference2);
    }

    @Test(expected=BindingException.class)
    public void testFailedResolution() {
        Object runtimeContext = new Object();
        Object result = new Object();
        expect(reference1.getReferenceContext()).andReturn(context);
        expect(reference2.getReferenceContext()).andReturn(context);
        expect(reference1.getType()).andReturn(String.class);
        expect(reference2.getType()).andReturn(String.class);
        expect(reference1.resolve(runtimeContext)).andThrow(new BindingException("Not found."));
        expect(reference2.resolve(runtimeContext)).andThrow(new BindingException("Not found."));
        replay(reference1, reference2);
        MultiReference multi = new MultiReference(reference1, reference2);
        try {
            multi.resolve(runtimeContext);
        } finally {
            verify(reference1, reference2);
        }
    }

    @Test
    public void testSelectIndex() {
        String index = "pi";
        Reference selected1 = createMock(Reference.class);
        Reference selected2 = createMock(Reference.class);
        expect(reference1.getReferenceContext()).andReturn(context);
        expect(reference2.getReferenceContext()).andReturn(context);
        expect(reference1.getType()).andReturn(String.class);
        expect(reference2.getType()).andReturn(String.class);
        expect(selected1.getType()).andReturn(String.class);
        expect(selected2.getType()).andReturn(String.class);
        expect(reference1.selectItem(index)).andReturn(selected1);
        expect(reference2.selectItem(index)).andReturn(selected2);
        expect(selected1.getReferenceContext()).andReturn(context);
        expect(selected2.getReferenceContext()).andReturn(context);
        replay(reference1, reference2, selected1, selected2);
        MultiReference multi = new MultiReference(reference1, reference2);
        assertNotNull(multi.selectItem(index));
        verify(reference1, reference2, selected1, selected2);
    }

    @Test
    public void testSelectProperty() {
        String propertyName = "pi";
        Reference selected1 = createMock(Reference.class);
        Reference selected2 = createMock(Reference.class);
        expect(reference1.getType()).andReturn(String.class);
        expect(reference2.getType()).andReturn(String.class);
        expect(selected1.getType()).andReturn(String.class);
        expect(selected2.getType()).andReturn(String.class);
        expect(reference1.getReferenceContext()).andReturn(context);
        expect(reference2.getReferenceContext()).andReturn(context);
        expect(reference1.selectAttribute(propertyName)).andReturn(selected1);
        expect(reference2.selectAttribute(propertyName)).andReturn(selected2);
        expect(selected1.getReferenceContext()).andReturn(context);
        expect(selected2.getReferenceContext()).andReturn(context);
        replay(reference1, reference2, selected1, selected2);
        MultiReference multi = new MultiReference(reference1, reference2);
        assertNotNull(multi.selectAttribute(propertyName));
        verify(reference1, reference2, selected1, selected2);
    }

    @Test
    public void testSelectNonExistingProperty() {
        StringBuilder builder = new StringBuilder();
        Document document = new StringBuilderDocument(builder);
        String propertyName = "pi";
        Reference selected1 = createMock(Reference.class);
        Reference selected2 = createMock(Reference.class);
        expect(reference1.getType()).andReturn(String.class);
        expect(reference2.getType()).andReturn(String.class);
        expect(selected1.getType()).andReturn(String.class);
        expect(reference1.getReferenceContext()).andReturn(context);
        expect(reference2.getReferenceContext()).andReturn(context);
        expect(reference1.selectAttribute(propertyName)).andReturn(selected1);
        expect(reference2.selectAttribute(propertyName)).andThrow(new BindingException("No property pi"));
        selected1.document(document);
        expect(selected1.getReferenceContext()).andReturn(context);
        replay(reference1, reference2, selected1, selected2);
        MultiReference multi = new MultiReference(reference1, reference2);
        Reference selected = multi.selectAttribute(propertyName);
        assertNotNull(selected);
        selected.document(document);
        verify(reference1, reference2, selected1, selected2);
    }

    @Test
    public void testNarrow() {
        StringBuilder builder = new StringBuilder();
        Document document = new StringBuilderDocument(builder);
        String propertyName = "pi";
        expect(reference1.narrow(String.class)).andReturn(reference1);
        expect(reference2.narrow(String.class)).andReturn(reference2);
        expect(reference1.getType()).andReturn(String.class).times(2);
        expect(reference2.getType()).andReturn(String.class).times(2);
        expect(reference1.getReferenceContext()).andReturn(context).times(2);
        expect(reference2.getReferenceContext()).andReturn(context).times(2);
        replay(reference1, reference2, context);
        MultiReference multi = new MultiReference(reference1, reference2);
        multi.narrow(String.class);
        verify(reference1, reference2, context);
    }

    @Test
    public void testNarrowPartly() {
        StringBuilder builder = new StringBuilder();
        Document document = new StringBuilderDocument(builder);
        String propertyName = "pi";
        expect(reference1.narrow(String.class)).andReturn(reference1);
        expect(reference2.narrow(String.class)).andReturn(null);
        expect(reference1.getType()).andReturn(String.class).times(2);
        expect(reference2.getType()).andReturn(String.class).times(1);
        expect(reference1.getReferenceContext()).andReturn(context).times(2);
        expect(reference2.getReferenceContext()).andReturn(context).times(1);
        replay(reference1, reference2, context);
        MultiReference multi = new MultiReference(reference1, reference2);
        multi.narrow(String.class);
        verify(reference1, reference2, context);
    }

    @Test
    public void testDocumentation() {
        StringBuilderDocument document = new StringBuilderDocument();
        expect(reference1.getReferenceContext()).andReturn(context);
        expect(reference2.getReferenceContext()).andReturn(context);
        expect(reference3.getReferenceContext()).andReturn(context);
        expect(reference1.getType()).andReturn(String.class);
        expect(reference2.getType()).andReturn(String.class);
        expect(reference3.getType()).andReturn(String.class);
        reference1.document(document);
        reference2.document(document);
        reference3.document(document);
        replay(reference1, reference2, reference3);
        MultiReference multi = new MultiReference(reference1, reference2, reference3);
        multi.document(document);
        System.out.println(document.toString());
        verify(reference1, reference2, reference3);
    }

}
