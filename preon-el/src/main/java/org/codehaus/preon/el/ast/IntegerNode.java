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

import java.util.Set;

import org.codehaus.preon.el.Document;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.ReferenceContext;

/**
 * A node representing an integer literal.
 * 
 * @author Wilfred Springer
 * 
 */
public class IntegerNode<E> extends AbstractNode<Integer, E> {

    /**
     * The {@link Integer} value.
     */
    private Integer value;

    public IntegerNode(Integer value) {
        this.value = value;
    }

    public Class<Integer> getType() {
        return Integer.class;
    }

    public Node<Integer, E> simplify() {
        return this;
    }

    public static IntegerNode fromBin(String bin) {
        return new IntegerNode(Integer.parseInt(bin.substring(2), 2));
    }

    public static IntegerNode fromHex(String hex) {
        return new IntegerNode(Integer.parseInt(hex.substring(2), 16));
    }

    public Integer eval(E context) {
        return value;
    }

    public void gather(Set<Reference<E>> references) {
        // Nothing to add
    }

    public void document(Document target) {
        target.text(value.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.Expression#isParameterized()
     */
    public boolean isParameterized() {
        return false;
    }

    public Node<Integer, E> rescope(ReferenceContext<E> context) {
        return this;
    }

}
