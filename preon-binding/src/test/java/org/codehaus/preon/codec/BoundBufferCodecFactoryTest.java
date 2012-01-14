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
