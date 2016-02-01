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

import java.lang.reflect.Field;

import org.codehaus.preon.el.BindingException;
import org.codehaus.preon.el.Document;
import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Expressions;
import org.codehaus.preon.el.InvalidExpressionException;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.ReferenceContext;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.rendering.CamelCaseRewriter;
import org.codehaus.preon.rendering.IdentifierRewriter;

/**
 * A {@link Reference} to a property. (And in this case, property means a private field, not a bean property.)
 *
 * @author Wilfred Springer (wis)
 */
public class PropertyReference implements Reference<Resolver> {

    private static IdentifierRewriter rewriter = new CamelCaseRewriter(false);

    /** The field representing the property. */
    private Field field;

    /** The {@link Reference} to the object this {@link PropertyReference} is based upon. */
    private Reference<Resolver> reference;

    /** The context for constructing references. */
    private ReferenceContext<Resolver> context;

    /** Include the type in the description generated. */
    private boolean includeType = true;

    /**
     * Constructs a new {@link Reference}.
     *
     * @param reference The source for this property.
     * @param type      The type of the source.
     * @param name      The name of the property.
     * @param context   The original context, to be used when constructing other references.
     */
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

    private PropertyReference(Reference<Resolver> reference, Field field, ReferenceContext<Resolver> context) {
        this.reference = reference;
        this.field = field;
        this.context = context;
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
            Expression<Integer, Resolver> expr = Expressions.createInteger(
                    context, index);
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
        target.text("the " + rewriter.rewrite(field.getName()));
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

    public Reference<Resolver> narrow(Class<?> type) {
        if (type.isAssignableFrom(field.getType())) {
            return this;
        } else {
            return null;
        }
    }

    public boolean isBasedOn(ReferenceContext<Resolver> context) {
        return this.context.equals(context);
    }

    public Reference<Resolver> rescope(ReferenceContext<Resolver> context) {
        return new PropertyReference(reference.rescope(context), field, context);
    }

}