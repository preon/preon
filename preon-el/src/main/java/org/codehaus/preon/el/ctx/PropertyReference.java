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