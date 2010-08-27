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
