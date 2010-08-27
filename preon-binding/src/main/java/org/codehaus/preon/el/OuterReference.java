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
