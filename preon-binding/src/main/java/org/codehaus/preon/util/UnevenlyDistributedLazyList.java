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
