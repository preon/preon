/**
 * Copyright (C) 2009 Wilfred Springer
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
package nl.flotsam.preon.codec;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.AnnotatedElement;

import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Expressions;
import nl.flotsam.limbo.util.StringBuilderDocument;
import nl.flotsam.pecia.Contents;
import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import nl.flotsam.preon.Builder;
import nl.flotsam.preon.Codec;
import nl.flotsam.preon.CodecConstructionException;
import nl.flotsam.preon.CodecDescriptor;
import nl.flotsam.preon.CodecFactory;
import nl.flotsam.preon.Codecs;
import nl.flotsam.preon.DecodingException;
import nl.flotsam.preon.Resolver;
import nl.flotsam.preon.ResolverContext;
import nl.flotsam.preon.annotation.BoundString;
import nl.flotsam.preon.annotation.BoundString.ByteConverter;
import nl.flotsam.preon.annotation.BoundString.Encoding;
import nl.flotsam.preon.buffer.BitBuffer;
import nl.flotsam.preon.descriptor.Documenters;
import nl.flotsam.preon.util.TextUtils;

/**
 * A {@link CodecFactory} generating {@link Codecs} capable of generating String
 * from {@link BitBuffer} content.
 * 
 * @author Wilfred Springer
 * 
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
                if (settings.size().length() > 0) {
                    Expression<Integer, Resolver> expr;
                    expr = Expressions.createInteger(context, settings.size());
                    return (Codec<T>) new FixedLengthStringCodec(settings
                            .encoding(), expr, settings.match(), settings
                            .converter().newInstance());
                } else {
                    return (Codec<T>) new NullTerminatedStringCodec(settings
                            .encoding(), settings.match(), settings.converter()
                            .newInstance());
                }
            } catch (InstantiationException e) {
                throw new CodecConstructionException(e.getMessage());
            } catch (IllegalAccessException e) {
                throw new CodecConstructionException(e.getMessage());
            }
        } else {
            return null;
        }
    }

    /**
     * A {@link Codec} that reads null-terminated Strings. Basically, it will
     * read bytes until it encounters a '\0' character, in which case it
     * considers itself to be done, and construct a String from the bytes read.
     * 
     * @author Wilfred Springer (wis)
     * 
     */
    public static class NullTerminatedStringCodec implements Codec<String> {

        private Encoding encoding;

        private String match;

        private ByteConverter byteConverter;

        public NullTerminatedStringCodec(Encoding encoding, String match,
                ByteConverter byteConverter) {
            this.encoding = encoding;
            this.match = match;
            this.byteConverter = byteConverter;
        }

        public String decode(BitBuffer buffer, Resolver resolver,
                Builder builder) throws DecodingException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte value;
            while ((value = buffer.readAsByte(8)) != 0x00) {
                out.write(byteConverter.convert(value));
            }
            String charset = null;
            switch (encoding) {
                case ASCII: {
                    charset = "US-ASCII";
                    break;
                }
                case ISO_8859_1: {
                    charset = "ISO-8859-1";
                    break;
                }
            }
            try {
                return new String(out.toByteArray(), charset);
            } catch (UnsupportedEncodingException uee) {
                throw new DecodingException(uee);
            }
        }

        public Class<?>[] getTypes() {
            return new Class[] { String.class };
        }

        public Expression<Integer, Resolver> getSize() {
            return null;
        }

        public Class<?> getType() {
            return String.class;
        }

        public CodecDescriptor getCodecDescriptor() {
            return new CodecDescriptor() {

                public <C extends SimpleContents<?>> Documenter<C> details(
                        String bufferReference) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            if (match != null && match.length() > 0) {
                                target.para().text(
                                        "The string is expected to match \"")
                                        .text(match).text("\".").end();
                            }
                        }
                    };
                }

                public String getTitle() {
                    return null;
                }

                public <C extends ParaContents<?>> Documenter<C> reference(
                        final Adjective adjective, boolean startWithCapital) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target.text(adjective.asTextPreferA(false)).text(
                                    "string of characters");
                        }
                    };
                }

                public boolean requiresDedicatedSection() {
                    return false;
                }

                public <C extends ParaContents<?>> Documenter<C> summary() {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target
                                    .text("A null-terminated sequence of characters, encoded in "
                                            + encoding + ".");
                        }
                    };
                }

            };
        }
    }

    /**
     * A {@link Codec} decoding Strings based on a fixed number of
     * <em>bytes</em>. (Note that it says <i>bytes</i>, not <i>characters</i>.)
     * 
     * @author Wilfred Springer (wis)
     * 
     */
    public static class FixedLengthStringCodec implements Codec<String> {

        private Encoding encoding;

        private Expression<Integer, Resolver> sizeExpr;

        private String match;

        private ByteConverter byteConverter;

        public FixedLengthStringCodec(Encoding encoding,
                Expression<Integer, Resolver> sizeExpr, String match,
                ByteConverter byteConverter) {
            this.encoding = encoding;
            this.sizeExpr = sizeExpr;
            this.match = match;
            this.byteConverter = byteConverter;
        }

        public String decode(BitBuffer buffer, Resolver resolver,
                Builder builder) throws DecodingException {
            int size = sizeExpr.eval(resolver);
            byte[] bytes = new byte[size];
            for (int i = 0; i < size; i++) {
                bytes[i] = byteConverter.convert(buffer.readAsByte(8));
            }
            String result;
            try {
                result = encoding.decode(bytes);
            } catch (UnsupportedEncodingException e) {
                throw new DecodingException(e);
            }
            if (match.length() > 0) {
                if (!match.equals(result)) {
                    throw new DecodingException(new IllegalStateException(
                            "Expected \"" + match + "\", but got \"" + result
                                    + "\"."));
                }
            }
            return result;
        }

        public Class<?>[] getTypes() {
            return new Class[] { String.class };
        }

        public Expression<Integer, Resolver> getSize() {
            return Expressions.multiply(Expressions.createInteger(8,
                    Resolver.class), sizeExpr);
        }

        public Class<?> getType() {
            return String.class;
        }

        public CodecDescriptor getCodecDescriptor() {
            return new CodecDescriptor() {

                public <C extends SimpleContents<?>> Documenter<C> details(
                        String bufferReference) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target
                                    .para()
                                    .text("The number of characters of the string is ")
                                    .document(
                                            Documenters.forExpression(sizeExpr))
                                    .text(".").end();
                            if (match != null && match.length() > 0) {
                                target.para().text(
                                        "The string is expected to match \"")
                                        .text(match).text("\".").end();
                            }
                        }
                    };
                }

                public String getTitle() {
                    return null;
                }

                public <C extends ParaContents<?>> Documenter<C> reference(
                        final Adjective adjective, boolean startWithCapital) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target.text(adjective.asTextPreferA(false)).text(
                                    "string of characters");
                        }
                    };
                }

                public boolean requiresDedicatedSection() {
                    return false;
                }

                public <C extends ParaContents<?>> Documenter<C> summary() {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target
                                    .text("A sequence of characters, encoded in "
                                            + encoding + ".");
                        }
                    };
                }

            };
        }
    }

}
