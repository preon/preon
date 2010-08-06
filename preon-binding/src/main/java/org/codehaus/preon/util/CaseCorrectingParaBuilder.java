/**
 * Copyright (C) 2009-2010 Wilfred Springer
 *
 * This file is part of Preon.
 *
 * Preon is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * Preon is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Preon; see the file COPYING. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
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
