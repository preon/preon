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

import org.codehaus.preon.el.BindingException;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.ResolverContext;
import junit.framework.TestCase;

import static org.easymock.EasyMock.*;

public class OuterReferenceTest extends TestCase {

    private ResolverContext outerContext;
    private ResolverContext originalContext;
    private Reference sampleReference;
    private Resolver outerResolver;
    private Resolver originalResolver;

    public void setUp() {
        outerContext = createMock(ResolverContext.class);
        originalContext = createMock(ResolverContext.class);
        sampleReference = createMock(Reference.class);
        outerResolver = createMock(Resolver.class);
        originalResolver = createMock(Resolver.class);
    }

    public void testCreateReference() {
        expect(outerContext.selectAttribute("foobar")).andReturn(
                sampleReference);
        expect(originalResolver.get(OuterReference.DEFAULT_OUTER_NAME))
                .andReturn(outerResolver);
        expect(sampleReference.resolve(isA(Resolver.class))).andReturn("Wilfred");
        expect(originalResolver.getOriginalResolver()).andReturn(originalResolver);

        // Replay
        replay(outerContext, originalContext, sampleReference, outerResolver,
                originalResolver);

        OuterReference reference = new OuterReference(outerContext,
                originalContext);
        Reference<Resolver> result = reference.selectAttribute("foobar");
        assertNotSame(sampleReference, result);
        assertEquals(originalContext, result.getReferenceContext());
        result.resolve(originalResolver);

        // Verify
        verify(outerContext, originalContext, sampleReference, outerResolver,
                originalResolver);
    }

    @SuppressWarnings("unchecked")
    public void testResolveOuterResolverNull() {
        expect(outerContext.selectAttribute("foobar")).andReturn(
                sampleReference);
        expect(originalResolver.get(OuterReference.DEFAULT_OUTER_NAME))
                .andReturn(null);

        // Replay
        replay(outerContext, originalContext, sampleReference, outerResolver,
                originalResolver);

        OuterReference reference = new OuterReference(outerContext,
                originalContext);
        Reference<Resolver> result = reference.selectAttribute("foobar");
        try {
            result.resolve(originalResolver);
            fail("BindingException expected");
        } catch (BindingException expected) {
        }
    }
}
