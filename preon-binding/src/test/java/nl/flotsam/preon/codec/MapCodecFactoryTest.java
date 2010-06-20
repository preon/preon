package nl.flotsam.preon.codec;

import nl.flotsam.preon.Codec;
import nl.flotsam.preon.CodecFactory;
import nl.flotsam.preon.ResolverContext;
import nl.flotsam.preon.annotation.BoundList;
import nl.flotsam.preon.annotation.Choices;
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
