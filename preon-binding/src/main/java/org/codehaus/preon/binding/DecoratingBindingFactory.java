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

import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import org.codehaus.preon.Codec;
import org.codehaus.preon.ResolverContext;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

public class DecoratingBindingFactory implements BindingFactory {

    private final BindingFactory bindingFactory;
    private final BindingDecorator[] bindingDecorators;

    public DecoratingBindingFactory(BindingFactory bindingFactory, BindingDecorator[] bindingDecorators) {
        this.bindingDecorators = bindingDecorators;
        this.bindingFactory = bindingFactory;
    }

    public Binding create(AnnotatedElement metadata, Field field, Codec<?> codec, ResolverContext context, Documenter<ParaContents<?>> containerReference) {
        Binding binding = bindingFactory.create(metadata, field, codec, context, containerReference);
        if (binding != null) {
            for (BindingDecorator decorator : bindingDecorators) {
                binding = decorator.decorate(binding);
            }
        }
        return binding;
    }
}
