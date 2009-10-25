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

import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Expressions;
import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import nl.flotsam.pecia.Table3Cols;
import nl.flotsam.preon.*;
import nl.flotsam.preon.CodecDescriptor.Adjective;
import nl.flotsam.preon.annotation.Bound;
import nl.flotsam.preon.annotation.BoundObject;
import nl.flotsam.preon.binding.Binding;
import nl.flotsam.preon.binding.BindingFactory;
import nl.flotsam.preon.binding.StandardBindingFactory;
import nl.flotsam.preon.buffer.BitBuffer;
import nl.flotsam.preon.channel.BitChannel;
import nl.flotsam.preon.descriptor.Documenters;
import nl.flotsam.preon.limbo.ImportSupportingObjectResolverContext;
import nl.flotsam.preon.limbo.ObjectResolverContext;
import nl.flotsam.preon.rendering.ClassNameRewriter;
import nl.flotsam.preon.rendering.IdentifierRewriter;
import nl.flotsam.preon.util.HidingAnnotatedElement;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link CodecFactory} generating {@link Codec Codecs} capable of decoding certain types of objects from the {@link
 * BitBuffer} by constructing a default instance of that type and decoding its state from the {@link BitBuffer}. <p/>
 * <p> Fields on objects can have annotations referring to other fields, potentially fields on the same class. Whenever
 * an ObjectCodec is requested to decode data from the {@link BitBuffer}, it will first create an instance of the
 * Object, create a Resolver that 'wraps' around this object, and pass that on to the {@link Binding} instances tied to
 * fields on the object. </p>
 *
 * @author Wilfred Springer
 */
public class ObjectCodecFactory implements CodecFactory {

    /**
     * The object that will be used to construct {@link Binding} instances.
     */
    private BindingFactory bindingFactory;

    /**
     * The object that will be used to construct the appropriate {@link Codec}
     * instance. (In order to have coverage for all fields defined.)
     */
    private CodecFactory codecFactory;

    /**
     * The object used to turn Java identifiers into something that is
     * potentially readable by humans.
     */
    private IdentifierRewriter rewriter = new ClassNameRewriter();

    /**
     * Constructs a new instance, using a default mechanism for constructing
     * {@link Binding} instances.
     */
    public ObjectCodecFactory() {
        bindingFactory = new StandardBindingFactory();
    }

    /**
     * Constructs a new instance, using a default mechanism for constructing
     * {@link Binding} instances.
     *
     * @param codecFactory The {@link CodecFactory} used to create
     * <code>Codecs</code>.
     */
    public ObjectCodecFactory(CodecFactory codecFactory) {
        bindingFactory = new StandardBindingFactory();
        this.codecFactory = codecFactory;
    }

    /**
     * Constructs a new instance.
     *
     * @param codecFactory The object used to create <code>Codecs</code>.
     * @param bindingFactory The object used to create <code>Bindings</code>.
     */
    public ObjectCodecFactory(CodecFactory codecFactory,
                              BindingFactory bindingFactory) {
        this.codecFactory = codecFactory;
        this.bindingFactory = bindingFactory;
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * nl.flotsam.preon.CodecFactory#create(java.lang.reflect.AnnotatedElement,
      * java.lang.Class, nl.flotsam.preon.ResolverContext)
      */
    public <T> Codec<T> create(AnnotatedElement metadata, Class<T> type,
                               ResolverContext context) {
        if (metadata == null) {
            return createCodec(type, context);
        } else if (metadata.isAnnotationPresent(Bound.class)) {
            return createCodec(type, context);
        } else if (metadata.isAnnotationPresent(BoundObject.class)) {
            return createCodec(type, context, metadata);
        } else {
            return null;
        }
    }

