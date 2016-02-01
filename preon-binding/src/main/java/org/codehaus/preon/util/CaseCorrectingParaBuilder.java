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

import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.Footnote;
import nl.flotsam.pecia.Para;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.builder.ParaBuilder;

public class CaseCorrectingParaBuilder<T> implements ParaBuilder<T> {

    private boolean started;

    private ParaBuilder<T> wrapped;

    public CaseCorrectingParaBuilder(ParaBuilder<T> wrapped) {
        this.wrapped = wrapped;
    }

    public T end() {
        return wrapped.end();
    }

    public T getParent() {
        return wrapped.getParent();
    }

    public Para<T> code(String text) {
        started = true;
        return wrapped.code(text);
    }

    public Para<T> document(Documenter<ParaContents<?>> target) {
        started = true;
        return wrapped.document(target);
    }

    public Para<T> email(String email) {
        started = true;
        return wrapped.email(email);
    }

    public Para<T> emphasis(String text) {
        started = true;
        return wrapped.emphasis(text);
    }

    public Footnote<? extends Para<T>> footnote() {
        started = true;
        return wrapped.footnote();
    }

    public Para<T> footnote(String text) {
        started = true;
        return wrapped.footnote(text);
    }

    public Para<T> link(Object id, String text) {
        started = true;
        return wrapped.link(id, text);
    }

    public Para<T> term(Object id, String text) {
        started = true;
        return wrapped.term(id, text);
    }

    public Para<T> text(String text) {
        if (!started) {
            StringBuilder builder = new StringBuilder();
            if (text.length() >= 1) {
                builder.append(Character.toUpperCase(text.charAt(0)));
            }
            builder.append(text.substring(1));
            wrapped.text(builder.toString());
        } else {
            wrapped.text(text);
        }
        started = true;
        return wrapped.text(text);
    }

    public Para<T> xref(String id) {
        started = true;
        return wrapped.xref(id);
    }

    public ParaBuilder<T> start() {
        wrapped.start();
        started = false;
        return this;
    }

}
