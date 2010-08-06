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
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import org.codehaus.preon.CodecDescriptor;

public class CodecDescriptorHolder implements CodecDescriptor {

    private CodecDescriptor descriptor;

    public <C extends SimpleContents<?>> Documenter<C> details(String bufferReference) {
        return descriptor.details(bufferReference);
    }

    public String getTitle() {
        return descriptor.getTitle();
    }

    public <C extends ParaContents<?>> Documenter<C> reference(Adjective adjective, boolean startWithCapital) {
        return descriptor.reference(adjective, startWithCapital);
    }

    public boolean requiresDedicatedSection() {
        return descriptor.requiresDedicatedSection();
    }

    public <C extends ParaContents<?>> Documenter<C> summary() {
        return descriptor.summary();
    }

    public void setDescriptor(CodecDescriptor descriptor) {
        this.descriptor = descriptor;
    }

}
