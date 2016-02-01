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
import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import org.codehaus.preon.*;
import org.codehaus.preon.annotation.Bound;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;


/**
 * A {@link CodecFactory} capable of creating {@link Codec Codecs} that deal with booleans.
 *
 * @author Wilfred Springer
 */
public class BooleanCodecFactory implements CodecFactory {

    /*
     * (non-Javadoc)
     * @see org.codehaus.preon.CodecFactory#create(java.lang.reflect.AnnotatedElement, java.lang.Class, org.codehaus.preon.ResolverContext)
     */

    @SuppressWarnings("unchecked")
    public <T> Codec<T> create(AnnotatedElement metadata, Class<T> type,
                               ResolverContext context) {
        if (metadata == null || metadata.isAnnotationPresent(Bound.class)) {
            if (boolean.class.equals(type)) {
                return (Codec<T>) new BooleanCodec(true);
            } else if (Boolean.class.equals(type)) {
                return (Codec<T>) new BooleanCodec(false);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private static class BooleanCodec implements Codec<Boolean> {

        private boolean primitive;

        public BooleanCodec(boolean primitive) {
            this.primitive = primitive;
        }

        public Boolean decode(BitBuffer buffer, Resolver resolver,
                              Builder builder) throws DecodingException {
            return buffer.readAsBoolean();
        }

        public void encode(Boolean value, BitChannel channel, Resolver resolver) throws IOException {
            channel.write(value);
        }

        public CodecDescriptor getCodecDescriptor() {
            return new CodecDescriptor() {

                public <T extends SimpleContents<?>> Documenter<T> details(
                        String bufferReference) {
                    return new Documenter<T>() {
                        public void document(T target) {
                        }
                    };
                }

                public String getTitle() {
                    return null;
                }

                public <T extends ParaContents<?>> Documenter<T> reference(
                        final Adjective adjective, boolean startWithCapital) {
                    return new Documenter<T>() {
                        public void document(T target) {
                            target.text(adjective == Adjective.A ? "a " : "the ");
                            target.text("boolean value");
                        }
                    };
                }

                public boolean requiresDedicatedSection() {
                    return false;
                }

                public <T extends ParaContents<?>> Documenter<T> summary() {
                    return new Documenter<T>() {
                        public void document(T target) {
                            target.text("A one-bit representation of a boolean value: ");
                            target.text("1 = true; 0 = false.");
                        }
                    };
                }

            };
        }

        public Class<?>[] getTypes() {
            if (primitive) {
                return new Class[]{boolean.class};
            } else {
                return new Class[]{Boolean.class};
            }
        }

        public Expression<Integer, Resolver> getSize() {
            return Expressions.createInteger(1, Resolver.class);
        }

        public Class<?> getType() {
            return Boolean.class;
        }

    }

}
