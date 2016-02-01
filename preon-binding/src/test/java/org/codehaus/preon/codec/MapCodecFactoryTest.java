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
