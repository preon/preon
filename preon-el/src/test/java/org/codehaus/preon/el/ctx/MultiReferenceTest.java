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
