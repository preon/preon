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
package org.codehaus.preon.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.codehaus.preon.Builder;
import org.codehaus.preon.Codec;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.buffer.BitBuffer;

/**
 * A {@link List} implementation that will lazy load its elements. Big difference with {@link EvenlyDistributedLazyList}
 * is that this implementation does not assume all elements to have the same size.
 *
 * @author Wilfred Springer
 */
public class UnevenlyDistributedLazyList<E> implements List<E> {

    /** The {@link Codec} decoding elements of the list. */
    private Codec<E> codec;

    /** The {@link BitBuffer} to read from. */
    private BitBuffer buffer;

    /**
     *
     */
    private Resolver resolver;

    private Builder builder;

    public boolean add(E element) {
        // TODO Auto-generated method stub
        return false;
    }

    public void add(int position, E element) {
        // TODO Auto-generated method stub

    }

    public boolean addAll(Collection<? extends E> elements) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean addAll(int position, Collection<? extends E> elements) {
        // TODO Auto-generated method stub
        return false;
    }

    public void clear() {
        // TODO Auto-generated method stub

    }

    public boolean contains(Object object) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean containsAll(Collection<?> elements) {
        // TODO Auto-generated method stub
        return false;
    }

    public E get(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public int indexOf(Object object) {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    public Iterator<E> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    public int lastIndexOf(Object object) {
        // TODO Auto-generated method stub
        return 0;
    }

    public ListIterator<E> listIterator() {
        // TODO Auto-generated method stub
        return null;
    }

    public ListIterator<E> listIterator(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean remove(Object object) {
        // TODO Auto-generated method stub
        return false;
    }

    public E remove(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean removeAll(Collection<?> elements) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean retainAll(Collection<?> elements) {
        // TODO Auto-generated method stub
        return false;
    }

    public E set(int position, E element) {
        // TODO Auto-generated method stub
        return null;
    }

    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }

    public List<E> subList(int start, int end) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object[] toArray() {
        // TODO Auto-generated method stub
        return null;
    }

    public <T> T[] toArray(T[] elements) {
        // TODO Auto-generated method stub
        return null;
    }

    private interface Holder<T> {

        T get() throws InterruptedException;

    }

}
