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
