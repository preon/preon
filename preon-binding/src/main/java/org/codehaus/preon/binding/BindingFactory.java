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
package org.codehaus.preon.binding;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

import org.codehaus.preon.el.ReferenceContext;
import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import org.codehaus.preon.Codec;
import org.codehaus.preon.ResolverContext;
import org.codehaus.preon.buffer.BitBuffer;

/**
 * A factory for {@link Binding} instances.
 *
 * @author Wilfred Springer
 */
public interface BindingFactory {

    /**
     * Constructs a new {@link Binding}.
     *
     * @param metadata           The annotations of the field.
     * @param field              The field to bound to.
     * @param codec              The {@link Codec} to be used to decode instances of the type of object to be injected
     *                           in the {@link Field field}.
     * @param context            The {@link ReferenceContext context} for creating references.
     * @param containerReference TODO
     * @return A new {@link Binding} instance, capable of loading data from a {@link BitBuffer} into an object's field.
     */
    Binding create(AnnotatedElement metadata, Field field, Codec<?> codec,
                   ResolverContext context, Documenter<ParaContents<?>> containerReference);

}
