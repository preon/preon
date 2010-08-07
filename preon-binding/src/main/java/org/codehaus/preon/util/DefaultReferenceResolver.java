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
package org.codehaus.preon.util;
/*
 * Copyright (C) 2008 Wilfred Springer
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

/*package org.codehaus.preon.util;

import java.lang.reflect.Field;

import org.codehaus.preon.el.DescriptionBuilder;
import org.codehaus.preon.el.ReferenceResolver;
import org.codehaus.preon.el.UnresolvableReferenceException;
import org.codehaus.preon.reflect.ReflectionUtils;
import org.codehaus.preon.rendering.CamelCaseRewriter;
import org.codehaus.preon.rendering.IdentifierRewriter;

*//**
 * A {@link ReferenceResolver} that will transitively resolve variables to
 * properties on an Object.
 *
 * @author Wilfred Springer
 *
 *//*
public class DefaultReferenceResolver implements
        RelativeReferenceResolver<Object> {

    *//**
 * The type of context.
 *//*
    private Class type;

    *//**
 * The object responsible for turning the name of the Field into something
 * sensible.
 *//*
    private IdentifierRewriter rewriter;

    *//**
 * Constructs a new instance.
 *
 * @param type
 *            The type of object passed in as a context.
 *//*
    public DefaultReferenceResolver(Class type) {
        this.type = type;
        rewriter = new CamelCaseRewriter();
    }

    public Object resolve(Object context, String... reference)
            throws UnresolvableReferenceException {
        return resolveValue(context, 0, reference);
    }

    private Field resolveField(Class type, int pointer, String... reference) {
        Field field = ReflectionUtils.getField(type, reference[pointer]);
        if (pointer == reference.length - 1) {
            return field;
        } else {
            return resolveField(field.getType(), pointer + 1, reference);
        }
    }

    protected Object resolveValue(Object object, int pointer,
            String... reference) {
        if (pointer == reference.length) {
            return object;
        } else {
            Field field = ReflectionUtils.getField(object.getClass(),
                    reference[pointer]);
            ReflectionUtils.makeAssessible(field);
            object = ReflectionUtils.getValue(field, object);
            return resolveValue(object, pointer + 1, reference);
        }
    }

    public void describe(StringBuilder target, String... reference)
            throws UnresolvableReferenceException {
        Field field = resolveField(type, 0, reference);
        target.append(rewriter.rewrite(field.getName()));
    }

    public void describe(DescriptionBuilder target, String... reference)
            throws UnresolvableReferenceException {
        Field field = resolveField(type, 0, reference);
        target.link(field, rewriter.rewrite(field.getName()));
    }

}
*/