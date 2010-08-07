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
package org.codehaus.preon.el.ast;

import org.codehaus.preon.el.Reference;


/**
 * A {@link AbstractReferenceNode} subclass that evaluates to an integer value.
 * 
 * @author Wilfred Springer
 * 
 */
public class IntegerReferenceNode<E> extends AbstractReferenceNode<Integer, E> {

    public IntegerReferenceNode(Reference<E> reference) {
        super(reference);
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.el.ast.Node#getType()
     */
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
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

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.el.ast.Node#simplify()
     */
    public Node<Integer, E> simplify() {
        return this;
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.el.Expression#isParameterized()
     */
    public boolean isParameterized() {
        return true;
    }


}
