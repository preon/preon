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
