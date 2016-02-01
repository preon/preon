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
package org.codehaus.preon.codec;

import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Expressions;
import org.codehaus.preon.*;
import org.codehaus.preon.annotation.BoundString;
import org.codehaus.preon.buffer.BitBuffer;
import java.nio.charset.Charset;

import java.lang.reflect.AnnotatedElement;

/**
 * A {@link CodecFactory} generating {@link Codecs} capable of generating String from {@link BitBuffer} content.
 *
 * @author Wilfred Springer
 */
public class StringCodecFactory implements CodecFactory {

    @SuppressWarnings("unchecked")
    public <T> Codec<T> create(AnnotatedElement metadata, Class<T> type,
                               ResolverContext context) {
        if (metadata == null) {
            return null;
        }
        BoundString settings = metadata.getAnnotation(BoundString.class);
        if (String.class.equals(type) && settings != null) {
            try {
				Charset charset; // Encodings are now given as strings, and turned into Charsets
                charset = Charset.availableCharsets().get(settings.encoding());
                // This throws a NullPointerException if the Charset can't be found
                if (settings.size().length() > 0) {
                    Expression<Integer, Resolver> expr;
                    expr = Expressions.createInteger(context, settings.size());
                    return (Codec<T>) new FixedLengthStringCodec(
							charset, //Note that this is a Charset, not an Encoding
							expr,
							settings.match(),
							settings.converter().newInstance());
                } else {
                    return (Codec<T>) new NullTerminatedStringCodec(
							charset, //Note that this is a Charset, not an Encoding
							settings.match(),
							settings.converter().newInstance());
                }
            } catch (InstantiationException e) {
                throw new CodecConstructionException(e.getMessage());
            } catch (IllegalAccessException e) {
                throw new CodecConstructionException(e.getMessage());
            } catch (NullPointerException e) {
				throw new CodecConstructionException(
							"Unsupported encoding: "+e.getMessage());
			}
        } else {
            return null;
        }
    }

}
