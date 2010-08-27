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
