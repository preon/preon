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