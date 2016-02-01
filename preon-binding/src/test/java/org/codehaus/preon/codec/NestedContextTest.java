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
//
//package org.codehaus.preon.codec;
//
//import org.codehaus.preon.el.Reference;
//import org.codehaus.preon.Resolver;
//import org.codehaus.preon.ResolverContext;
//import org.codehaus.preon.codec.CombinedObjectResolverContext;
//import org.codehaus.preon.codec.ObjectResolverContext;
//
//import junit.framework.TestCase;
//
//import static org.easymock.EasyMock.*;
//
//public class NestedContextTest extends TestCase {
//
//	private ObjectResolverContext inner;
//	private ResolverContext outer;
//	private Reference<Resolver> aReference;
//	private Reference<Resolver> bReference;
//	private Reference<Resolver> outerReference;
//	private Resolver resolver;
//	
//	public void setUp() {
//		inner = createMock(ObjectResolverContext.class);
//		outer = createMock(ResolverContext.class);
//		aReference = createMock(Reference.class);
//		bReference = createMock(Reference.class);
//		outerReference = createMock(Reference.class);
//		resolver = createMock(Resolver.class);
//	}
//	
//	public void testReferenceJustInner() {
//		expect(inner.selectAttribute("a")).andReturn(aReference);
//		expect(aReference.selectAttribute("b")).andReturn(bReference);
//		expect(bReference.resolve(resolver)).andReturn(3);
//		replay(inner, outer, aReference, bReference, resolver);
//		CombinedObjectResolverContext context = new CombinedObjectResolverContext(outer, inner, "outer");
//		Reference<Resolver> reference = context.selectAttribute("a");
//		reference = reference.selectAttribute("b");
//		assertEquals(3, reference.resolve(resolver));
//		verify(inner, outer, aReference, bReference, resolver);
//	}
//	
//	public void testReferenceOuter() {
//		expect(outer.selectAttribute("b")).andReturn(bReference);
//		replay(inner, outer, outerReference, bReference);
//		CombinedObjectResolverContext context = new CombinedObjectResolverContext(outer, inner, "outer");
//		Reference<Resolver> reference = context.selectAttribute("outer");
//		reference = reference.selectAttribute("b");
//		verify(inner, outer, outerReference, bReference);
//	}
//	
//	
//}
