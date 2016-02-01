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
package org.codehaus.preon.util;


/*package org.codehaus.preon.util;

import java.util.Arrays;

import org.codehaus.preon.el.DescriptionBuilder;
import org.codehaus.preon.el.ReferenceResolver;
import org.codehaus.preon.el.UnresolvableReferenceException;

public class ContextualReferenceResolver<T> implements ReferenceResolver {

    private RelativeReferenceResolver<T> delegate;

    private ContextResolver<T> context;

    public ContextualReferenceResolver(RelativeReferenceResolver<T> delegate,
            ContextResolver<T> context) {
        this.delegate = delegate;
        this.context = context;
    }

    public Object resolve(String... reference)
            throws UnresolvableReferenceException {
        return delegate.resolve(context.getContext(), reference);
    }

    public void describe(StringBuilder builder, String... reference)
            throws UnresolvableReferenceException {
        delegate.describe(builder);
    }

    public void describe(DescriptionBuilder target, String... path)
            throws UnresolvableReferenceException {
        delegate.describe(target);
    }
    
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ContextualReferenceResolver[context=");
        builder.append(context.toString());
        builder.append(",delegate=");
        builder.append(delegate.toString());
        builder.append("]");
        return builder.toString();
    }

}
*/