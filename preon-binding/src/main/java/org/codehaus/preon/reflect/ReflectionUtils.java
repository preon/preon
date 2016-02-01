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
