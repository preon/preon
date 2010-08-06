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

import org.codehaus.preon.Builder;
import org.codehaus.preon.Codec;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.buffer.BitBuffer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ListBasedMapCodecTest {

    @Mock
    private Codec<List<Map.Entry<String,Integer>>> listCodec;

    @Mock
    private Map.Entry<String,Integer> item1;

    @Mock
    private Map.Entry<String,Integer> item2;

    @Mock
    private BitBuffer buffer;

    @Mock
    private Resolver resolver;

    @Mock
    private Builder builder;

    @Test
    public void shouldWorkWithNormalList() throws DecodingException {
        when(listCodec.decode(any(BitBuffer.class), any(Resolver.class), any(Builder.class)))
                .thenReturn(Arrays.asList(item1, item2));
        when(item1.getKey()).thenReturn("foo");
        when(item1.getValue()).thenReturn(1);
        when(item2.getKey()).thenReturn("bar");
        when(item2.getValue()).thenReturn(2);
        ListBasedMapCodec<String,Integer> codec = new ListBasedMapCodec<String, Integer>(listCodec);
        Map<String, Integer> map = codec.decode(buffer, resolver, builder);
        assertThat(map.size(), is(2));
        assertThat(map, hasEntry("foo", 1));
        assertThat(map, hasEntry("bar", 2));
        verify(listCodec).decode(buffer, resolver, builder);
        Mockito.verifyNoMoreInteractions(listCodec);        
    }

    @Test
    public void shouldWorkWithZeroSizeList() throws DecodingException {
        when(listCodec.decode(any(BitBuffer.class), any(Resolver.class), any(Builder.class)))
                .thenReturn(Collections.<Map.Entry<String, Integer>>emptyList());
        ListBasedMapCodec<String,Integer> codec = new ListBasedMapCodec<String, Integer>(listCodec);
        Map<String, Integer> map = codec.decode(buffer, resolver, builder);
        assertThat(map.size(), is(0));
        verify(listCodec).decode(buffer, resolver, builder);
        Mockito.verifyNoMoreInteractions(listCodec);
    }

}
