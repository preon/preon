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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;

import org.codehaus.preon.Codec;
import org.codehaus.preon.annotation.BoundBuffer;
import org.codehaus.preon.channel.OutputStreamBitChannel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BoundBufferCodecFactoryTest {

    @Mock
    private AnnotatedElement metadata;

    @Mock
    private BoundBuffer boundBuffer;

    private BoundBufferCodecFactory factory;

    @Before
    public void createFactory() {
        factory = new BoundBufferCodecFactory();
    }

    @Test
    public void encodedBufferShouldEqualMatchBuffer() throws IOException {
        byte[] match = { 1, 2, 3, 4 };

        when(metadata.isAnnotationPresent(BoundBuffer.class)).thenReturn(true);
        when(metadata.getAnnotation(BoundBuffer.class)).thenReturn(boundBuffer);
        when(boundBuffer.match()).thenReturn(match);

        Codec<byte[]> codec = factory.create(metadata, byte[].class, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        codec.encode(match, new OutputStreamBitChannel(out), null);

        byte[] output = out.toByteArray();
        assertThat(output.length, is(match.length));
        assertArrayEquals(match, output);
    }
}
