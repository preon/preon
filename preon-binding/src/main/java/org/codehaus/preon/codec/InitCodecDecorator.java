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
package org.codehaus.preon.codec;

import org.codehaus.preon.el.Expression;
import org.codehaus.preon.*;
import org.codehaus.preon.annotation.Init;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.descriptor.PassThroughCodecDescriptor2;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * A decorator that will inspect all methods on the object constructed by the {@link Codec} to be decorated, and create
 * a decorated Codec that will invoke a method after the object has been constructed, based on the {@link Init}
 * annotation.
 *
 * @author Wilfred Springer
 */
public class InitCodecDecorator implements CodecDecorator {

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.CodecDecorator#decorate(org.codehaus.preon.Codec,
     * java.lang.reflect.AnnotatedElement, java.lang.Class,
     * org.codehaus.preon.ResolverContext)
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
     * A {@link Codec}, calling the method annotated with the {@link Init} annotation on the result, once all data of
     * that result has been read.
     */
    private class InitCodec<T> implements Codec<T> {

        /** The {@link Codec} producing the result. */
        private Codec<T> codec;

        /** The method to be called. */
        private Method method;

        /**
         * Constructs a new instance, accepting the {@link Codec} producing the result, as well as the method to be
         * invoked on the result once it has been succesfully decoded.
         *
         * @param codec  The {@link Codec} producing th result.
         * @param method The method to be called once it has been successfully decoded.
         */
        public InitCodec(Codec<T> codec, Method method) {
            this.codec = codec;
            this.method = method;
        }

        /*
         * (non-Javadoc)
         * @see org.codehaus.preon.Codec#decode(org.codehaus.preon.buffer.BitBuffer, org.codehaus.preon.Resolver, org.codehaus.preon.Builder)
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

        public void encode(T value, BitChannel channel, Resolver resolver) throws IOException {
            codec.encode(value, channel, resolver);
        }

        /*
         * (non-Javadoc)
         * @see org.codehaus.preon.Codec#getTypes()
         */

        public Class<?>[] getTypes() {
            return codec.getTypes();
        }

        /*
         * (non-Javadoc)
         * @see org.codehaus.preon.Codec#getSize()
         */

        public Expression<Integer, Resolver> getSize() {
            return codec.getSize();
        }

        public Class<?> getType() {
            return codec.getType();
        }

        public CodecDescriptor getCodecDescriptor() {
            return new PassThroughCodecDescriptor2(codec.getCodecDescriptor(), true);
        }


        @Override
        public boolean equals(Object obj) {
            if (codec != null) return codec.equals(obj);
            if (this == obj) return true;
            return false;
        }

    }

}
