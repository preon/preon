/*
 * Copyright (C) 2008 Wilfred Springer
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
 * Preon; see the file COPYING. If not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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

package nl.flotsam.preon.codec;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import nl.flotsam.limbo.Expression;
import nl.flotsam.preon.Builder;
import nl.flotsam.preon.Codec;
import nl.flotsam.preon.CodecDecorator;
import nl.flotsam.preon.CodecDescriptor;
import nl.flotsam.preon.DecodingException;
import nl.flotsam.preon.Resolver;
import nl.flotsam.preon.ResolverContext;
import nl.flotsam.preon.annotation.Init;
import nl.flotsam.preon.buffer.BitBuffer;

/**
 * A decorator that will inspect all methods on the object constructed by the
 * {@link Codec} to be decorated, and create a decorated Codec that will invoke
 * a method after the object has been constructed, based on the {@link Init}
 * annotation.
 * 
 * @author Wilfred Springer
 * 
 */
public class InitCodecDecorator implements CodecDecorator {

    /*
     * (non-Javadoc)
     * 
     * @see nl.flotsam.preon.CodecDecorator#decorate(nl.flotsam.preon.Codec,
     * java.lang.reflect.AnnotatedElement, java.lang.Class,
     * nl.flotsam.preon.ResolverContext)
     */
    public <T> Codec<T> decorate(Codec<T> decorated, AnnotatedElement metadata, Class<T> type,
            ResolverContext context) {
        for (Method method : type.getMethods()) {
            if (!Modifier.isStatic(method.getModifiers()) && method.getParameterTypes().length == 0
                    && method.isAnnotationPresent(Init.class)) {
                method.setAccessible(true);
                return new InitCodec<T>(decorated, method);
            }
        }
        return decorated;
    }

    /**
     * A {@link Codec}, calling the method annotated with the {@link Init}
     * annotation on the result, once all data of that result has been read.
     */
    private class InitCodec<T> implements Codec<T> {

        /**
         * The {@link Codec} producing the result.
         */
        private Codec<T> codec;

        /**
         * The method to be called.
         */
        private Method method;

        /**
         * Constructs a new instance, accepting the {@link Codec} producing the
         * result, as well as the method to be invoked on the result once it has
         * been succesfully decoded.
         * 
         * @param codec
         *            The {@link Codec} producing th result.
         * @param method
         *            The method to be called once it has been successfully
         *            decoded.
         */
        public InitCodec(Codec<T> codec, Method method) {
            this.codec = codec;
            this.method = method;
        }

        /*
         * (non-Javadoc)
         * @see nl.flotsam.preon.Codec#decode(nl.flotsam.preon.buffer.BitBuffer, nl.flotsam.preon.Resolver, nl.flotsam.preon.Builder)
         */
        public T decode(BitBuffer buffer, Resolver resolver, Builder builder)
                throws DecodingException {
            T result = codec.decode(buffer, resolver, builder);
            if (result != null) {
                try {
                    method.invoke(result);
                } catch (IllegalArgumentException e) {
                    throw new DecodingException("Failed to invoke init method.");
                } catch (IllegalAccessException e) {
                    throw new DecodingException("Failed to invoke init method.");
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    throw new DecodingException("Failed to invoke init method.");
                }
            }
            return result;
        }

        /*
         * (non-Javadoc)
         * @see nl.flotsam.preon.Codec#getCodecDescriptor()
         */
        public CodecDescriptor getCodecDescriptor() {
            return codec.getCodecDescriptor();
        }

        /*
         * (non-Javadoc)
         * @see nl.flotsam.preon.Codec#getSize(nl.flotsam.preon.Resolver)
         */
        public int getSize(Resolver resolver) {
            return codec.getSize(resolver);
        }

        /*
         * (non-Javadoc)
         * @see nl.flotsam.preon.Codec#getTypes()
         */
        public Class<?>[] getTypes() {
            return codec.getTypes();
        }

        /*
         * (non-Javadoc)
         * @see nl.flotsam.preon.Codec#getSize()
         */
        public Expression<Integer, Resolver> getSize() {
            return codec.getSize();
        }

        public Class<?> getType() {
            return codec.getType();
        }

    }

}
