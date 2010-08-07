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

public class Converters {

    private static ByteToIntegerConverter byteToInteger = new ByteToIntegerConverter();
    private static ShortToIntegerConverter shortToInteger = new ShortToIntegerConverter();
    private static LongToIntegerConverter longToInteger = new LongToIntegerConverter();

    public static <T, V> Converter<T, V> get(Class<T> from, Class<V> to) {
        if (to == Integer.class || to == int.class) {
            if (from == Byte.class || from == byte.class) {
                return (Converter<T, V>) byteToInteger;
            } else if (from == Short.class || from == short.class) {
                return (Converter<T, V>) shortToInteger;
            } else if (from == Long.class || from == long.class) {
                return (Converter<T, V>) longToInteger;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static <T> int toInt(T value) {
        Converter<T, Integer> converter = (Converter<T, Integer>) get(value.getClass(),
                Integer.class);
        return converter.convert(value);
    }

    private static class ByteToIntegerConverter implements Converter<Byte, Integer> {

        public Integer convert(Byte instance) {
            return instance.intValue();
        }

        public Class<Integer> getTargetType() {
            return Integer.class;
        }

    }

    private static class ShortToIntegerConverter implements Converter<Short, Integer> {

        public Integer convert(Short instance) {
            return instance.intValue();
        }

        public Class<Integer> getTargetType() {
            return Integer.class;
        }

    }

    private static class LongToIntegerConverter implements Converter<Long, Integer> {

        public Integer convert(Long instance) {
            return instance.intValue();
        }

        public Class<Integer> getTargetType() {
            return Integer.class;
        }

    }

}
