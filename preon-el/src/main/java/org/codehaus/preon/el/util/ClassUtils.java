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
