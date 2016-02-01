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
