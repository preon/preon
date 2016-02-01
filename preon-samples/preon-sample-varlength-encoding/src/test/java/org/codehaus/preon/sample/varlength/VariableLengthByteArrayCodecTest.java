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
package org.codehaus.preon.sample.varlength;

import org.codehaus.preon.Builder;
import org.codehaus.preon.Codec;
import org.codehaus.preon.Codecs;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.buffer.BitBuffer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class VariableLengthByteArrayCodecTest {

    @Mock
    private BitBuffer buffer;

    @Mock
    private Resolver resolver;

    @Mock
    private Builder builder;

    @Test
    public void shouldDecodeOneByte() throws DecodingException {
        VariableLengthByteArrayCodec codec = new VariableLengthByteArrayCodec();
        when(buffer.readAsByte(8)).thenReturn((byte) 0x0f);
        byte[] decoded = codec.decode(buffer, resolver, builder);
        assertThat(decoded, is(not(nullValue())));
        assertThat(decoded.length, is(1));
        verify(buffer).readAsByte(8);
        verifyNoMoreInteractions(buffer, resolver, builder);
    }

    @Test
    public void shouldDecodeMultipleBytes() throws DecodingException {
        VariableLengthByteArrayCodec codec = new VariableLengthByteArrayCodec();
        when(buffer.readAsByte(8)).thenReturn((byte) 0xff).thenReturn((byte) 0x0f);
        byte[] decoded = codec.decode(buffer, resolver, builder);
        assertThat(decoded, is(not(nullValue())));
        assertThat(decoded.length, is(2));
        verify(buffer, times(2)).readAsByte(8);
        verifyNoMoreInteractions(buffer, resolver, builder);
    }

    @Test
    public void shouldDecodeSomeHolder() throws DecodingException {
        Codec<SomeHolder> codec = Codecs.create(SomeHolder.class, new VariableLengthByteArrayCodecFactory());
        SomeHolder holder = Codecs.decode(codec, (byte) 0xff, (byte) 0x0f);
        assertThat(holder.getValue(), is(not(nullValue())));
        assertThat(holder.getValue().length, is(2));
        assertThat(holder.getValue()[0], is((byte) 0xff));
        assertThat(holder.getValue()[1], is((byte) 0x0f));
        Codecs.document(codec, Codecs.DocumentType.Html, System.err);
    }

    public static class SomeHolder {

        @VarLengthEncoded byte[] value;

        public byte[] getValue() {
            return value;
        }

    }

}
