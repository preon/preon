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
import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.ReferenceContext;

/**
 * A convenience {@link Node} wrapper around <code>String</code>s.
 * 
 * @author Wilfred Springer (wis)
 * 
 * @param <E> 
 */
public class StringNode<E> extends AbstractNode<String, E> {

    private String value;

    public StringNode(String value) {
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.el.ast.Node#eval(java.lang.Object)
     */
    public String eval(E context) {
        return value;
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.el.ast.Node#gather(java.util.Set)
     */
    public void gather(Set<Reference<E>> references) {
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.el.ast.Node#getType()
     */
    public Class<String> getType() {
        return String.class;
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.el.ast.Node#simplify()
     */
    public Node<String, E> simplify() {
        return this;
    }

    public Node<String, E> rescope(ReferenceContext<E> eReferenceContext) {
        return this;
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.el.Expression#isParameterized()
     */
    public boolean isParameterized() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.el.Descriptive#document(org.codehaus.preon.el.Document)
     */
    public void document(Document target) {
        target.text("the String \"");
        target.text(value);
        target.text("\"");
    }

}
