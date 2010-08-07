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

package nl.flotsam.limbo.ctx;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Date;

import nl.flotsam.limbo.BindingException;
import nl.flotsam.limbo.Document;
import nl.flotsam.limbo.Reference;
import nl.flotsam.limbo.ReferenceContext;
import nl.flotsam.limbo.ctx.MultiReference;
import nl.flotsam.limbo.util.StringBuilderDocument;
import junit.framework.TestCase;

/**
 * A collection of tests for the {@link MultiReference}.
 * 
 * @author Wilfred Springer (wis)
 * 
 */
public class MultiReferenceTest extends TestCase {

    private Reference reference1;

    private Reference reference2;

    private Reference reference3;

    private ReferenceContext context;

    public void setUp() {
        this.reference1 = createMock(Reference.class);
        this.reference2 = createMock(Reference.class);
        this.reference3 = createMock(Reference.class);
        this.context = createMock(ReferenceContext.class);
    }

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
            fail("Expecting exception.");
        } catch (BindingException rre) {
            // Right on!
        }
        verify(reference1, reference2);
    }

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
