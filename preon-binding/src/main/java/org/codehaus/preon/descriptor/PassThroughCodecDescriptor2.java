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
package org.codehaus.preon.descriptor;

import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import org.codehaus.preon.CodecDescriptor;

public class PassThroughCodecDescriptor2 implements CodecDescriptor {

    private CodecDescriptor delegate;
    private boolean requiresDedicatedSection;

    public PassThroughCodecDescriptor2(CodecDescriptor delegate, boolean requiresDedicatedSection) {
        this.delegate = delegate;
        this.requiresDedicatedSection = requiresDedicatedSection;
    }

    public <C extends SimpleContents<?>> Documenter<C> details(String bufferReference) {
        return delegate.details(bufferReference);
    }

    public String getTitle() {
        return delegate.getTitle();
    }

    public <C extends ParaContents<?>> Documenter<C> reference(
            Adjective adjective, boolean startWithCapital) {
        return delegate.reference(adjective, false);
    }

    public boolean requiresDedicatedSection() {
        return requiresDedicatedSection;
    }

    public <C extends ParaContents<?>> Documenter<C> summary() {
        return delegate.summary();
    }

}