    private <T> ObjectCodec<T> createCodec(Class<T> type,
                                           ResolverContext context) {
        ObjectResolverContext passThroughContext = new BindingsContext(type,
                context);
        passThroughContext = ImportSupportingObjectResolverContext.decorate(
                passThroughContext, type);
        CodecReference reference = new CodecReference();
        harvestBindings(type, passThroughContext, reference);
        ObjectCodec<T> result = new ObjectCodec<T>(type, rewriter,
                passThroughContext);
        reference.setCodec(result);
        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> Codec<T> createCodec(Class<T> type, ResolverContext context,
                                     AnnotatedElement metadata) {
        BoundObject settings = metadata.getAnnotation(BoundObject.class);
        // TODO: Handle type incompatibility
        if (Void.class.equals(settings.type())) {
            if (settings.selectFrom().alternatives().length > 0
                    || settings.selectFrom().defaultType() != Void.class) {
                return (Codec<T>) new SelectFromCodec(type, settings
                        .selectFrom(), context, codecFactory,
                        hideChoices(metadata));
            }
            if (settings.types().length == 0) {
                return createCodec(type, context);
            }
            List<Codec<?>> codecs = new ArrayList<Codec<?>>();
            for (Class valueType : settings.types()) {
                codecs.add(codecFactory.create(null, valueType, context));
            }
            CodecSelectorFactory selectorFactory = null;
            selectorFactory = new TypePrefixSelectorFactory();
            CodecSelector selector = selectorFactory.create(context, codecs);
            return (Codec<T>) new SwitchingCodec(selector);
        } else {
            return (Codec<T>) createCodec(settings.type(), context);
        }
    }

    private AnnotatedElement hideChoices(AnnotatedElement metadata) {
        return new HidingAnnotatedElement(BoundObject.class, metadata);
    }

    private <T> void harvestBindings(Class<T> type,
                                     ObjectResolverContext context, CodecReference reference) {
        if (Object.class.equals(type)) {
            return;
        }
        harvestBindings(type.getSuperclass(), context, reference);
        Field[] fields = type.getDeclaredFields();
        // For creating the Codecs, we already need a modified
        // ReferenceContext, allowing us to incrementally bind to references
        // of fields declared before.
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())
                    && !field.isSynthetic()) {
                Codec<?> codec = codecFactory.create(field, field.getType(),
                        context);
                if (codec != null) {
                    Binding binding = bindingFactory.create(field, field,
                            codec, context, reference);
                    context.add(field.getName(), binding);
                }
            }
        }
    }

    /**
     * The {@link Codec} for Objects, in general.
     *
     */
    private static class ObjectCodec<T> implements Codec<T> {

        private Class<T> type;

        private IdentifierRewriter rewriter;

        private ObjectResolverContext context;

        public ObjectCodec(Class<T> type, IdentifierRewriter rewriter,
                           ObjectResolverContext context) {
            this.type = type;
            this.rewriter = rewriter;
            this.context = context;
        }

        public T decode(BitBuffer buffer, Resolver resolver, Builder builder)
                throws DecodingException {
            try {
                final T result = builder.create(type);
                resolver = context.getResolver(result, resolver);
                // TODO: I think I need a replacement resolver here.
                for (Binding binding : context.getBindings()) {
                    binding.load(result, buffer, resolver, builder);
                }
                return result;
            }
            catch (InstantiationException ie) {
                ie.printStackTrace();
                throw new DecodingException(type, ie);
            }
            catch (IllegalAccessException iae) {
                throw new DecodingException(iae);
            }
        }

        public void encode(T value, BitChannel channel, Resolver resolver) {
            throw new UnsupportedOperationException();
        }

        public Class<?>[] getTypes() {
            return new Class[]{type};
            // Set<Class<?>> types = new HashSet<Class<?>>();
            // for (Binding binding : context.getBindings()) {
            // types.addAll(Arrays.asList(binding.getTypes()));
            // }
            // types.add(type);
            // return new ArrayList<Class<?>>(types).toArray(new Class[0]);
        }

        /*
           * (non-Javadoc)
           *
           * @see nl.flotsam.preon.Codec#getSize()
           */
        public Expression<Integer, Resolver> getSize() {
            List<Binding> bindings = context.getBindings();
            if (bindings.size() > 0) {
                Expression<Integer, Resolver> result = null;
                for (Binding binding : bindings) {
                    if (result == null) {
                        result = binding.getSize();
                    } else {
                        result = Expressions.add(result, binding.getSize());
                    }
                }
                return result;
            } else {
                return Expressions.createInteger(0, Resolver.class);
            }
        }

        /*
           * (non-Javadoc)
           *
           * @see java.lang.Object#toString()
           */
        public String toString() {
            return "Codec of " + type.getSimpleName();
        }

        public Class<?> getType() {
            return type;
        }

        public CodecDescriptor getCodecDescriptor() {
            return new CodecDescriptor() {

                public <C extends SimpleContents<?>> Documenter<C> details(
                        String bufferReference) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target
                                    .para()
                                    .document(reference(Adjective.THE, false))
                                    .text(
                                            " is composed out of several other smaller elements.")
                                    .text(
                                            " The table below provides an overview.")
                                    .end();
                            Table3Cols<?> table3Cols = target.table3Cols();
                            table3Cols = table3Cols.header().entry().para()
                                    .text("Name").end().entry().para().text(
                                            "Description").end().entry().para()
                                    .text("Size (in bits)").end().end();
                            for (Binding binding : ObjectCodec.this.context
                                    .getBindings()) {
                                table3Cols
                                        .row()
                                        .entry()
                                        .para()
                                        .document(
                                                Documenters.forBindingName(
                                                        binding, rewriter))
                                        .end()
                                        .entry()
                                        .document(
                                                Documenters
                                                        .forBindingDescription(binding))
                                        .entry().para().document(
                                        Documenters.forBits(binding
                                                .getSize())).end()
                                        .end();
                            }
                            table3Cols.end();
                        }
                    };
                }

                public String getTitle() {
                    return rewriter.rewrite(type.getName());
                }

                public <C extends ParaContents<?>> Documenter<C> reference(
                        Adjective adjective, boolean startWithCapital) {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target.link(getTitle(), getTitle());
                        }
                    };
                }

                public boolean requiresDedicatedSection() {
                    return true;
                }

                public <C extends ParaContents<?>> Documenter<C> summary() {
                    return new Documenter<C>() {
                        public void document(C target) {
                            target.text("A ").link(getTitle(), getTitle());
                        }
                    };
                }

            };
        }

    }

    private static class CodecReference implements Documenter<ParaContents<?>> {

        private Codec<?> codec;

        public void document(ParaContents<?> target) {
            target.document(codec.getCodecDescriptor().reference(Adjective.THE,
                    false));
        }

        public void setCodec(Codec<?> codec) {
            this.codec = codec;
        }

    }

}
