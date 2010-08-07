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

import org.codehaus.preon.el.ReferenceResolver;
import org.codehaus.preon.el.UnresolvableReferenceException;
import org.codehaus.preon.reflect.ReflectionUtils;
import org.codehaus.preon.reflect.RuntimeNoSuchFieldException;

*//**
 * A {@link ReferenceResolver} that is not only able to resolve variables to
 * sibling property values, but also able to resolve variables to variables on
 * the outer class definition, in case of inner classes.
 *
 * <p>
 * Currently, this {@link ReferenceResolver} only makes guarantees for classes
 * nested one level deep.
 * </p>
 *
 * @author Wilfred Springer
 *
 *//*
public class NestedClassSupportingReferenceResolver extends
		DefaultReferenceResolver {

	public NestedClassSupportingReferenceResolver(Class type) {
		super(type);
	}

	@Override
	public Object resolve(Object context, String... reference)
			throws UnresolvableReferenceException {
		if ("enclosing".equals(reference[0])) {
			Class enclosing = context.getClass().getEnclosingClass();
			if (enclosing != null) {
				Field field = null;
				try {
					field = ReflectionUtils.getField(context.getClass(),
							"this$0");
				} catch (RuntimeNoSuchFieldException rnmsfe) {
					// The Eclipse compiler case
					field = ReflectionUtils.getField(context.getClass(),
							"this$1");
				}
				ReflectionUtils.makeAssessible(field);
				Object object = ReflectionUtils.getValue(field, context);
				return resolveValue(object, 1, reference);
			} else {
				throw new UnresolvableReferenceException(reference);
			}
		} else {
			return super.resolve(context, reference);
		}
	}

}
*/