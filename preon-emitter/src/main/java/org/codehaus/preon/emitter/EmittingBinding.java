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
