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

import org.codehaus.preon.el.BindingException;
import org.codehaus.preon.el.Document;
import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.ReferenceContext;
import org.codehaus.preon.Resolver;

public class ContextReplacingReference implements Reference<Resolver> {

    private ReferenceContext<Resolver> alternativeContext;

    private Reference<Resolver> reference;

    public ContextReplacingReference(
            ReferenceContext<Resolver> alternativeContext,
            Reference<Resolver> reference) {
        this.alternativeContext = alternativeContext;
        this.reference = reference;
    }

    public ReferenceContext<Resolver> getReferenceContext() {
        return alternativeContext;
    }

    public Class<?> getType() {
        return reference.getType();
    }

    public boolean isAssignableTo(Class<?> type) {
        return reference.isAssignableTo(type);
    }

    public Object resolve(Resolver context) {
        return reference.resolve(context);
    }

    public Reference<Resolver> selectAttribute(String name)
            throws BindingException {
        return new ContextReplacingReference(alternativeContext, reference
                .selectAttribute(name));
    }

    public Reference<Resolver> selectItem(String index) throws BindingException {
        return new ContextReplacingReference(alternativeContext, reference
                .selectItem(index));
    }

    public Reference<Resolver> selectItem(Expression<Integer, Resolver> index)
            throws BindingException {
        return new ContextReplacingReference(alternativeContext, reference
                .selectItem(index));
    }

    public void document(Document target) {
        reference.document(target);
    }

    public Reference<Resolver> narrow(Class<?> type) {
        Reference<Resolver> narrowed = this.reference.narrow(type);
        if (narrowed == null) {
            return null;
        } else {
            return new ContextReplacingReference(alternativeContext, narrowed);
        }
    }

    public boolean isBasedOn(ReferenceContext<Resolver> other) {
        return reference.isBasedOn(other);
    }

    public Reference<Resolver> rescope(ReferenceContext<Resolver> context) {
        return reference.rescope(context);
    }

}
