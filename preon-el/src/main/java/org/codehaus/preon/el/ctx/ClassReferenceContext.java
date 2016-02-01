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

public class ClassReferenceContext<T> implements Reference<T> {

    private Class<T> type;

    public ClassReferenceContext(Class<T> type) {
        this.type = type;
    }

    public ReferenceContext<T> getReferenceContext() {
        return this;
    }

    public boolean isAssignableTo(Class<?> type) {
        return this.type.isAssignableFrom(type);
    }

    public Object resolve(T context) {
        return context;
    }

    public Reference<T> selectAttribute(String name) throws BindingException {
        return new PropertyReference<T>(this, type, name, this);
    }

    public Reference<T> selectItem(String index) throws BindingException {
        throw new BindingException("Items not supported.");
    }

    public Reference<T> selectItem(Expression<Integer, T> index)
            throws BindingException {
        throw new BindingException("Items not supported.");
    }

    public void document(Document target) {
        target.text("a " + type.getSimpleName());
    }

    public Class<?> getType() {
        return type;
    }

    public Reference<T> narrow(Class<?> type) {
        if (type.isAssignableFrom(type)) {
            return this;
        } else {
            return null;
        }
    }

    public boolean isBasedOn(ReferenceContext<T> other) {
        return this.equals(other);
    }

    public Reference<T> rescope(ReferenceContext<T> context) {
        assert context == this;
        return this;
    }

}
