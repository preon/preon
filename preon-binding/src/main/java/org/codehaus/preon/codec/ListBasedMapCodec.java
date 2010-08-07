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
import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import org.codehaus.preon.*;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA. User: wilfred Date: Jun 20, 2010 Time: 3:49:00 AM To change this template use File |
 * Settings | File Templates.
 */
public class ListBasedMapCodec<K,V> implements Codec<Map<K,V>> {

    private final Codec<List<Map.Entry<K, V>>> listCodec;

    public ListBasedMapCodec(Codec<List<Map.Entry<K,V>>> listCodec) {
        this.listCodec = listCodec;
    }

    public Map<K,V> decode(BitBuffer buffer, Resolver resolver, Builder builder) throws DecodingException {
        List<? extends Map.Entry<K, V>> entries = listCodec.decode(buffer, resolver, builder);
        Map<K,V> result = new HashMap(entries.size());
        for (Map.Entry<K, V> entry : entries) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public void encode(Map value, BitChannel channel, Resolver resolver) throws IOException {
        throw new UnsupportedOperationException();
    }

    public Expression<Integer, Resolver> getSize() {
        return listCodec.getSize();
    }

    public CodecDescriptor getCodecDescriptor() {
        return new CodecDescriptor() {

            public <C extends ParaContents<?>> Documenter<C> summary() {
                return new Documenter<C>() {
                    public void document(C target) {
                        target.document(reference(Adjective.A, true)).text(".");
                    }
                };
            }

            public <C extends ParaContents<?>> Documenter<C> reference(final Adjective adjective,
                                                                       final boolean startWithCapital)
            {
                 return new Documenter<C>() {
                        public void document(C target) {
                            target.text(adjective.asTextPreferA(startWithCapital)).text(
                                    "map produced from a ").document(
                                    listCodec.getCodecDescriptor().reference(
                                            Adjective.NONE, false));
                        }
                    };
            }

            public <C extends SimpleContents<?>> Documenter<C> details(String bufferReference) {
                return listCodec.getCodecDescriptor().details(bufferReference);
            }

            public boolean requiresDedicatedSection() {
                return false;
            }

            public String getTitle() {
                return null;
            }
        };
    }

    public Class<?>[] getTypes() {
        return new Class<?>[] { List.class };
    }

    public Class<?> getType() {
        return List.class;
    }
}
