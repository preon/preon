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

import java.lang.reflect.Array;

import org.codehaus.preon.el.Document;
import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Expressions;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.ReferenceContext;
import org.codehaus.preon.el.ctx.PropertyReference;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.util.TextUtils;

/**
 * A reference to an array element.
 *
 * @author Wilfred Springer (wis)
 */
public class ArrayElementReference implements Reference<Resolver> {

    /** The type of element. */
    private Class<?> elementType;

    /** The expression indicating the specific element of the array. */
    private Expression<Integer, Resolver> index;

    /** A reference to the object representing the actual array. */
    private Reference<Resolver> arrayReference;

    /** The {@link ReferenceContext}. */
    private ReferenceContext<Resolver> context;

    /**
     * Constructs a new {@link ArrayElementReference}.
     *
     * @param arrayReference A reference to an array.
     * @param elementType    The type of element.
     * @param index          The position in the array. (A Limbo expression.)
     * @param context        The root context of the reference.
     */
    public ArrayElementReference(Reference<Resolver> arrayReference,
                                 Class<?> elementType, Expression<Integer, Resolver> index,
                                 ReferenceContext<Resolver> context) {
        this.arrayReference = arrayReference;
        this.elementType = elementType;
        this.index = index;
        this.context = context;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.Reference#resolve(java.lang.Object)
     */

    public Object resolve(Resolver context) {
        Object array = arrayReference.resolve(context);
        int i = index.eval(context.getOriginalResolver());
        return Array.get(array, i);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.ReferenceContext#selectAttribute(java.lang.String)
     */

    public Reference<Resolver> selectAttribute(String name) {
        return new PropertyReference<Resolver>(this, elementType, name, context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.ReferenceContext#selectItem(java.lang.String)
     */

    public Reference<Resolver> selectItem(String index) {
        Expression<Integer, Resolver> expr;
        expr = Expressions.createInteger(context, index);
        return selectItem(expr);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.codehaus.preon.el.ReferenceContext#selectItem(org.codehaus.preon.el.Expression)
     */

    public Reference<Resolver> selectItem(Expression<Integer, Resolver> index) {
        return new ArrayElementReference(this, elementType
                .getComponentType(), index, context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */

    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (other instanceof ArrayElementReference) {
            return equals((ArrayElementReference) other);
        } else {
            return false;
        }
    }

    public boolean equals(ArrayElementReference other) {
        return arrayReference.equals(other.arrayReference)
                && index.equals(other.index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.Descriptive#document(org.codehaus.preon.el.Document)
     */

    public void document(Document target) {
        if (!index.isParameterized()) {
            target.text("the ");
            target.text(TextUtils.getPositionAsText(index.eval(null)));
            target.text(" element of ");
            arrayReference.document(target);
        } else {
            target.text("the nth element of ");
            arrayReference.document(target);
            target.text(" (with n being ");
            index.document(target);
            target.text(")");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.Reference#getReferenceContext()
     */

    public ReferenceContext<Resolver> getReferenceContext() {
        return context;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.Reference#isAssignableTo(java.lang.Class)
     */

    public boolean isAssignableTo(Class<?> type) {
        return elementType.isAssignableFrom(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.Reference#getType()
     */

    public Class<?> getType() {
        return elementType;
    }

    public Reference<Resolver> narrow(Class<?> type) {
        if (type == elementType) {
            return this;
        } else {
            return null;
        }
    }

    public boolean isBasedOn(ReferenceContext<Resolver> resolverReferenceContext) {
        return this.arrayReference.isBasedOn(resolverReferenceContext) 
                && this.index.isConstantFor(resolverReferenceContext);
    }

    public Reference<Resolver> rescope(ReferenceContext<Resolver> newScope) {
        return new ArrayElementReference(arrayReference.rescope(newScope), elementType, index.rescope(newScope), newScope);
    }

}
