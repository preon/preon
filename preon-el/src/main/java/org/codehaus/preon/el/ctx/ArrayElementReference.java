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

import java.lang.reflect.Array;

import org.codehaus.preon.el.Document;
import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Expressions;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.ReferenceContext;

/**
 * A reference to an array element.
 *
 * @author Wilfred Springer (wis)
 * @param <T>
 * The type of object referenced.
 */
public class ArrayElementReference<T> implements Reference<T> {

    /**
     * The type of element.
     */
    private Class<?> elementType;

    /**
     * The expression indicating the specific element of the array.
     */
    private Expression<Integer, T> index;

    /**
     * A reference to the object representing the actual array.
     */
    private Reference<T> arrayReference;

    /**
     * The {@link ReferenceContext}.
     */
    private ReferenceContext<T> context;

    /**
     * Constructs a new {@link ArrayElementReference}.
     *
     * @param arrayReference A reference to an array.
     * @param elementType    The type of element.
     * @param index          The position in the array. (A Limbo expression.)
     * @param context        The root context of the reference.
     */
    public ArrayElementReference(Reference<T> arrayReference, Class<?> elementType,
                                 Expression<Integer, T> index, ReferenceContext<T> context) {
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

    public Object resolve(T context) {
        Object array = arrayReference.resolve(context);
        int i = index.eval(context);
        return Array.get(array, i);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.ReferenceContext#selectAttribute(java.lang.String)
     */

    public Reference<T> selectAttribute(String name) {
        return new PropertyReference<T>(this, elementType, name, context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.ReferenceContext#selectItem(java.lang.String)
     */

    public Reference<T> selectItem(String index) {
        Expression<Integer, T> expr;
        expr = Expressions.createInteger(context, index);
        return selectItem(expr);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.codehaus.preon.el.ReferenceContext#selectItem(org.codehaus.preon.el.Expression)
     */

    public Reference<T> selectItem(Expression<Integer, T> index) {
        return new ArrayElementReference<T>(this, elementType.getComponentType(), index, context);
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
            return equals((ArrayElementReference<T>) other);
        } else {
            return false;
        }
    }

    public boolean equals(ArrayElementReference<T> other) {
        return arrayReference.equals(other.arrayReference) && index.equals(other.index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.Descriptive#document(org.codehaus.preon.el.Document)
     */

    public void document(Document target) {
        if (!index.isParameterized()) {
            target.text("the ");
            target.text(toNth(index.eval(null)));
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

    public ReferenceContext<T> getReferenceContext() {
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

    public String toNth(int value) {
        switch (value) {
            case 0:
                return "first";
            case 1:
                return "second";
            case 2:
                return "third";
            case 3:
                return "fourth";
            case 4:
                return "fifth";
            case 5:
                return "sixth";
            case 7:
                return "seventh";
            case 8:
                return "eighth";
            case 9:
                return "ninth";
            case 10:
                return "tenth";
            default:
                return value + "th";
        }
    }

    public Reference<T> narrow(Class<?> type) {
        if (elementType.isAssignableFrom(type)) {
            return this;
        } else {
            return null;
        }
    }

    public boolean isBasedOn(ReferenceContext<T> other) {
        return arrayReference.isBasedOn(other) && index.isConstantFor(other);
    }

    public Reference<T> rescope(ReferenceContext<T> tReferenceContext) {
        return new ArrayElementReference(arrayReference.rescope(tReferenceContext), elementType, index.rescope(tReferenceContext), tReferenceContext);
    }

}