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
