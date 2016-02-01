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

import org.codehaus.preon.el.DescriptionBuilder;
import org.codehaus.preon.el.ReferenceResolver;
import org.codehaus.preon.el.UnresolvableReferenceException;

public class ChainingResolver implements ReferenceResolver {
    
    private ReferenceResolver match;
    
    private String key;
    
    private ReferenceResolver alternative;
    
    public ChainingResolver(ReferenceResolver match, String key, ReferenceResolver alternative) {
        this.match = match;
        this.alternative = alternative;
        this.key = key;
    }

    public Object resolve(String... reference) throws UnresolvableReferenceException {
        if (key.equals(reference[0])) {
            return match.resolve(chop(reference));
        } else {
            return alternative.resolve(reference);
        }
    }

    public void describe(StringBuilder builder, String... reference)
            throws UnresolvableReferenceException {
        if (key.equals(reference[0])) {
            match.describe(builder);
        } else {
            alternative.describe(builder);
        }
    }

    public void describe(DescriptionBuilder builder, String... reference)
            throws UnresolvableReferenceException {
        if (key.equals(reference[0])) {
            match.describe(builder);
        } else {
            alternative.describe(builder);
        }
    }
    
    private String[] chop(String... reference) {
        String[] result = new String[reference.length - 1];
        System.arraycopy(reference, 1, result, 0, result.length);
        return result;
    }
    
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ChainingResolver[first");
        builder.append(match.toString());
        builder.append(", alternative=");
        builder.append(alternative.toString());
        builder.append("]");
        return builder.toString();
    }

}
*/