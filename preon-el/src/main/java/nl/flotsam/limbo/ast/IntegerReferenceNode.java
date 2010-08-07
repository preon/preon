/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.flotsam.limbo.ast;

import nl.flotsam.limbo.Reference;


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
     * @see nl.flotsam.limbo.ast.Node#getType()
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
     * @see nl.flotsam.limbo.ast.Node#simplify()
     */
    public Node<Integer, E> simplify() {
        return this;
    }

    /*
     * (non-Javadoc)
     * @see nl.flotsam.limbo.Expression#isParameterized()
     */
    public boolean isParameterized() {
        return true;
    }


}
