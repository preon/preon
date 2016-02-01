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
import org.codehaus.preon.el.util.ClassUtils;
import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.Para;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import org.codehaus.preon.*;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;

import java.io.IOException;
import java.util.*;

/**
 * A {@link Codec} that is able to dynamically choose between different types of objects to decode, based on a couple of
 * leading bits.
 *
 * @author Wilfred Springer
 * @see CodecSelector
 */
public class SwitchingCodec implements Codec<Object> {

    /** The object responsible for picking the right {@link Codec}. */
    private CodecSelector selector;

    /**
     * Constructs a new instance.
     *
     * @param selector The object responsible for picking the right {@link Codec}.
     */
    public SwitchingCodec(CodecSelector selector) {
        this.selector = selector;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.Codec#decode(org.codehaus.preon.buffer.BitBuffer,
     * org.codehaus.preon.Resolver, org.codehaus.preon.Builder)
     */

    public Object decode(BitBuffer buffer, Resolver resolver, Builder builder)
            throws DecodingException {
        Codec<?> codec = selector.select(buffer, resolver);
        return codec.decode(buffer, resolver, builder);
    }

    public void encode(Object value, BitChannel channel, Resolver resolver) throws IOException {
        Codec codec = selector.select(value.getClass(), channel, resolver);
        codec.encode(value, channel, resolver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.Codec#getTypes()
     */

    public Class<?>[] getTypes() {
        Set<Class<?>> types = new HashSet<Class<?>>();
        for (Codec<?> codec : selector.getChoices()) {
            types.addAll(Arrays.asList(codec.getTypes()));
        }
        return new ArrayList<Class<?>>(types).toArray(new Class[0]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.Codec#getSize()
     */

    public Expression<Integer, Resolver> getSize() {
        Collection<Codec<?>> choices = selector.getChoices();
        if (choices.size() == 0) {
            return null;
        } else if (choices.size() == 1) {
            return choices.iterator().next().getSize();
        } else {
            Integer size = null;
            Expression<Integer, Resolver> sizeExpr = null;
            for (Codec<?> codec : choices) {
                sizeExpr = codec.getSize();
                if (sizeExpr == null) {
                    return null;
                } else if (!sizeExpr.isParameterized()) {
                    if (size == null) {
                        size = sizeExpr.eval(null);
                    } else {
                        if (!size.equals(sizeExpr.eval(null))) {
                            return null;
                        }
                    }
                }
            }
            if (size != null) {
                return Expressions.add(Expressions.createInteger(size,
                        Resolver.class), selector.getSize());
            } else {
                return null;
            }
        }
    }

    public Class<?> getType() {
        Set<Class<?>> types = new HashSet<Class<?>>();
        for (Codec<?> codec : selector.getChoices()) {
            types.add(codec.getType());
        }
        Class<?>[] result = new Class<?>[0];
        result = new ArrayList<Class<?>>(types).toArray(result);
        return ClassUtils.calculateCommonSuperType(result);
    }

    public CodecDescriptor getCodecDescriptor() {
        return new CodecDescriptor() {

            public <C extends SimpleContents<?>> Documenter<C> details(
                    String bufferReference) {
                return new Documenter<C>() {
                    public void document(C target) {
                        Para<?> para = target.para();
                        selector.document(para);
                        para.end();
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
                        if (selector.getChoices().size() <= 3) {
                            target.text(adjective.asTextPreferA(false)).text(
                                    "either ");
                            List<Codec<?>> codecs = Arrays.asList(selector
                                    .getChoices().toArray(new Codec<?>[0]));
                            for (int i = 0; i < codecs.size(); i++) {
                                target.document(codecs.get(i)
                                        .getCodecDescriptor().reference(
                                        Adjective.NONE, false));
                                if (i > codecs.size() - 2) {
                                    // Do nothing
                                } else if (i == codecs.size() - 2) {
                                    target.text(" or ");
                                } else if (i < codecs.size() - 2) {
                                    target.text(", ");
                                }
                            }
                            target.text(" elements");
                        } else {
                            target.text(adjective.asTextPreferA(false)).text(
                                    "list of elements");
                        }
                    }
                };
            }

            public boolean requiresDedicatedSection() {
                return false;
            }

            public <C extends ParaContents<?>> Documenter<C> summary() {
                return new Documenter<C>() {
                    public void document(C target) {
                        target.text("A list of ");
                        List<Codec<?>> codecs = Arrays.asList(selector
                                .getChoices().toArray(new Codec<?>[0]));
                        for (int i = 0; i < codecs.size(); i++) {
                            target.document(codecs.get(i).getCodecDescriptor()
                                    .reference(Adjective.NONE, false));
                            if (i > codecs.size() - 2) {
                                // Do nothing
                            } else if (i == codecs.size() - 2) {
                                target.text(" or ");
                            } else if (i < codecs.size() - 2) {
                                target.text(", ");
                            }
                        }
                        target.text(" elements.");
                    }
                };
            }

        };
    }

}
