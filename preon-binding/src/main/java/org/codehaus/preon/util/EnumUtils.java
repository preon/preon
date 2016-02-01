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
