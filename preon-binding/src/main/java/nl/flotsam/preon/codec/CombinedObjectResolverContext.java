/*
 * Copyright (C) 2008 Wilfred Springer
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
 * Preon; see the file COPYING. If not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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

import java.util.List;

import nl.flotsam.limbo.BindingException;
import nl.flotsam.limbo.Document;
import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Reference;
import nl.flotsam.limbo.ReferenceContext;
import nl.flotsam.preon.Resolver;
import nl.flotsam.preon.ResolverContext;
import nl.flotsam.preon.binding.Binding;

/**
 * A {@link ObjectResolverContext} that allows you to combine a
 * {@link ObjectResolverContext} as well as a {@link ResolverContext}.
 * {@link #selectAttribute(String)} will resolve into the {@link #outerContext}
 * if the name of the attribute is a prefix passed in. Otherwise, it will
 * resolve to an attribute of the second {@link ResolverContext}.
 * 
 * @author Wilfred Springer (wis)
 * 
 */
public class CombinedObjectResolverContext implements ObjectResolverContext {

    /**
     * The object to which the {@link #prefix} will resolve.
     */
    private ResolverContext outerContext;

    /**
     * The object resolving all non-{@link #prefix} references.
     */
    private ObjectResolverContext innerContext;

    /**
     * The prefix to match for selecting the {@link #outerContext}.
     */
    private String prefix;

    /**
     * 
     * @param outer
     * @param inner
     * @param prefix
     */
    public CombinedObjectResolverContext(ResolverContext outer, ObjectResolverContext inner,
            String prefix) {
        this.outerContext = outer;
        this.innerContext = inner;
        this.prefix = prefix;
    }

    /**
     * 
     * @param outer
     * @param inner
     * @param prefix
     * @param outerType
     */
    public CombinedObjectResolverContext(ResolverContext outer, ObjectResolverContext inner,
            String prefix, Class<?> outerType) {
        this.outerContext = outer;
        this.innerContext = inner;
        this.prefix = prefix;
    }

    public Resolver createResolver(final Resolver inner, final Object value) {
        return new Resolver() {

            public Object get(String name) throws BindingException {
                if (prefix.equals(name)) {
                    return value;
                } else {
                    return inner.get(name);
                }
            }

            public Resolver getOuter() {
                return inner.getOuter();
            }

        };
    }

    public Reference<Resolver> selectAttribute(String name) {
        if (prefix.equals(name)) {
            return new OuterContextReference(outerContext);
        } else {
            return innerContext.selectAttribute(name);
        }
    }

    public Reference<Resolver> selectItem(String index) {
        // TODO Auto-generated method stub
        return null;
    }

    public Reference<Resolver> selectItem(Expression<Integer, Resolver> index) {
        // TODO Auto-generated method stub
        return null;
    }

    public void document(Document target) {
        // TODO Auto-generated method stub

    }

    private static class OuterContextReference implements Reference<Resolver> {

        private ResolverContext context;

        public OuterContextReference(ResolverContext context) {
            this.context = context;
        }

        public ReferenceContext<Resolver> getReferenceContext() {
            return context;
        }

        public boolean isAssignableTo(Class<?> type) {
            return type.isAssignableFrom(ReferenceContext.class);
        }

        public Object resolve(Resolver context) {
            throw new BindingException("Not expected to use this directly.");
        }

        public Reference<Resolver> selectAttribute(String name) {
            return new SteppingStoneReference(context.selectAttribute(name));
        }

        public Reference<Resolver> selectItem(String index) {
            throw new BindingException("Cannot resolve index on CompoundContext.");
        }

        public Reference<Resolver> selectItem(Expression<Integer, Resolver> index) {
            throw new BindingException("Cannot resolve index on CompoundContext.");
        }

        public void document(Document target) {
            context.document(target);
        }

        public Class<?> getType() {
            return ReferenceContext.class;
        }

    }

    private static class SteppingStoneReference implements Reference<Resolver> {

        private Reference<Resolver> wrapped;

        private SteppingStoneReference(Reference<Resolver> reference) {
            this.wrapped = reference;
        }

        public ReferenceContext<Resolver> getReferenceContext() {
            return wrapped.getReferenceContext();
        }

        public boolean isAssignableTo(Class<?> type) {
            return wrapped.isAssignableTo(type);
        }

        public Object resolve(Resolver context) {
            Resolver resolver = context.getOuter();
            return wrapped.resolve(resolver);
        }

        public Reference<Resolver> selectAttribute(String name) {
            return new SteppingStoneReference(wrapped.selectAttribute(name));
        }

        public Reference<Resolver> selectItem(String index) {
            return new SteppingStoneReference(wrapped.selectItem(index));
        }

        public Reference<Resolver> selectItem(Expression<Integer, Resolver> index) {
            return new SteppingStoneReference(wrapped.selectItem(index));
        }

        public void document(Document target) {
            wrapped.document(target);
        }

        public Class<?> getType() {
            return wrapped.getType();
        }

    }

    private class CompoundResolver implements Resolver {

        private Resolver inner;
        private Resolver outer;

        public CompoundResolver(Resolver inner, Resolver outer) {
            this.inner = inner;
            this.outer = outer;
        }

        public Object get(String name) {
            return inner.get(name);
        }

        public Resolver getOuter() {
            return outer;
        }

    }

    public List<Binding> getBindings() {
        return innerContext.getBindings();
    }

    public Resolver getResolver(Object context, Resolver resolver) {
        Resolver inner = innerContext.getResolver(context, resolver);
        return new CompoundResolver(inner, resolver);
    }

    public void add(String name, Binding binding) {
        innerContext.add(name, binding);
    }

}
