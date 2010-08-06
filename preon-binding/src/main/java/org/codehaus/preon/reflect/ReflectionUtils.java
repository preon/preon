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
package org.codehaus.preon.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * A utility class, providing a couple of convenience operations for dealing with reflection. (Adding Plexus Reflector
 * or Spring ReflectionUtils is considered to be overhead.)
 *
 * @author Wilfred Springer
 */
public class ReflectionUtils {

    /**
     * Make the field accessible, if it not already is accessible
     *
     * @param field The field that needs to be made accessible.
     */
    public static void makeAssessible(Field field) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * Obtains the field value from the object passed in. {@link IllegalAccessException} will be caught and rethrown as
     * a runtime exception.
     *
     * @throws RuntimeIllegalAccessException If accessing the field passed in is not permitted.
     */
    public static Object getValue(Field field, Object object)
            throws RuntimeIllegalAccessException {
        try {
            return field.get(object);
        } catch (IllegalAccessException iae) {
            throw new RuntimeIllegalAccessException(iae);
        }
    }

    /**
     * Obtains the field value from the object passed in as an integer. {@link IllegalAccessException} will be caught
     * and rethrown as a runtime exception.
     *
     * @throws RuntimeIllegalAccessException If accessing the field passed in is not permitted.
     */
    public static int getValueAsInt(Field field, Object object)
            throws RuntimeIllegalAccessException {
        try {
            return field.getInt(object);
        } catch (IllegalAccessException iae) {
            throw new RuntimeIllegalAccessException(iae);
        }
    }

    /**
     * Obtains the field value from the object passed in as an integer. {@link IllegalAccessException} will be caught
     * and rethrown as a runtime exception.
     *
     * @param name The name of the field.
     * @throws RuntimeIllegalAccessException If accessing the field passed in is not permitted.
     */
    public static int getValueAsInt(String name, Object object)
            throws RuntimeIllegalAccessException {
        try {
            return getField(object.getClass(), name).getInt(object);
        } catch (IllegalAccessException iae) {
            throw new RuntimeIllegalAccessException(iae);
        }
    }

    /**
     * Obtains the field value from the object passed in. {@link IllegalAccessException} will be caught and rethrown as
     * a runtime exception.
     *
     * @throws RuntimeIllegalAccessException If accessing the field passed in is not permitted.
     */
    public static void setValue(Field field, Object object, Object value)
            throws RuntimeIllegalAccessException {
        try {
            field.set(object, value);
        } catch (IllegalAccessException iae) {
            throw new RuntimeIllegalAccessException(iae);
        }
    }

    /**
     * Creates a new instance of the class passed in, using the default constructor.
     *
     * @return A new instance of the class passed in.
     */
    public static <T> T create(Class<T> cl) {
        try {
            return (T) cl.newInstance();
        } catch (InstantiationException ie) {
            throw new RuntimeInstantiationException(ie);
        } catch (IllegalAccessException iae) {
            throw new RuntimeIllegalAccessException(iae);
        }
    }

    /**
     * Creates a new instance of the class passed in, using the default constructor passed in.
     *
     * @param constructor The constructor of the class.
     * @return A new instance of the class passed in.
     */
    public static <T> T create(Constructor<T> constructor) {
        try {
            return (T) constructor.newInstance();
        } catch (InstantiationException ie) {
            throw new RuntimeInstantiationException(ie);
        } catch (IllegalAccessException iae) {
            throw new RuntimeIllegalAccessException(iae);
        } catch (InvocationTargetException ite) {
            throw new RuntimeInvocationTargetException(ite);
        }
    }

    /**
     * Creates a new instance of the class passed in, using the default constructor passed in.
     *
     * @param constructor The constructor of the class.
     * @param args        The arguments to be passed to the constructor.
     * @return A new instance of the class passed in.
     */
    public static <T> T create(Constructor<T> constructor, Object[] args) {
        try {
            return (T) constructor.newInstance(args);
        } catch (InstantiationException ie) {
            throw new RuntimeInstantiationException(ie);
        } catch (IllegalAccessException iae) {
            throw new RuntimeIllegalAccessException(iae);
        } catch (InvocationTargetException ite) {
            throw new RuntimeInvocationTargetException(ite);
        }
    }

    /**
     * Returns the {@link Field} for a field with the given name on the given class.
     *
     * @param cl   The class for which we need the field.
     * @param name The name of the field.
     * @return A new {@link Field} instanc.e
     */
    public static Field getField(Class cl, String name) {
        try {
            return cl.getDeclaredField(name);
        } catch (SecurityException se) {
            throw new RuntimeSecurityException(se);
        } catch (NoSuchFieldException nse) {
            cl = cl.getSuperclass();
            if (!java.lang.Object.class.equals(cl)) {
                return getField(cl, name);
            } else {
                throw new RuntimeNoSuchFieldException(nse);
            }
        }
    }

}
