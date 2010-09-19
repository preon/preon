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
package org.codehaus.preon.emitter;

import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import org.codehaus.preon.Builder;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.binding.Binding;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.el.Expression;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA. User: wilfred Date: Sep 17, 2010 Time: 9:30:37 PM To change this template use File |
 * Settings | File Templates.
 */
public class EmittingBinding implements Binding {

    private final Binding binding;
    private final Emitter emitter;

    public EmittingBinding(Binding binding, Emitter emitter) {
        this.binding = binding;
        this.emitter = emitter;
    }

    public void load(Object object, BitBuffer buffer, Resolver resolver, Builder builder) throws DecodingException {
        emitter.markStartLoad(binding.getName(), object);
        binding.load(object, buffer, resolver, builder);
        emitter.markEndLoad();
    }

    public <V extends SimpleContents<?>> V describe(V contents) {
        return binding.describe(contents);
    }

    public <T, V extends ParaContents<T>> V writeReference(V contents) {
        return binding.writeReference(contents);
    }

    public Class<?>[] getTypes() {
        return binding.getTypes();
    }

    public Object get(Object context) throws IllegalArgumentException, IllegalAccessException {
        return binding.get(context);
    }

    public String getName() {
        return binding.getName();
    }

    public Expression<Integer, Resolver> getSize() {
        return binding.getSize();
    }

    public String getId() {
        return binding.getId();
    }

    public Class<?> getType() {
        return binding.getType();
    }

    public void save(Object value, BitChannel channel, Resolver resolver) throws IOException {
        binding.save(value, channel, resolver);
    }
}
