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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.preon.annotation.BoundEnumOption;

import edu.emory.mathcs.backport.java.util.Collections;

public class EnumUtils {

    @SuppressWarnings("unchecked")
    public static <T> Map<Long, T> getBoundEnumOptionIndex(Class<T> enumType) {
        if (!enumType.isEnum()) {
            return Collections.emptyMap();
        } else {
            Map<Long, T> result = new HashMap<Long, T>();
            Field[] fields = enumType.getFields();
            for (Field field : fields) {
                if (field.isEnumConstant()) {
                    try {
                        field.setAccessible(true);
                        BoundEnumOption annotation = field
                                .getAnnotation(BoundEnumOption.class);
                        if (annotation == null) {
                            result.put(null, (T) field.get(null));
                        } else {
                            result.put(annotation.value(), (T) field.get(null));
                        }
                    }
                    catch (IllegalAccessException iae) {
                        iae.printStackTrace(); // Should never happen.
                    }
                }
            }
            return result;
        }
    }

}
