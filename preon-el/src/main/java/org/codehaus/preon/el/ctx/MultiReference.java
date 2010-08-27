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
package org.codehaus.preon.el.ctx;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.preon.el.BindingException;
import org.codehaus.preon.el.Document;
import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.ReferenceContext;
import org.codehaus.preon.el.util.ClassUtils;

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

    public boolean isBasedOn(ReferenceContext<E> context) {
        for (Reference<E> reference : references) {
            if (!reference.isBasedOn(context)) return false;
        }
        return true;
    }

    public Reference<E> rescope(ReferenceContext<E> eReferenceContext) {
        Reference<E>[] replacements = new Reference[this.references.length];
        int i = 0;
        for (Reference<E> reference : references) {
            replacements[i] = reference.rescope(context);
        }
        return new MultiReference<E>(replacements);
    }

}
