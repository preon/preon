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
package org.codehaus.preon.codec;

import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Expressions;
import org.codehaus.preon.*;
import org.codehaus.preon.annotation.BoundString;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.util.DefaultInputStreamReaderFactory;

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
							settings.match());
                } else {
                    return (Codec<T>) new NullTerminatedStringCodec(
							charset, //Note that this is a Charset, not an Encoding
                            new DefaultInputStreamReaderFactory(),
							settings.match());
                }
            } catch (NullPointerException e) {
				throw new CodecConstructionException(
							"Unsupported encoding: "+e.getMessage());
			}
        } else {
            return null;
        }
    }

}
