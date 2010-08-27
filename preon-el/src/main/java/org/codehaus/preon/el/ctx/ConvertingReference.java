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
