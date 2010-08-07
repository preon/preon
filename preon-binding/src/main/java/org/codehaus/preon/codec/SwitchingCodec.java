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
