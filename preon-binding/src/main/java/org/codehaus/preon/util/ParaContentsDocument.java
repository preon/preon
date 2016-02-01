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

import org.codehaus.preon.el.Document;
import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.Footnote;
import nl.flotsam.pecia.Para;

@SuppressWarnings("unchecked")
public class ParaContentsDocument implements Para {

    private Document document;

    public ParaContentsDocument(Document document) {
        this.document = document;
    }

    public Object end() {
        return null; // No relevant implementation
    }

    public Object getParent() {
        return null; // No relevant implementation
    }

    public Para code(String text) {
        document.text(text);
        return this;
    }

    public Para email(String email) {
        document.text(email);
        return this;
    }

    public Para emphasis(String text) {
        document.text(text);
        return this;
    }

    public Footnote footnote() {
        return null;
    }

    public Para link(Object id, String text) {
        document.link(id, text);
        return this;
    }

    public Para term(Object id, String text) {
        document.text(text);
        return this;
    }

    public Para text(String text) {
        document.text(text);
        return this;
    }

    public Para xref(String id) {
        return this;
    }

    public Para footnote(String footnote) {
        return this;
    }

    public Para document(Documenter documenter) {
        documenter.document(this);
        return this;
    }

}
