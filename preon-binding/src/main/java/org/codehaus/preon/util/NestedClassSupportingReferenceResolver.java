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
package org.codehaus.preon.util;


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