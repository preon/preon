package org.codehaus.preon.codec;

import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import org.codehaus.preon.*;
import org.codehaus.preon.annotation.Purpose;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.el.Expression;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;

public class PurposeDecorator implements CodecDecorator {

    public <T> Codec<T> decorate(final Codec<T> codec, AnnotatedElement metadata, Class<T> type, ResolverContext context) {
        final Purpose purpose =
                metadata != null ? metadata.getAnnotation(Purpose.class) : null;
        if (purpose != null) {
            
            return new Codec<T>() {
                public T decode(BitBuffer buffer, Resolver resolver, Builder builder) throws DecodingException {
                    return codec.decode(buffer, resolver, builder);
                }

                public void encode(T value, BitChannel channel, Resolver resolver) throws IOException {
                    codec.encode(value, channel, resolver);
                }

                public Expression<Integer, Resolver> getSize() {
                    return codec.getSize();
                }

                public CodecDescriptor getCodecDescriptor() {
                    return new CodecDescriptor() {
                        public <C extends ParaContents<?>> Documenter<C> summary() {
                            return new Documenter<C>() {
                                public void document(C c) {
                                    c.text(purpose.value() + " ").document(codec.getCodecDescriptor().summary());
                                }
                            };
                        }

                        public <C extends ParaContents<?>> Documenter<C> reference(Adjective adjective, boolean startWithCapital) {
                            return codec.getCodecDescriptor().reference(adjective, startWithCapital);
                        }

                        public <C extends SimpleContents<?>> Documenter<C> details(final String bufferReference) {
                            return codec.getCodecDescriptor().details(bufferReference);
                        }

                        public boolean requiresDedicatedSection() {
                            return codec.getCodecDescriptor().requiresDedicatedSection();
                        }

                        public String getTitle() {
                            return codec.getCodecDescriptor().getTitle();
                        }
                    };
                }

                public Class<?>[] getTypes() {
                    return codec.getTypes();
                }

                public Class<?> getType() {
                    return codec.getType();
                }
            };
        } else {
            return codec;
        }

    }

}
