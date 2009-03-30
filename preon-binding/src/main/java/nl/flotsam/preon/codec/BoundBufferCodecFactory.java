package nl.flotsam.preon.codec;

import java.lang.reflect.AnnotatedElement;

import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Expressions;
import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import nl.flotsam.preon.Builder;
import nl.flotsam.preon.Codec;
import nl.flotsam.preon.CodecDescriptor2;
import nl.flotsam.preon.CodecFactory;
import nl.flotsam.preon.DecodingException;
import nl.flotsam.preon.Resolver;
import nl.flotsam.preon.ResolverContext;
import nl.flotsam.preon.annotation.BoundBuffer;
import nl.flotsam.preon.buffer.BitBuffer;
import nl.flotsam.preon.descriptor.Documenters;

public class BoundBufferCodecFactory implements CodecFactory {

    private Class<?> BYTE_CLASS = (new byte[0]).getClass();

    public <T> Codec<T> create(AnnotatedElement metadata, Class<T> type,
            ResolverContext context) {
        if (type.isArray() && BYTE_CLASS.equals(type)
                && metadata.isAnnotationPresent(BoundBuffer.class)) {
            return (Codec<T>) new BoundBufferCodec(metadata.getAnnotation(
                    BoundBuffer.class).matches());
        } else {
            return null;
        }
    }

    private static class BoundBufferCodec implements Codec<Object> {

        private byte[] criterion;

        public BoundBufferCodec(byte[] matches) {
            this.criterion = matches;
        }

        public Object decode(BitBuffer buffer, Resolver resolver,
                Builder builder) throws DecodingException {
            for (int i = 0; i < criterion.length; i++) {
                System.out.println(criterion[i]);
                if (criterion[i] != buffer.readAsByte(8)) {
                    throw new DecodingException("First " + criterion.length
                            + " bytes do not match expected value.");
                }
            }
            return criterion;
        }

        public CodecDescriptor2 getCodecDescriptor2() {
            return new CodecDescriptor2() {

                public <C extends SimpleContents<?>> Documenter<C> details(
                        String bufferReference) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target
                                    .para()
                                    .text(
                                            "The sequence is expected to match this hexidecimal sequence: ")
                                    .document(
                                            Documenters
                                                    .forHexSequence(criterion))
                                    .text(".").end();
                        }
                    };
                }

                public String getTitle() {
                    return null;
                }

                public <C extends ParaContents<?>> Documenter<C> reference(
                        final Adjective adjective,
                        final boolean startWithCapital) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target.text(
                                    adjective.asTextPreferA(startWithCapital))
                                    .text(" sequence of bytes");
                        }
                    };
                }

                public boolean requiresDedicatedSection() {
                    return false;
                }

                public <C extends ParaContents<?>> Documenter<C> summary() {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target.document(reference(Adjective.A, true)).text(
                                    ".");
                        }
                    };
                }

            };
        }

        public Expression<Integer, Resolver> getSize() {
            return Expressions.createInteger(criterion.length * 8, Resolver.class);
        }

        public Class<?> getType() {
            return byte.class;
        }

        public Class<?>[] getTypes() {
            return new Class<?>[] { byte.class };
        }
    }

}
