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
 * A {@link Reference} to support references to objects referenced from outer references. Whenever a new reference is
 * constructed from a {@link OuterReference}, then that new reference needs to preserve some properties:
 * <p/>
 * <ul> <li>{@link #getReferenceContext()} will need to return a reference to the original {@link ResolverContext};</li>
 * <li>{@link #resolve(Resolver)} will first need to Resolve the "outer" resolver, using the {@link #outerName};</li>
 * <li>... and these properties need to be preserved for all next references constructed from this reference.</li>
 * </ul>
 *
 * @author Wilfred Springer (wis)
 */
public class OuterResolvingReference implements Reference<Resolver> {

    /**
     * The name to use when resolving the <em>actual</em> {@link Resolver} we need to use, from the {@link Resolver}
     * passed to {@link #resolve(Resolver)}.
     */
    private String outerName;

    /**
     * The original {@link ResolverContext} to return by this reference. (To make sure that any other references are
     * always contructed from this context.)
     */
    private final ResolverContext originalContext;

    /** The {@link Reference} wrapped. */
    private Reference<Resolver> wrapped;

    /**
     * The {@link ResolverContext} to which this is reference is essentially pointing.
     */
    private final ResolverContext outerContext;

    /**
     * Constructs a new instance.
     *
     * @param outerName       The name to use when resolving the outer {@link org.codehaus.preon.Resolver} from the {@link org.codehaus.preon.Resolver} passed
     *                        to {@link #resolve(org.codehaus.preon.Resolver)}.
     * @param originalContext The context to be returned.
     * @param outerContext
     */
    public OuterResolvingReference(String outerName,
                                   ResolverContext originalContext, Reference<Resolver> wrapped, ResolverContext outerContext) {
        this.outerName = outerName;
        this.originalContext = originalContext;
        this.wrapped = wrapped;
        this.outerContext = outerContext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.Reference#getReferenceContext()
     */

    public ReferenceContext<Resolver> getReferenceContext() {
        return originalContext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.Reference#getType()
     */

    public Class<?> getType() {
        return wrapped.getType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.Reference#isAssignableTo(java.lang.Class)
     */

    public boolean isAssignableTo(Class<?> type) {
        return wrapped.isAssignableTo(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.Reference#resolve(java.lang.Object)
     */

    public Object resolve(Resolver resolver) {
        Object outerResolver = resolver.get(outerName);
        if (outerResolver != null && outerResolver instanceof Resolver) {
            Resolver originalResolver = resolver.getOriginalResolver();
            return wrapped.resolve(new OriginalReplacingResolver(
                    originalResolver, (Resolver) outerResolver));
        } else {
            throw new BindingException("Failed to resolve " + outerName
                    + " to a value of the proper type; got "
                    + (outerResolver == null ? outerResolver : outerResolver.getClass().getSimpleName()));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.ReferenceContext#selectAttribute(java.lang.String)
     */

    public Reference<Resolver> selectAttribute(String name)
            throws BindingException {
        Reference<Resolver> actual = wrapped.selectAttribute(name);
        return new OuterResolvingReference(outerName, originalContext, actual, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.ReferenceContext#selectItem(java.lang.String)
     */

    public Reference<Resolver> selectItem(String index) throws BindingException {
        Reference<Resolver> actual = wrapped.selectItem(index);
        return new OuterResolvingReference(outerName, originalContext, actual, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.codehaus.preon.el.ReferenceContext#selectItem(org.codehaus.preon.el.Expression)
     */

    public Reference<Resolver> selectItem(Expression<Integer, Resolver> index)
            throws BindingException {
        Reference<Resolver> actual = wrapped.selectItem(index);
        return new OuterResolvingReference(outerName, originalContext, actual, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.Descriptive#document(org.codehaus.preon.el.Document)
     */

    public void document(Document target) {
        wrapped.document(target);
    }

    private static class OriginalReplacingResolver implements Resolver {

        private Resolver originalResolver;
        private Resolver currentResolver;

        public OriginalReplacingResolver(Resolver originalResolver,
                                         Resolver currentResolver) {
            this.currentResolver = currentResolver;
            this.originalResolver = originalResolver;
        }

        public Object get(String name) throws BindingException {
            return currentResolver.get(name);
        }

        public Resolver getOriginalResolver() {
            return originalResolver;
        }

    }

    public Reference<Resolver> narrow(Class<?> type) {
        Reference<Resolver> narrowed = wrapped.narrow(type);
        if (narrowed == null) {
            return this;
        } else {
            return new OuterResolvingReference(outerName, originalContext,
                    narrowed, null);
        }
    }

    public boolean isBasedOn(ReferenceContext<Resolver> context) {
        return this.wrapped.isBasedOn(context);
    }

    public Reference<Resolver> rescope(ReferenceContext<Resolver> context) {
        if (outerContext.equals(context)) {
            return wrapped;
        } else {
            return wrapped.rescope(context);
        }
    }

}
