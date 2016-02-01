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
package org.codehaus.preon.descriptor;

import org.codehaus.preon.el.Expression;
import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import org.codehaus.preon.CodecDescriptor;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.CodecDescriptor.Adjective;
import org.codehaus.preon.binding.Binding;
import org.codehaus.preon.buffer.ByteOrder;
import org.codehaus.preon.rendering.IdentifierRewriter;
import org.codehaus.preon.util.DocumentParaContents;
import org.codehaus.preon.util.TextUtils;

public class Documenters {

    private static final char[] HEX_SYMBOLS = "0123456789abcdef".toCharArray();

    public static Documenter<ParaContents<?>> forExpression(
            final Expression<?, Resolver> expr) {
        return new Documenter<ParaContents<?>>() {
            public void document(ParaContents<?> target) {
                if (expr == null) {
                    target.text("(unknown)");
                } else {
                    if (expr.isParameterized()) {
                        Expression<?, Resolver> simplified = expr.simplify();
                        simplified.document(new DocumentParaContents(target));
                    } else {
                        target.text(expr.eval(null).toString());
                    }
                }
            }
        };
    }

    public static Documenter<ParaContents<?>> forNumericValue(final int nrBits,
                                                              final ByteOrder byteOrder) {
        return new Documenter<ParaContents<?>>() {
            public void document(ParaContents<?> target) {
                target.text(nrBits + "bits numeric value");
                if (nrBits > 8) {
                    target.text(" (");
                    switch (byteOrder) {
                        case BigEndian:
                            target.text("big endian");
                            break;
                        case LittleEndian:
                            target.text("little endian");
                            break;
                    }
                    target.text(")");
                }
            }
        };
    }

    public static Documenter<ParaContents<?>> forByteOrder(
            final ByteOrder byteOrder) {
        return new Documenter<ParaContents<?>>() {
            public void document(ParaContents<?> target) {
                target.text(byteOrder.asText());
            }
        };
    }

    public static Documenter<ParaContents<?>> forBits(
            final Expression<Integer, Resolver> expr) {
        if (expr == null) {
            return new Documenter<ParaContents<?>>() {
                public void document(ParaContents<?> target) {
                    target.text("(unknown)");
                }
            };
        } else {
            if (expr.isParameterized()) {
                return forExpression(expr);
            } else {
                return new Documenter<ParaContents<?>>() {
                    public void document(ParaContents<?> target) {
                        int nrBits = expr.eval(null);
                        target.text(TextUtils.bitsToText(nrBits));
                    }
                };
            }
        }
    }

    public static Documenter<ParaContents<?>> forBindingName(
            final Binding binding, final IdentifierRewriter rewriter) {
        return new Documenter<ParaContents<?>>() {
            public void document(ParaContents<?> target) {
                target.term(binding.getId(), rewriter
                        .rewrite(binding.getName()));
            }
        };
    }

    public static Documenter<SimpleContents<?>> forBindingDescription(
            final Binding binding) {
        return new Documenter<SimpleContents<?>>() {
            public void document(SimpleContents<?> target) {
                binding.describe(target);
            }
        };
    }

    public static Documenter<ParaContents<?>> forHexSequence(final byte[] sequence) {
        return new Documenter<ParaContents<?>>() {
            public void document(ParaContents<?> target) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < sequence.length; i++) {
                    if (i != 0) {
                        builder.append(' ');
                    }
                    builder.append(HEX_SYMBOLS[((sequence[i] >> 4) & 0xf)]);
                    builder.append(HEX_SYMBOLS[(sequence[i] & 0x0f)]);
                }
                target.text(builder.toString());
            }
        };
    }

    public static Documenter<ParaContents<?>> forDescriptor(final CodecDescriptor descriptor) {
        return new Documenter<ParaContents<?>>() {

            public void document(ParaContents<?> target) {
                descriptor.reference(Adjective.THE, false);
            }

        };
    }

}
