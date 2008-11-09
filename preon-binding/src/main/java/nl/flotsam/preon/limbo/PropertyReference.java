/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.flotsam.preon.limbo;

import java.lang.reflect.Field;

import nl.flotsam.limbo.BindingException;
import nl.flotsam.limbo.Document;
import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Expressions;
import nl.flotsam.limbo.InvalidExpressionException;
import nl.flotsam.limbo.Reference;
import nl.flotsam.limbo.ReferenceContext;
import nl.flotsam.preon.Resolver;

public class PropertyReference implements Reference<Resolver> {

    private Field field;

    private Reference<Resolver> reference;

    private ReferenceContext<Resolver> context;

    /**
     * Include the type in the description generated.
     */
    private boolean includeType = true;

    public PropertyReference(Reference<Resolver> reference, Class<?> type,
            String name, ReferenceContext<Resolver> context) {
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

    public PropertyReference(Reference<Resolver> reference, Class<?> type,
            String name, ReferenceContext<Resolver> context, boolean includeType) {
        this(reference, type, name, context);
        this.includeType = includeType;
    }

    public Object resolve(Resolver context) {
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

    public Reference<Resolver> selectAttribute(String name) {
        return new PropertyReference(this, this.getType(), name, context);
    }

    public Reference<Resolver> selectItem(String index) {
        try {
            Expression<Integer, Resolver> expr = Expressions.createInteger(context,
                    index);
            return selectItem(expr);
        } catch (InvalidExpressionException e) {
            throw new BindingException("Invalid index.", e);
        }
    }

    public Reference<Resolver> selectItem(Expression<Integer, Resolver> index) {
        Class<?> type = this.field.getType();
        return new ArrayElementReference(this, type.getComponentType(), index,
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
            return equals((PropertyReference) other);
        } else {
            return false;
        }
    }

    public boolean equals(PropertyReference other) {
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

    public ReferenceContext<Resolver> getReferenceContext() {
        return context;
    }

    public boolean isAssignableTo(Class<?> type) {
        return type.isAssignableFrom(field.getType());
    }

}