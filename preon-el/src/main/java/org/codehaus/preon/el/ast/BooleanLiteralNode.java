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
package org.codehaus.preon.el.ast;

import org.codehaus.preon.el.Document;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.ReferenceContext;

import java.util.Set;

public class BooleanLiteralNode<E> extends AbstractNode<Boolean, E> {

    private boolean value;

    public BooleanLiteralNode(boolean value) {
        this.value = value;
    }
    
    public Boolean eval(E context) {
        return value;
    }

    public Class<Boolean> getType() {
        return Boolean.class;
    }

    public Node<Boolean, E> simplify() {
        return this;
    }

    public Node<Boolean, E> rescope(ReferenceContext<E> eReferenceContext) {
        return this;
    }

    public void gather(Set<Reference<E>> references) {
        // Nothing to do
    }

    public boolean isParameterized() {
        return false;
    }

    public void document(Document target) {
        target.text(Boolean.toString(value));
    }
}
