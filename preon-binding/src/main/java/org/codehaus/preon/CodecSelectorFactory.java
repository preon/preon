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
package org.codehaus.preon;

import java.util.List;

/**
 * A factory for creating {@link CodecSelector CodecSelectors}.
 *
 * @author Wilfred Springer
 */
public interface CodecSelectorFactory {

    /**
     * Creates a new {@link CodecSelector}. The {@link CodecSelector} constructed will <em>only</em> be able to resolve
     * references constructed from the {@link ResolverContext} passed in + the <code>BitBuffer</code>.
     *
     * @param context The context for creating {@link Reference} instances.
     * @param codecs  The {@link Codec Codecs} from which the {@link CodecSelector} needs to select.
     * @return The {@link CodecSelector} capable of selecting the right {@link Codec}.
     */
    CodecSelector create(ResolverContext context, List<Codec<?>> codecs)
            throws CodecConstructionException;

}
