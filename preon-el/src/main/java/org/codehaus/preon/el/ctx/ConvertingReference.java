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

import org.codehaus.preon.el.BindingException;
import org.codehaus.preon.el.Document;
import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.ReferenceContext;
import org.codehaus.preon.el.util.Converter;
import org.codehaus.preon.el.util.Converters;

public class ConvertingReference<T, E> implements Reference<E> {

    private Reference<E> reference;
    
    private Class<T> type;
    
    private Converter<Object, Class<T>> converter;
    
    public ConvertingReference(Class<T> type, Reference<E> reference) {
        this.type = type;
        this.reference = reference;
        this.converter = (Converter<Object, Class<T>>) Converters.get(reference.getType(), type);
    }
    
    public ReferenceContext<E> getReferenceContext() {
        return reference.getReferenceContext();
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isAssignableTo(Class<?> type) {
        return type.isAssignableFrom(this.type);
    }

    public Object resolve(E context) {
        return converter.convert(reference.resolve(context));
    }

    public Reference<E> selectAttribute(String name) throws BindingException {
        return reference.selectAttribute(name);
    }

    public Reference<E> selectItem(String index) throws BindingException {
        return reference.selectItem(index);
    }

    public Reference<E> selectItem(Expression<Integer, E> index) throws BindingException {
        return reference.selectItem(index);
    }

    public void document(Document target) {
        reference.document(target);
    }
    
    public static <T,E> ConvertingReference<T, E> create(Class<T> type, Reference<E> reference) {
        return new ConvertingReference<T, E>(type, reference);
    }

    public Reference<E> narrow(Class<?> type) throws BindingException {
        if (this.type.isAssignableFrom(type)) {
            return this;
        } else {
            return null;
        }
    }

    public boolean isBasedOn(ReferenceContext<E> context) {
        return reference.isBasedOn(context);
    }

    public Reference<E> rescope(ReferenceContext<E> context) {
        return new ConvertingReference<T,E>(type, reference.rescope(context));
    }

}
