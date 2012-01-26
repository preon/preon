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
