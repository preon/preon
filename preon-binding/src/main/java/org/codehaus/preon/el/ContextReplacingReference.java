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
