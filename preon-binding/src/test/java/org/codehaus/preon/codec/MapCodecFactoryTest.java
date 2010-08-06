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

import org.codehaus.preon.Codec;
import org.codehaus.preon.CodecFactory;
import org.codehaus.preon.ResolverContext;
import org.codehaus.preon.annotation.BoundList;
import org.codehaus.preon.annotation.Choices;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MapCodecFactoryTest {

    @Mock
    private AnnotatedElement annotations;

    @Mock
    private BoundList boundList;

    @Mock
    private CodecFactory delegate;

    @Mock
    private Codec<List> codec;

    @Mock
    private ResolverContext context;

    @Mock
    private Choices choices;

    @Mock
    private Choices.Choice firstChoice;

    @Mock
    private Choices.Choice secondChoice;
    
    @Test
    public void shouldReturnCodecForObjectAnnotatedWithSingleType() {
        when(annotations.getAnnotation(BoundList.class)).thenReturn(boundList);
        when(delegate.create(annotations, List.class, context)).thenReturn(codec);
        when(boundList.type()).thenReturn((Class) Map.Entry.class);

        MapCodecFactory factory = new MapCodecFactory(delegate);
        Codec<Map> mapCodec = factory.create(annotations, Map.class, context);
        assertThat(mapCodec, is(not(nullValue())));

        verify(delegate).create(annotations, List.class, context);
        verify(annotations).getAnnotation(BoundList.class);
        verifyNoMoreInteractions(annotations, delegate, codec);
    }

    @Test
    public void shouldReturnCodecForObjectAnnotatedWithMultipleTypes() {
        when(annotations.getAnnotation(BoundList.class)).thenReturn(boundList);
        when(delegate.create(annotations, List.class, context)).thenReturn(codec);
        when(boundList.types()).thenReturn(new Class<?>[] {
                Map.Entry.class
        });

        MapCodecFactory factory = new MapCodecFactory(delegate);
        Codec<Map> mapCodec = factory.create(annotations, Map.class, context);
        assertThat(mapCodec, is(not(nullValue())));

        verify(delegate).create(annotations, List.class, context);
        verify(annotations).getAnnotation(BoundList.class);
        verifyNoMoreInteractions(annotations, delegate, codec);
    }

    @Test
    public void shouldReturnCodecForObjectAnnotatedWithChoices() {
        when(annotations.getAnnotation(BoundList.class)).thenReturn(boundList);
        when(delegate.create(annotations, List.class, context)).thenReturn(codec);
        when(boundList.selectFrom()).thenReturn(choices);
        when(choices.alternatives()).thenReturn(new Choices.Choice[] { firstChoice, secondChoice });
        when(firstChoice.type()).thenReturn((Class) Map.Entry.class);
        when(secondChoice.type()).thenReturn((Class) Map.Entry.class);

        MapCodecFactory factory = new MapCodecFactory(delegate);
        Codec<Map> mapCodec = factory.create(annotations, Map.class, context);
        assertThat(mapCodec, is(not(nullValue())));

        verify(delegate).create(annotations, List.class, context);
        verify(annotations).getAnnotation(BoundList.class);
        verifyNoMoreInteractions(annotations, delegate, codec);
    }

    @Test
    public void shouldNotReturnCodecForObjectAnnotatedWithChoicesOneOfWhichNotEntry() {
        when(annotations.getAnnotation(BoundList.class)).thenReturn(boundList);
        when(boundList.selectFrom()).thenReturn(choices);
        when(choices.alternatives()).thenReturn(new Choices.Choice[] { firstChoice, secondChoice });
        when(firstChoice.type()).thenReturn((Class) Map.Entry.class);
        when(secondChoice.type()).thenReturn((Class) String.class);

        MapCodecFactory factory = new MapCodecFactory(delegate);
        Codec<Map> mapCodec = factory.create(annotations, Map.class, context);
        assertThat(mapCodec, is(nullValue()));

        verify(annotations).getAnnotation(BoundList.class);
        verifyNoMoreInteractions(annotations, delegate, codec);
    }

}
