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
package org.codehaus.preon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.codehaus.preon.buffer.BitBuffer;


/**
 * A simple annotation for marking particular fields to be optional, depending on the condition. The condition is based
 * on the Limbo notation. Variables are expected to be resolved relatively to the object holding the annotated field.
 * <p/> <p> Here is an example snippet: </p>
 * <p/>
 * <pre>
 * private int databaseVersion;
 *
 * @author Wilfred Springer
 * @If(&quot;databaseVersion &gt; 700&quot;)
 * @Bound private int foobar; </pre> <p/> <p> In the above case, <code>foobar</code> is only expected to be read from
 * the {@link BitBuffer} if the condition holds. If <code>databaseVersion</code> is 300, it will be skipped. </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface If {

    /**
     * The expression to be evaluated.
     *
     * @return The expression to be evaluated.
     */
    String value();

}
