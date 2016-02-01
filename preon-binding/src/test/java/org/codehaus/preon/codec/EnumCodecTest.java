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
import org.codehaus.preon.Resolver;

import static org.codehaus.preon.buffer.ByteOrder.BigEndian;

import org.codehaus.preon.channel.BitChannel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class EnumCodecTest {

    @Mock
    private Expression<Integer, Resolver> size;

    private final Map<Long, Direction> map;

    @Mock
    private BitChannel channel;

    @Mock
    private Resolver resolver;

    public EnumCodecTest() {
        this.map = new HashMap<Long, Direction>();
        map.put(1L, Direction.Forward);
        map.put(2L, Direction.Backward);
    }

    @Test
    public void shouldEncodeCorrectly() throws IOException {
        int nrbits = 2;
        EnumCodec<Direction> codec = new EnumCodec<Direction>(Direction.class, map, size, BigEndian);
        when(size.eval(org.mockito.Matchers.any(Resolver.class))).thenReturn(nrbits);
        codec.encode(Direction.Backward, channel, resolver);
        verify(channel).write(nrbits, 2L, BigEndian);
        verifyNoMoreInteractions(channel);
        verifyNoMoreInteractions(resolver);
    }

    public enum Direction {
        Forward,
        Backward
    }

}
