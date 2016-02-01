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

/**
 * Reference to boolean literal values.
 *
 * @param <E>
 */
public class BooleanLiteralReference<E> implements Reference<E> {

    private boolean value;
    private ReferenceContext<E> context;

    public BooleanLiteralReference(boolean value, ReferenceContext<E> context) {
        this.value = value;
        this.context = context;
    }
    
    public Object resolve(E context) {
        return value;
    }

    public ReferenceContext<E> getReferenceContext() {
        return context;
    }

    public boolean isAssignableTo(Class<?> type) {
        return type.isAssignableFrom(Boolean.class);
    }

    public Class<?> getType() {
        return Boolean.class;
    }

    public Reference<E> narrow(Class<?> type) {
        return this;
    }

    public boolean isBasedOn(ReferenceContext<E> eReferenceContext) {
        return false;
    }

    public Reference<E> rescope(ReferenceContext<E> eReferenceContext) {
        return this;
    }

    public Reference<E> selectAttribute(String name) throws BindingException {
        throw new BindingException("No such attribute");
    }

    public Reference<E> selectItem(String index) throws BindingException {
        throw new BindingException("No such indexed value");
    }

    public Reference<E> selectItem(Expression<Integer, E> index) throws BindingException {
        throw new BindingException("No such indexed value");
    }

    public void document(Document target) {
        target.text(Boolean.toString(value));
    }
}
