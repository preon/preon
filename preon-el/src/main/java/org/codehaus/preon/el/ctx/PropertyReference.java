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

import java.lang.reflect.Field;

import org.codehaus.preon.el.BindingException;
import org.codehaus.preon.el.Document;
import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Expressions;
import org.codehaus.preon.el.InvalidExpressionException;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.ReferenceContext;

public class PropertyReference<T> implements Reference<T> {

    private Field field;

    private Reference<T> reference;

    private ReferenceContext<T> context;

    /**
     * Include the type in the description generated.
     */
    private boolean includeType = true;

    public PropertyReference(Reference<T> reference, Class<?> type,
            String name, ReferenceContext<T> context) {
        this.reference = reference;
        this.context = context;
        try {
            field = type.getDeclaredField(name);
            field.setAccessible(true);
        } catch (SecurityException e) {
            throw new BindingException("Binding to " + name + " forbidden.");
        } catch (NoSuchFieldException e) {
            throw new BindingException("No field named " + name + ".");
        }
    }

    private PropertyReference(Reference<T> reference, Field field, ReferenceContext<T> context) {
        this.reference = reference;
        this.field = field;
        this.context = context;
    }

    public PropertyReference(Reference<T> reference, Class<?> type,
            String name, ReferenceContext<T> context, boolean includeType) {
        this(reference, type, name, context);
        this.includeType = includeType;
    }

    public Object resolve(T context) {
        try {
            return field.get(reference.resolve(context));
        } catch (IllegalArgumentException e) {
            throw new BindingException("Cannot resolve " + field.getName()
                    + " on context.", e);
        } catch (IllegalAccessException e) {
            throw new BindingException("Access denied for field  "
                    + field.getName(), e);
        }
    }

    public Reference<T> selectAttribute(String name) {
        return new PropertyReference<T>(this, this.getType(), name, context);
    }

    public Reference<T> selectItem(String index) {
        try {
            Expression<Integer, T> expr = Expressions.createInteger(context,
                    index);
            return selectItem(expr);
        } catch (InvalidExpressionException e) {
            throw new BindingException("Invalid index.", e);
        }
    }

    public Reference<T> selectItem(Expression<Integer, T> index) {
        Class<?> type = this.field.getType();
        return new ArrayElementReference<T>(this, type.getComponentType(), index,
                context);
    }

    public Class<?> getType() {
        return field.getType();
    }

    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        } else if (other instanceof PropertyReference) {
            return equals((PropertyReference<T>) other);
        } else {
            return false;
        }
    }

    public boolean equals(PropertyReference<T> other) {
        return field.equals(other.field) && reference.equals(other.reference);
    }

    public void document(Document target) {
        target.text("the " + field.getName());
        if (includeType) {
            target.text(" (a ");
            target.text(getType().getSimpleName());
            target.text(") ");
        } else {
            target.text(" ");
        }
        target.text("of ");
        reference.document(target);

    }

    public ReferenceContext<T> getReferenceContext() {
        return context;
    }

    public boolean isAssignableTo(Class<?> type) {
        return field.getType().isAssignableFrom(type);
    }

    public Reference<T> narrow(Class<?> type) {
        if (field.getType().isAssignableFrom(type)) {
            return this;
        } else {
            return null;
        }
    }

    public boolean isBasedOn(ReferenceContext<T> context) {
        return reference.isBasedOn(context);
    }

    public Reference<T> rescope(ReferenceContext<T> other) {
        return new PropertyReference<T>(reference.rescope(other), field, this.context);
    }

}