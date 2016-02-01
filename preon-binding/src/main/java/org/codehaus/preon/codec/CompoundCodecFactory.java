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

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.preon.Codec;
import org.codehaus.preon.CodecFactory;
import org.codehaus.preon.ResolverContext;


/**
 * A {@link CodecFactory} capable of generating {@link Codec Codecs} by delegating to a sorted list of other {@link
 * CodecFactory CodecFactories}.
 *
 * @author Wilfred Springer
 */
public class CompoundCodecFactory implements CodecFactory {

    /** The sorted list of {@link CodecFactory CodecFactories} to which this {@link CodecFactory} will delegate. */
    private final List<CodecFactory> factories = new ArrayList<CodecFactory>();

    public <T> Codec<T> create(AnnotatedElement overrides, Class<T> type,
                               ResolverContext context) {
        Codec<T> result = null;
        for (CodecFactory delegate : factories) {
            result = delegate.create(overrides, type, context);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public void add(CodecFactory factory) {
        factories.add(factory);
    }

    public void remove(CodecFactory factory) {
        factories.remove(factory);
    }

}
