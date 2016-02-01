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

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.codehaus.preon.el.Expression;
import org.codehaus.preon.*;
import org.codehaus.preon.annotation.LazyLoading;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.descriptor.PassThroughCodecDescriptor2;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/**
 * An attempt to create a general purpose {@link CodecFactory} whose {@link Codec Codecs} will only load their data once
 * operations are invoked upon them. <p/> <p> Note that there are some concurrency issues with this approach. The {@link
 * Codec} will create a proxy. When an operation is invoked on that proxy, the proxy will check if it already obtained
 * the actual value. This is where it's getting nasty. Multiple threads might come in simultaneously. </p>
 *
 * @author Wilfred Springer
 */
public class LazyLoadingCodecDecorator implements CodecDecorator {

    public <T> Codec<T> decorate(Codec<T> decorated, AnnotatedElement metadata,
                                 Class<T> type, ResolverContext context) {
        if (metadata != null && metadata.isAnnotationPresent(LazyLoading.class)) {
            return new LazyLoadingCodec<T>(decorated, type);
        } else {
            return decorated;
        }
    }

    /**
     * A {@link Codec} that will only start loading the data when one of the
     * methods of that object are invoked.
     *
     * @author Wilfred Springer (wis)
     *
     * @param <T>
     */
    public static class LazyLoadingCodec<T> implements Codec<T> {

        /**
         * The {@link Codec} to use.
         */
        private Codec<T> wrapped;

        /**
         * The type of object created.
         */
        private Class<T> type;

        /**
         * Constructs a new instance.
         *
         * @param wrapped
         *            The {@link Codec} to use when loading the data.
         * @param type
         *            The type of object that will be returned.
         */
        public LazyLoadingCodec(Codec<T> wrapped, Class<T> type) {
            this.wrapped = wrapped;
            this.type = type;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.codehaus.preon.Codec#decode(org.codehaus.preon.buffer.BitBuffer,
         * org.codehaus.preon.Resolver, org.codehaus.preon.Builder)
         */

        @SuppressWarnings("unchecked")
        public T decode(final BitBuffer buffer, final Resolver resolver,
                        final Builder builder) throws DecodingException {
            final int size = wrapped.getSize().eval(resolver);
            final long pos = buffer.getBitPos();
            ClassLoader loader = this.getClass().getClassLoader();
            Enhancer enhancer = new Enhancer();
            enhancer.setClassLoader(loader);
            enhancer.setSuperclass(type);
            enhancer.setCallback(new MethodInterceptor() {

                private Object actual;

                public Object intercept(Object target, Method method,
                                        Object[] args, MethodProxy proxy) throws Throwable {
                    if (actual == null) {
                        buffer.setBitPos(pos);
                        actual = wrapped.decode(buffer, resolver, builder);
                    }
                    return proxy.invoke(actual, args);
                }
            });
            buffer.setBitPos(pos + size);
            return (T) enhancer.create();
        }

        public void encode(T value, BitChannel channel, Resolver resolver) throws IOException {
            wrapped.encode(value, channel, resolver);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.codehaus.preon.Codec#getTypes()
         */

        public Class<?>[] getTypes() {
            return wrapped.getTypes();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.codehaus.preon.Codec#getSize()
         */

        public Expression<Integer, Resolver> getSize() {
            return wrapped.getSize();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.codehaus.preon.Codec#getType()
         */

        public Class<?> getType() {
            return type;
        }

        public CodecDescriptor getCodecDescriptor() {
            return new PassThroughCodecDescriptor2(wrapped
                    .getCodecDescriptor(), false);
        }

    }

}
