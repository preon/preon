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
package org.codehaus.preon.el.util;

/**
 * A couple of utilities supporting classes.
 * 
 * @author Wilfred Springer
 * 
 */
public class ClassUtils {

    /**
     * Determines the common base type for a couple of types passed in.
     * 
     * @param types
     *            The collection of types to for which we need a common base
     *            class.
     * @return The common type.
     */
    public static Class<?> calculateCommonSuperType(Class<?>... types) {
        switch (types.length) {
            case 0:
                return null;
            case 1:
                return types[0];
            case 2:
                return calculateCommonSuperType(types[0], types[1]);
            default: {
                Class<?> common = types[0];
                for (int i = 1; i < types.length; i++) {
                    if (common == java.lang.Object.class) {
                        break;
                    }
                    common = calculateCommonSuperType(common, types[i]);
                }
                return common;
            }
        }
    }

    /**
     * Determines the common base type for two types passed in.
     * 
     * @param first
     *            The first type.
     * @param second
     *            The second type.
     * @return The common base class.
     */
    public static Class<?> calculateCommonSuperType(Class<?> first,
            Class<?> second) {
        if (first != null && first.isPrimitive()) {
            return calculateCommonSuperType(second, getBoxedType(first));
        } else {
            if (first == null || second == null) {
                return Object.class;
            }
            if (first.isAssignableFrom(second)) {
                return first;
            } else if (second.isAssignableFrom(first)) {
                return first;
            } else {
                return calculateCommonSuperType(second.getSuperclass(), first);
            }
        }
    }

    private static Class<?> getBoxedType(Class<?> type) {
        if (double.class == type) {
            return Double.class;
        } else if (long.class == type) {
            return Long.class;
        } else if (int.class == type) {
            return Integer.class;
        } else if (short.class == type) {
            return Short.class;
        } else if (byte.class == type) {
            return Byte.class;
        } else if (boolean.class == type) {
            return Boolean.class;
        } else if (float.class == type) {
            return Float.class;
        } else {
            throw new IllegalArgumentException("Not a primitive type: " + type.getSimpleName());
        }
    }
    
    public static Class<?> getGuaranteedBoxedVersion(Class<?> type) {
        if (type.isPrimitive()) {
            return getBoxedType(type);
        } else {
            return type;
        }
    }

}
