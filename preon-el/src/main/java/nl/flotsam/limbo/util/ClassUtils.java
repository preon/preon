/*
 * Copyright 2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package nl.flotsam.limbo.util;

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
