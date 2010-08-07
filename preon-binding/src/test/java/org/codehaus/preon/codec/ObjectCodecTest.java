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

import org.codehaus.preon.Resolver;
import org.codehaus.preon.binding.Binding;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.el.ObjectResolverContext;
import org.codehaus.preon.rendering.IdentifierRewriter;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.List;
import java.io.IOException;

@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class ObjectCodecTest {

    @Mock
    private ObjectResolverContext context;

    @Mock
    private IdentifierRewriter rewriter;

    @Mock
    private Binding binding1;

    @Mock
    private Binding binding2;

    @Mock
    private BitChannel channel;

    @Mock
    private Resolver resolver;

    private List<Binding> listOfBindings;

    @Before
    public void prepareListOfBindings() {
        listOfBindings = Arrays.asList(binding1, binding2);
    }

    @org.junit.Test
    public void shouldEncodeAllFields() throws IOException {
        ObjectCodec<Test> codec = new ObjectCodec<Test>(Test.class, rewriter, context);
        Test value = new Test();
        when(context.getBindings()).thenReturn(listOfBindings);
        codec.encode(value, channel, resolver);
        verify(binding1).save(value, channel, resolver);
        verify(binding2).save(value, channel, resolver);
        verifyNoMoreInteractions(binding1, binding2);
    }

    private static class Test {


    }

}
