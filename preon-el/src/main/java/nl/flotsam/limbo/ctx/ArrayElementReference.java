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

import java.lang.reflect.Array;

import nl.flotsam.limbo.Document;
import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Expressions;
import nl.flotsam.limbo.Reference;
import nl.flotsam.limbo.ReferenceContext;

/**
 * A reference to an array element.
 * 
 * @author Wilfred Springer (wis)
 * 
 * @param <T>
 *            The type of object referenced.
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
     * @param arrayReference
     *            A reference to an array.
     * @param elementType
     *            The type of element.
     * @param index
     *            The position in the array. (A Limbo expression.)
     * @param context
     *            The root context of the reference.
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
     * @see nl.flotsam.limbo.Reference#resolve(java.lang.Object)
     */
    public Object resolve(T context) {
        Object array = arrayReference.resolve(context);
        int i = index.eval(context);
        return Array.get(array, i);
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.flotsam.limbo.ReferenceContext#selectAttribute(java.lang.String)
     */
    public Reference<T> selectAttribute(String name) {
        return new PropertyReference<T>(this, elementType, name, context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.flotsam.limbo.ReferenceContext#selectItem(java.lang.String)
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
     * nl.flotsam.limbo.ReferenceContext#selectItem(nl.flotsam.limbo.Expression)
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
     * @see nl.flotsam.limbo.Descriptive#document(nl.flotsam.limbo.Document)
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
     * @see nl.flotsam.limbo.Reference#getReferenceContext()
     */
    public ReferenceContext<T> getReferenceContext() {
        return context;
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.flotsam.limbo.Reference#isAssignableTo(java.lang.Class)
     */
    public boolean isAssignableTo(Class<?> type) {
        return elementType.isAssignableFrom(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see nl.flotsam.limbo.Reference#getType()
     */
    public Class<?> getType() {
        return elementType;
    }
    
    public String toNth(int value) {
        switch (value) {
            case 0: return "first";
            case 1: return "second";
            case 2: return "third";
            case 3: return "fourth";
            case 4: return "fifth";
            case 5: return "sixth";
            case 7: return "seventh";
            case 8: return "eighth";
            case 9: return "ninth";
            case 10: return "tenth";
            default: return value + "th";
        }
    }

    public Reference<T> narrow(Class<?> type) {
        if (elementType.isAssignableFrom(type)) {
            return this;
        } else {
            return null;
        }
    }

}