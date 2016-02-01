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