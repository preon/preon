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
package org.codehaus.preon.binding;

import org.codehaus.preon.el.Expression;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import org.codehaus.preon.Builder;
import org.codehaus.preon.Codec;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.buffer.BitBuffer;

import java.io.IOException;

/**
 * The interface of objects that are able to load object state from a {@link BitBuffer} and store object state into a
 * {@link BitBuffer}. Note the difference with a {@link Codec}. A {@link Codec} is capable of creating <em>new</em>
 * objects. {@link Binding Bindings} are expected to unload their data into an existing object. <p/> <p> The {@link
 * Binding} abstraction is key to the inner workings of the {@link org.codehaus.preon.codec.ObjectCodecFactory}. The
 * reason why it is a public interface instead of an internal one is to allow you to plugin in other kinds of Binding.
 * The typical example here is the {@link ConditionalBindingFactory}. This {@link BindingFactory} creates {@link Binding
 * Binding} instances that respect conditions set as annotations on {@link Fields}. </p>
 *
 * @author Wilfred Springer
 */
public interface Binding {

    /**
     * Loads a value from the {@link BitBuffer} and uses the value to populate a
     * field on the object.
     *
     * @param object
     *            The Object on which fields need to be populated.
     * @param buffer
     *            The buffer from which data will be taken.
     * @param resolver
     *            The object capable of returning values for references passed
     *            in.
     * @param builder
     *            The builder that will be used when - while loading data from
     *            the {@link BitBuffer} - the Binding is (indirectly) required
     *            to create a default instance of a type.
     * @throws DecodingException
     *             If we fail to decode the fields value from the
     *             {@link BitBuffer}.
     */
    void load(Object object, BitBuffer buffer, Resolver resolver,
              Builder builder) throws DecodingException;

    /**
     * Describes this {@link Binding} in the paragraph passed in.
     *
     * @param <T>
     *            The type of the container for this paragraph.
     * @param <V>
     *            The paragraph in which the content has been written.
     * @param contents
     *            The paragraph in which content will be written.
     * @param resolver
     *            The object capable of rendering references in a human-readable
     *            way.
     * @return The same object as passed in.
     */
    <V extends SimpleContents<?>> V describe(V contents);

    /**
     * Writes a (potentially hyperlinked) reference in the paragraph passed in.
     */
    <T, V extends ParaContents<T>> V writeReference(V contents);

    /**
     * Returns an array of types that could potentially be instantiated while
     * decoding the field's value.
     *
     * @return An array of types that could potentially be instantiated while
     *         decoding the field's value.
     */
    Class<?>[] getTypes();

    /**
     * Returns the value by applying the binding to a certain context.
     *
     * @param context
     *            The object to bind to.
     * @return The value.
     */
    Object get(Object context) throws IllegalArgumentException,
            IllegalAccessException;

    /**
     * Returns the name of the binding.
     *
     * @return The name of the binding.
     */
    String getName();

    /**
     * Returns an {@link Expression} indicating the amount of bits required for
     * representing the value handled by this binding.
     *
     * @return An {@link Expression} evaluating to the amount of bits required
     *         for representing the value handled by this binding.
     */
    Expression<Integer, Resolver> getSize();

    /**
     * Returns a unique identifier for this binding. (Guaranteed to be unique
     * for the Codec created.) Note that decorators are expected to leave
     * this id untouched.
     *
     * @return A unique identifier for this binding.
     */
    String getId();

    /**
     * Returns the type of object expected to be loaded by this binding.
     *
     * @return The type of object expected to be loaded by this binding.
     */
    Class<?> getType();

    /**
     * Saves the state of the bound value to the {@link org.codehaus.preon.channel.BitChannel} passed in.
     *
     * @param value The value to be stored.
     * @param channel The channel receiving the encoded representation.
     * @param resolver The resolver, used to resolve variable references.
     */
    void save(Object value, BitChannel channel, Resolver resolver) throws IOException;
}