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

import org.codehaus.preon.Resolver;
import org.codehaus.preon.ResolverContext;

/**
 * A {@link Reference} to the outer context. This {@link Reference} will create new references ({@link
 * #selectAttribute(String)}, {@link #selectItem(Expression)}, etc.) that will be based on the {@link #outerContext}
 * passed in. In order to make sure the {@link #originalContext} will be preserved by wrapping these new {@link
 * Reference}s in objects that will handle all incoming calls appropriately.
 *
 * @author Wilfred Springer (wis)
 */
public class OuterReference implements Reference<Resolver> {

    /**
     * The default name of referring to the outer context.
     */
    public final static String DEFAULT_OUTER_NAME = "outer";

    /**
     * The "outer" {@link ResolverContext}.
     */
    private ResolverContext outerContext;

    /**
     * The "original" context from which this object was created. (Note that this not necessarily needs to be the sub
     * context of the outer context. It could be something way more deep down in the chain.
     */
    private ResolverContext originalContext;

    /**
     * Constructs a new instance.
     *
     * @param outerContext    The "outer" context.
     * @param originalContext The "original" context.
     * @see OuterReference#outerContext
     * @see OuterReference#originalContext
     */
    public OuterReference(ResolverContext outerContext,
                          ResolverContext originalContext) {
        this.outerContext = outerContext;
        this.originalContext = originalContext;
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.el.Reference#getType()
     */

    public Class<?> getType() {
        return Resolver.class;
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.el.Reference#isAssignableTo(java.lang.Class)
     */

    public boolean isAssignableTo(Class<?> type) {
        return type.isAssignableFrom(getType());
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.el.Reference#resolve(java.lang.Object)
     */

    public Object resolve(Resolver resolver) {
        throw new IllegalStateException("Never expected to be called.");
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.el.ReferenceContext#selectAttribute(java.lang.String)
     */

    public Reference<Resolver> selectAttribute(String name)
            throws BindingException {
        Reference<Resolver> actual = outerContext.selectAttribute(name);
        return new OuterResolvingReference(DEFAULT_OUTER_NAME, originalContext,
                actual, outerContext);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.el.ReferenceContext#selectItem(java.lang.String)
     */

    public Reference<Resolver> selectItem(String index) throws BindingException {
        Reference<Resolver> actual = outerContext.selectItem(index);
        return new OuterResolvingReference(DEFAULT_OUTER_NAME, originalContext,
                actual, null);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.el.ReferenceContext#selectItem(org.codehaus.preon.el.Expression)
     */

    public Reference<Resolver> selectItem(Expression<Integer, Resolver> index)
            throws BindingException {
        Reference<Resolver> actual = outerContext.selectItem(index);
        return new OuterResolvingReference(DEFAULT_OUTER_NAME, originalContext,
                actual, null);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.el.Descriptive#document(org.codehaus.preon.el.Document)
     */

    public void document(Document target) {
        target.text("the ");
        outerContext.document(target);
        target.text(" containing the ");
        originalContext.document(target);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.el.Reference#getReferenceContext()
     */

    public ReferenceContext<Resolver> getReferenceContext() {
        return outerContext;
    }

    public Reference<Resolver> narrow(Class<?> type) {
        return null;
    }

    public boolean isBasedOn(ReferenceContext<Resolver> context) {
        return outerContext.equals(context);
    }

    public Reference<Resolver> rescope(ReferenceContext<Resolver> resolverReferenceContext) {
        // Since instance of this class are taken out of the reference path, this operation is never expected to be
        // invoked on an instance of this class.
        throw new UnsupportedOperationException();
    }

}
