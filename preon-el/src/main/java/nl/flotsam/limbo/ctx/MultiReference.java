/*
 * Copyright 2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package nl.flotsam.limbo.ctx;

import java.util.ArrayList;
import java.util.List;

import nl.flotsam.limbo.BindingException;
import nl.flotsam.limbo.Document;
import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Reference;
import nl.flotsam.limbo.ReferenceContext;
import nl.flotsam.limbo.util.ClassUtils;
import nl.flotsam.limbo.util.StringBuilderDocument;

/**
 * A reference encapsulating a number of references. The reference can be
 * treated as a single reference. Trying to resolve a property by name or a
 * value by index will result in a new {@link MultiReference}. There may cases
 * in which {@link #selectAttribute(String)} will throw {@link BindingException}
 * s for some of the references managed by this {@link MultiReference}. In those
 * cases, the policy of this implementation is to skip these references, and
 * proceed with the others.
 * 
 * @author Wilfred Springer (wis)
 * 
 * @param <E>
 *            The type of context expected by the {@link Reference} when
 *            resolving values.
 */
public class MultiReference<E> implements Reference<E> {

    private static Reference[] REFERENCE_ARRAY_TYPE = new Reference[0];

    /**
     * A collection of references.
     */
    private Reference<E>[] references;

    /**
     * The (common) {@link ReferenceContext}.
     */
    private ReferenceContext<E> context;

    /**
     * The common supertype of anything that can be resolved through this
     * reference.
     */
    private Class<?> commonSuperType;

    /**
     * Constructs a new instance, accepting the references this single reference
     * needs to be constructed from.
     * 
     * @param references
     */
    public MultiReference(Reference<E>... references) {
        commonSuperType = calculateCommonSuperType(references);
        this.references = references;
        for (Reference<E> reference : references) {
            if (context != null) {
                if (!context.equals(reference.getReferenceContext())) {
                    throw new BindingException(
                            "Multiple types of runtime contexts.");
                }
            } else {
                context = reference.getReferenceContext();
            }
        }
    }

    private static <E> Class<?> calculateCommonSuperType(
            Reference<E>... references) {
        Class<?>[] types = new Class<?>[references.length];
        for (int i = 0; i < references.length; i++) {
            types[i] = references[i].getType();
        }
        return ClassUtils.calculateCommonSuperType(types);
    }

    public ReferenceContext<E> getReferenceContext() {
        return context;
    }

    public Object resolve(E context) {
        for (Reference<E> reference : references) {
            try {
                return reference.resolve(context);
            } catch (BindingException rre) {
                // Let's try one more.
            }
        }
        throw new BindingException(
                "Failed to resolve reference for all options.");
    }

    public Reference<E> selectItem(String index) {
        List<Reference<E>> results = new ArrayList<Reference<E>>();
        for (Reference<E> reference : references) {
            results.add(reference.selectItem(index));
        }
        return new MultiReference<E>(results.toArray(REFERENCE_ARRAY_TYPE));
    }

    public Reference<E> selectItem(Expression<Integer, E> index) {
        List<Reference<E>> results = new ArrayList<Reference<E>>();
        for (Reference<E> reference : references) {
            results.add(reference.selectItem(index));
        }
        return new MultiReference<E>(results.toArray(REFERENCE_ARRAY_TYPE));
    }

    public Reference<E> selectAttribute(String name) {
        List<Reference<E>> results = new ArrayList<Reference<E>>();
        for (Reference<E> reference : references) {
            try {
                results.add(reference.selectAttribute(name));
            } catch (BindingException e) {
                // This is ok, we are just not going to add this reference.
            }
        }
        return new MultiReference<E>(results.toArray(REFERENCE_ARRAY_TYPE));
    }

    public void document(Document target) {
        if (references.length > 1) {
            target.text("either ");
        }
        references[0].document(target);
        if (references.length > 2) {
            for (int i = 1; i <= references.length - 2; i++) {
                target.text(", ");
                references[i].document(target);
            }
        }
        if (references.length > 1) {
            target.text(" or ");
            references[references.length - 1].document(target);
        }
    }

    public boolean isAssignableTo(Class<?> type) {
        for (Reference<?> reference : references) {
            if (!reference.isAssignableTo(type)) {
                return false;
            }
        }
        return true;
    }

    public Class<?> getType() {
        return commonSuperType;
    }

    public Reference<E> narrow(Class<?> type) throws BindingException {
        List<Reference<E>> resulting = new ArrayList<Reference<E>>();
        for (Reference<E> reference :references) {
            Reference<E> result = reference.narrow(type);
            if (result != null) {
                resulting.add(result);
            }
        }
        if (resulting.size() == 0) {
            return null;
        } else {
            return new MultiReference<E>(resulting.toArray(REFERENCE_ARRAY_TYPE));
        }
    }

}
