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

package nl.flotsam.limbo.ctx;

import nl.flotsam.limbo.BindingException;
import nl.flotsam.limbo.Document;
import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Reference;
import nl.flotsam.limbo.ReferenceContext;

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
            throw new nl.flotsam.limbo.BindingException(
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
