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

public class VariableContext implements ReferenceContext<VariableResolver> {

    private VariableDefinitions defs;

    public VariableContext(VariableDefinitions defs) {
        this.defs = defs;
    }

    public Reference<VariableResolver> selectAttribute(String name) {
        return new VariableReference(name, this);
    }

    private static class VariableReference implements
            Reference<VariableResolver> {

        private String name;

        private VariableContext context;

        public VariableReference(String name, VariableContext context) {
            this.name = name;
            this.context = context;
        }

        public Object resolve(VariableResolver context) {
            return context.get(name);
        }

        public Reference<VariableResolver> selectAttribute(String name) {
            return new PropertyReference<VariableResolver>(this, context.defs
                    .getType(this.name), name, context);
        }

        public Reference<VariableResolver> selectItem(String index) {
            throw new org.codehaus.preon.el.BindingException(
                    "No indexed values defined for VariableContext.");
        }

        public Reference<VariableResolver> selectItem(
                Expression<Integer, VariableResolver> index) {
            throw new BindingException(
                    "No indexed values defined for VariableContext.");
        }

        @SuppressWarnings("unchecked")
        public boolean equals(Object other) {
            if (other == null) {
                return false;
            } else if (other instanceof VariableReference) {
                return equals((VariableReference) other);
            } else {
                return false;
            }
        }

        public boolean equals(VariableReference other) {
            return context.defs.equals(other.context.defs);
        }

        public void document(Document target) {
            target.text("the " + name);
        }

        public ReferenceContext<VariableResolver> getReferenceContext() {
            return context;
        }

        @SuppressWarnings("unchecked")
        public boolean isAssignableTo(Class<?> type) {
            return context.defs.getType(name).isAssignableFrom(type);
        }

        public Class<?> getType() {
            return context.defs.getType(name);
        }

        public Reference<VariableResolver> narrow(Class<?> type) {
            if (context.defs.getType(name).isAssignableFrom(type)) {
                return this;
            } else {
                return null;
            }
        }

        public boolean isBasedOn(ReferenceContext<VariableResolver> other) {
            return context.equals(other);            
        }

        public Reference<VariableResolver> rescope(ReferenceContext<VariableResolver> variableResolverReferenceContext) {
            return this;
        }


    }

    public Reference<VariableResolver> selectItem(String index) {
        throw new BindingException("Index not supported");
    }

    public Reference<VariableResolver> selectItem(
            Expression<Integer, VariableResolver> index) {
        throw new BindingException("Index not supported");
    }

    public void document(Document target) {
        defs.document(target);
    }

}
