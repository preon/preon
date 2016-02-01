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
