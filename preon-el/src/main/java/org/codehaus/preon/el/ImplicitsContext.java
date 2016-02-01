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
package org.codehaus.preon.el;

/**
 * A context that 'imports' constant references such as true and false.
 *
 * @param <E>
 */
public class ImplicitsContext<E> implements ReferenceContext<E> {

    private ReferenceContext<E> nested;

    public ImplicitsContext(ReferenceContext<E> nested) {
        this.nested = nested;
    }

    public Reference<E> selectAttribute(String name) throws BindingException {
        if ("true".equals(name) || "false".equals(name)) {
            return new BooleanLiteralReference<E>(Boolean.parseBoolean(name), this);
        } else {
            return nested.selectAttribute(name);
        }
    }

    public Reference<E> selectItem(String index) throws BindingException {
        return nested.selectItem(index);
    }

    public Reference<E> selectItem(Expression<Integer, E> index) throws BindingException {
        return nested.selectItem(index);
    }

    public void document(Document target) {
        nested.document(target);
    }

}

