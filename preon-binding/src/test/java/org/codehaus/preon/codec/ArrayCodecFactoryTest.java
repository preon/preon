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

import org.codehaus.preon.CodecFactory;
import org.codehaus.preon.ResolverContext;
import org.codehaus.preon.annotation.BoundList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.AnnotatedElement;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArrayCodecFactoryTest {

    @Mock
    private AnnotatedElement metadata;

    @Mock
    private BoundList boundList;

    @Mock
    private CodecFactory delegate;

    @Mock
    private ResolverContext context;

    private ArrayCodecFactory factory;

    @Before
    public void configureArrayCodedFactory() {
        factory = new ArrayCodecFactory(delegate);
    }

    @Test
    public void shouldNotTryToReturnCodecInCaseSizeUndefined() {
        Object array = new byte[0];
        Class<?> type = array.getClass();
        when(metadata.getAnnotation(BoundList.class)).thenReturn(boundList);
        when(boundList.size()).thenReturn("");
        assertThat(factory.create(metadata, type, context), is(nullValue()));
    }

}
