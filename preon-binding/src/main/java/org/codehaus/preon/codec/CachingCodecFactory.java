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
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.descriptor.PassThroughCodecDescriptor2;
import org.codehaus.preon.util.AnnotationUtils;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.util.*;

/**
 * An implementation of the {@link CodecFactory} interface that will prevent the same {@link Codec} from being
 * constructed twice. <p/> <p> Introduced to prevent problems when creating {@link Codec Codecs} for objects that
 * introduce a circular dependency, like the example below: </p> <p/>
 * <pre>
 * class Foo {
 * <p/>
 *     &#064;Bound
 *     private Bar value;
 * <p/>
 * }
 * <p/>
 * class Bar {
 * <p/>
 *     &#064;Bound
 *     private Foo value;
 * <p/>
 * }
 * </pre>
 * <p/> <p> Without using this {@link CodecFactory} decorator, the underlying {@link CodecFactory} might potentially
 * generate a stack overflow, creating {@link Codec Codecs} for Foo and Bar. </p> <p/> <p> Note that this class also
 * provides a convenient way to access the {@link Codec Codecs} created, which comes in handy when generating
 * documentation for all of these {@link Codec Codecs}. (See {@link #getCodecs()}.) </p>
 *
 * @author Wilfred Springer
 */
public class CachingCodecFactory implements CodecFactory {

    /**
     * A list of all {@link Codecs} already constructed, indexed by just the
     * type. (In the future, this should include the metadata as well.)
     */
    private Map<Key, CodecHolder<?>> created;

    /**
     * The object to which the actual construction of the {@link Codec} will be
     * delegated.
     */
    private CodecFactory delegate;

    /**
     * The object receiving notifications of new objects getting constructed.
     */
    private CodecConstructionListener listener = new CodecConstructionListener() {

        public void constructed(Codec<?> codec) {
        }

    };

    /**
     * Constructs a new instance, accepting the {@link CodecFactory} to which
     * this factory should delegate if it not already constructed the required
     * {@link Codec} before.
     *
     * @param delegate
     *            The {@link CodecFactory} to which this factory should delegate
     *            if it not already constructed the required {@link Codec}
     *            before.
     */
    public CachingCodecFactory(CodecFactory delegate) {
        created = new HashMap<Key, CodecHolder<?>>();
        this.delegate = delegate;
    }

    public CachingCodecFactory(CodecFactory delegate,
                               CodecConstructionListener listener) {
        this(delegate);
        if (listener == null) {
            throw new IllegalArgumentException("Null not allowed for listener.");
        }
        this.listener = listener;
    }

    // JavaDoc inherited

    @SuppressWarnings("unchecked")
    public <T> Codec<T> create(AnnotatedElement metadata, Class<T> type,
                               ResolverContext context) {
        Key key = new Key(metadata, type, context);
        Codec<T> result = (Codec<T>) created.get(key);
        if (result == null) {
            CodecHolder<T> holder = new CodecHolder<T>(type);
            created.put(key, holder);
            result = delegate.create(metadata, type, context);
            if (result == null) {
                return null;
            } else {
                listener.constructed(result);
                holder.set(result);
                return result;
            }
        } else {
            if (result instanceof CodecHolder) {
                CodecHolder<T> holder = (CodecHolder<T>) result;
                if (holder.get() == null) {
                    return null;
                }
            }
            return result;
        }
    }

    /**
     * Returns the {@link Codec Codecs} created by this factory
     *
     * @return A {@link List} of {@link Codec Codecs} created by this factory.
     */
    public List<Codec<?>> getCodecs() {
        return Collections.unmodifiableList(new ArrayList<Codec<?>>(created
                .values()));
    }

    private static class CodecHolder<T> implements Codec<T> {

        private Codec<T> codec;

        private Class<T> type;

        public CodecHolder(Class<T> type) {
            this.type = type;
        }

        public T decode(BitBuffer buffer, Resolver resolver, Builder builder)
                throws DecodingException {
            return codec.decode(buffer, resolver, builder);
        }

        public void encode(T value, BitChannel channel, Resolver resolver) throws IOException {
            codec.encode(value, channel, resolver);
        }

        public Class<?>[] getTypes() {
            return new Class<?>[]{type};
        }

        public void set(Codec<T> codec) {
            this.codec = codec;
        }

        public Codec<T> get() {
            return codec;
        }

        public Expression<Integer, Resolver> getSize() {
            return codec.getSize();
        }

        public Class<?> getType() {
            return codec.getType();
        }

        public CodecDescriptor getCodecDescriptor() {
            return new PassThroughCodecDescriptor2(codec.getCodecDescriptor(),
                    codec.getCodecDescriptor().requiresDedicatedSection());
        }

    }

    private static class Key {

        private AnnotatedElement metadata;

        private Class<?> type;

        private ResolverContext context;

        public Key(AnnotatedElement metadata, Class<?> type,
                   ResolverContext context) {
            this.metadata = metadata;
            this.type = type;
            this.context = context;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Key)) {
                return false;
            } else {
                Key key = (Key) obj;
                // TODO: Add ResolverContext
                return ((metadata == null && key.metadata == null) || AnnotationUtils
                        .equivalent(metadata, key.metadata))
                        && ((type == null && key.type == null) || type
                        .equals(key.type));
            }
        }

        @Override
        public int hashCode() {
            // TODO: Add ResolverContext
            int result = 7;
            result = result
                    * 31
                    + (metadata == null ? 0 : AnnotationUtils
                    .calculateHashCode(metadata));
            result = result * 31 + (type == null ? 0 : type.hashCode());
            return result;
        }

    }

}
