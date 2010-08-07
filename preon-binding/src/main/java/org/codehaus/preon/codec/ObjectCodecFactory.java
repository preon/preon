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

import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import org.codehaus.preon.*;
import org.codehaus.preon.annotation.Bound;
import org.codehaus.preon.annotation.BoundObject;
import org.codehaus.preon.binding.Binding;
import org.codehaus.preon.binding.BindingFactory;
import org.codehaus.preon.binding.StandardBindingFactory;
import org.codehaus.preon.el.ImportSupportingObjectResolverContext;
import org.codehaus.preon.el.ObjectResolverContext;
import org.codehaus.preon.rendering.ClassNameRewriter;
import org.codehaus.preon.rendering.IdentifierRewriter;
import org.codehaus.preon.util.HidingAnnotatedElement;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/** The {@link CodecFactory} for {@link ObjectCodec}s. */
public class ObjectCodecFactory implements CodecFactory {

    /** The object that will be used to construct {@link org.codehaus.preon.binding.Binding} instances. */
    private BindingFactory bindingFactory;

    /**
     * The object that will be used to construct the appropriate {@link org.codehaus.preon.Codec} instance. (In order to
     * have coverage for all fields defined.)
     */
    private CodecFactory codecFactory;

    /** The object used to turn Java identifiers into something that is potentially readable by humans. */
    private IdentifierRewriter rewriter = new ClassNameRewriter();

    /**
     * Constructs a new instance, using a default mechanism for constructing {@link org.codehaus.preon.binding.Binding}
     * instances.
     */
    public ObjectCodecFactory() {
        bindingFactory = new StandardBindingFactory();
    }

    /**
     * Constructs a new instance, using a default mechanism for constructing {@link org.codehaus.preon.binding.Binding}
     * instances.
     *
     * @param codecFactory The {@link org.codehaus.preon.CodecFactory} used to create <code>Codecs</code>.
     */
    public ObjectCodecFactory(CodecFactory codecFactory) {
        bindingFactory = new StandardBindingFactory();
        this.codecFactory = codecFactory;
    }

    /**
     * Constructs a new instance.
     *
     * @param codecFactory   The object used to create <code>Codecs</code>.
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
    * org.codehaus.preon.CodecFactory#create(java.lang.reflect.AnnotatedElement,
    * java.lang.Class, org.codehaus.preon.ResolverContext)
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
        if (passThroughContext.getBindings().size() == 0) {
            throw new CodecConstructionException("Failed to find a single bound field on " + type.getName());
        }
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

    private static class CodecReference implements Documenter<ParaContents<?>> {

        private Codec<?> codec;

        public void document(ParaContents<?> target) {
            target.document(codec.getCodecDescriptor().reference(CodecDescriptor.Adjective.THE,
                    false));
        }

        public void setCodec(Codec<?> codec) {
            this.codec = codec;
        }

    }

}
