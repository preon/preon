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
package org.codehaus.preon.el.ast;

import java.util.Set;

import org.codehaus.preon.el.Document;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.ReferenceContext;

/**
 * The superclass of reference nodes.
 * 
 * @author Wilfred Springer
 * 
 */
public class IntegerReferenceNode<E> extends AbstractNode<Integer, E> {

    private Reference<E> reference;

    public IntegerReferenceNode(Reference<E> reference) {
        this.reference = reference;
    }

    protected Object resolveValue(E context) {
        return reference.resolve(context);
    }

    public void gather(Set<Reference<E>> references) {
        references.add(reference);
    }

    public void document(Document target) {
        reference.document(target);
    }

    @Override
    public boolean isConstantFor(ReferenceContext<E> context) {
        return reference.isBasedOn(context);
    }

    public Integer eval(E context) {
        Object value = resolveValue(context);
        Class<?> type = value.getClass();
        if (Byte.class.equals(type)) {
            return ((Byte) value).intValue();
        } else if (Short.class.equals(type)) {
            return ((Short) value).intValue();
        } else if (Long.class.equals(type)) {
            return ((Long) value).intValue();
        } else if (Integer.class.equals(type)) {
            return (Integer) value;
        } else {
            final StringBuilder builder = new StringBuilder();
            builder.append("Type of ");
//            describe(new DescriptionBuilder() {
//
//                public DescriptionBuilder append(String text) {
//                    builder.append(text);
//                    return this;
//                }
//
//                public DescriptionBuilder link(Object object, String text) {
//                    builder.append(text);
//                    return this;
//                }
//
//            });
            builder.append(" can not be interpreted");
            builder.append(" as an integer value.");
            throw new ClassCastException(builder.toString());
        }
    }

    public Class<Integer> getType() {
        return Integer.class;
    }

    public Node<Integer, E> simplify() {
        return this;
    }

    public Node<Integer, E> rescope(ReferenceContext<E> context) {
        return new IntegerReferenceNode<E>(reference.rescope(context));
    }

    public boolean isParameterized() {
        return true;
    }
}
