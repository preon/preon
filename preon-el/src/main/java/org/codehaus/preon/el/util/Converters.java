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
