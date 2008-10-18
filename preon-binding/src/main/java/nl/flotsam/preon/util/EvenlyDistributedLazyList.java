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

package nl.flotsam.preon.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import nl.flotsam.preon.Builder;
import nl.flotsam.preon.Codec;
import nl.flotsam.preon.CodecException;
import nl.flotsam.preon.DecodingException;
import nl.flotsam.preon.Resolver;
import nl.flotsam.preon.buffer.BitBuffer;


/**
 * A {@link List} that will lazily load objects from a {@link BitBuffer}. Note
 * that it does <em>not</em> cache the elements in any way. Since the objects
 * will be instantiated on the fly, different threads will return instances with
 * a different object identity.
 * 
 * @author Wilfred Springer
 * 
 * @param <E>
 *            The type of elements in the {@link List}.
 */
public class EvenlyDistributedLazyList<E> implements List<E> {

    private CodecExceptionPolicy<E> policy;

    private Codec<E> codec;

    private long offset;

    private BitBuffer buffer;

    private int maxSize;

    private int elementSize;

    private Resolver resolver;

    private Builder builder;

    /**
     * Constructs a new instance. Currently the preferred way of constructing a
     * {@link EvenlyDistributedLazyList}.
     * 
     * @param codec
     * @param offset
     * @param buffer
     * @param maxSize
     * @param builder
     *            TODO
     */
    public EvenlyDistributedLazyList(Codec<E> codec, long offset, BitBuffer buffer, int maxSize,
            Builder builder, Resolver resolver) {
        this.codec = codec;
        this.offset = offset;
        this.buffer = buffer;
        this.builder = builder;
        this.maxSize = maxSize;
        this.resolver = resolver;
        this.elementSize = codec.getSize(resolver);
        this.policy = new CodecExceptionPolicy<E>() {

            public E handle(CodecException ce) {
                // There is really no way to be prepared for this.
                throw new RuntimeException(ce);
            }

        };
    }

    public boolean add(E o) {
        throw new UnsupportedOperationException();
    }

    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        if (index < 0 || index >= maxSize) {
            throw new IndexOutOfBoundsException();
        }
        buffer.setBitPos(offset + index * elementSize);
        try {
            return codec.decode(buffer, resolver, builder);
        } catch (DecodingException de) {
            return policy.handle(de);
        }
    }

    public int indexOf(Object o) {
        // Requires full table scan. Way to expensive.
        throw new UnsupportedOperationException();
    }

    public boolean isEmpty() {
        return maxSize != 0;
    }

    public Iterator<E> iterator() {
        return new LazyListIterator();
    }

    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    public ListIterator<E> listIterator() {
        return new LazyListIterator();
    }

    public ListIterator<E> listIterator(int index) {
        return new LazyListIterator(index);
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    public E remove(int index) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    public int size() {
        return maxSize;
    }

    public List<E> subList(int fromIndex, int toIndex) {
        return new EvenlyDistributedLazyList<E>(codec, offset + elementSize * fromIndex, buffer,
                toIndex - fromIndex, builder, resolver);
    }

    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    private class LazyListIterator implements ListIterator<E> {

        private int position = -1;

        public LazyListIterator() {
        }

        public LazyListIterator(int position) {
            this.position = position;
        }

        public void add(E o) {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext() {
            return position < maxSize - 1;
        }

        public boolean hasPrevious() {
            return position > 0;
        }

        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            } else {
                return get(++position);
            }
        }

        public int nextIndex() {
            return position + 1;
        }

        public E previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            } else {
                return get(--position);
            }
        }

        public int previousIndex() {
            return position - 1;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void set(E o) {
            throw new UnsupportedOperationException();
        }

    }

    public interface CodecExceptionPolicy<E> {

        E handle(CodecException ce);

    }

}