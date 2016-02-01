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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.preon.el.*;
import org.codehaus.preon.el.ctx.MultiReference;
import org.codehaus.preon.el.util.StringBuilderDocument;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.ResolverContext;
import org.codehaus.preon.binding.Binding;
import org.codehaus.preon.util.ParaContentsDocument;

/**
 * A {@link ResolverContext} based on a collection of {@link Binding Bindings}.
 *
 * @author Wilfred Springer (wis)
 */
public class BindingsContext implements ObjectResolverContext {

    /** All {@link Binding}s. */
    private List<Binding> orderedBindings;

    /** All {@link Binding}s, indexed by their name. */
    private HashMap<String, Binding> bindingsByName;

    /** The "outer" {@link ResolverContext}. */
    private ResolverContext outer;

    /**
     * Constructs a new instance.
     *
     * @param type
     * @param outer The "outer" {@link ResolverContext}.
     */
    public BindingsContext(Class<?> type, ResolverContext outer) {
        this.orderedBindings = new ArrayList<Binding>();
        this.bindingsByName = new HashMap<String, Binding>();
        this.outer = outer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.ObjectResolverContext#add(java.lang.String,
     * org.codehaus.preon.binding.Binding)
     */

    public void add(String name, Binding binding) {
        orderedBindings.add(binding);
        bindingsByName.put(name, binding);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.ReferenceContext#selectAttribute(java.lang.String)
     */

    public Reference<Resolver> selectAttribute(String name)
            throws BindingException {
        if ("outer".equals(name)) {
            return new OuterReference(outer, this);
        } else {
            Binding binding = bindingsByName.get(name);

            if (binding == null) {
                throw new BindingException(
                        "Failed to create binding for bound data called "
                                + name);
            }
            return new BindingReference(binding);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.ReferenceContext#selectItem(java.lang.String)
     */

    public Reference<Resolver> selectItem(String index) throws BindingException {
        throw new BindingException("Cannot resolve index on BindingContext.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.codehaus.preon.el.ReferenceContext#selectItem(org.codehaus.preon.el.Expression)
     */

    public Reference<Resolver> selectItem(Expression<Integer, Resolver> index)
            throws BindingException {
        StringBuilder builder = new StringBuilder();
        index.document(new StringBuilderDocument(builder));
        throw new BindingException("Cannot resolve index on BindingContext.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.Descriptive#document(org.codehaus.preon.el.Document)
     */

    public void document(Document target) {
        if (bindingsByName.size() > 0) {
            target.text("one of ");
            boolean passedFirst = false;
            for (Binding binding : bindingsByName.values()) {
                if (passedFirst) {
                    target.text(", ");
                }
                target.text(binding.getName());
                passedFirst = true;
            }
        } else {
            target.text("no variables");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.codehaus.preon.el.ObjectResolverContext#getResolver(java.lang.Object
     * , org.codehaus.preon.Resolver)
     */

    public Resolver getResolver(Object context, Resolver resolver) {
        return new BindingsResolver(context, resolver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.el.ObjectResolverContext#getBindings()
     */

    public List<Binding> getBindings() {
        return orderedBindings;
    }

    /** A {@link Reference} referring to a {@link Binding}. */
    private class BindingReference implements Reference<Resolver> {

        /** The {@link Binding} it refers to. */
        private Binding binding;

        /** The most specific supertype of anything that can be returned from this {@link Reference}. */
        private Class<?> commonType;

        /**
         * Constructs a new instance.
         *
         * @param binding The {@link Binding}.
         */
        public BindingReference(Binding binding) {
            this.binding = binding;
            commonType = binding.getType();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.codehaus.preon.el.Reference#getReferenceContext()
         */

        public ResolverContext getReferenceContext() {
            return BindingsContext.this;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.codehaus.preon.el.Reference#isAssignableTo(java.lang.Class)
         */

        public boolean isAssignableTo(Class<?> type) {

            for (Class<?> bound : binding.getTypes()) {

                if (bound.isAssignableFrom(type)) {
                    return true;
                }
            }

            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.codehaus.preon.el.Reference#resolve(java.lang.Object)
         */

        public Object resolve(Resolver context) {
            try {
                String name = binding.getName();
                return context.get(name);
            } catch (IllegalArgumentException e) {
                throw new BindingException("Failed to bind to "
                        + binding.getName(), e);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.codehaus.preon.el.ReferenceContext#selectAttribute(java.lang.String)
         */

        @SuppressWarnings("unchecked")
        public Reference<Resolver> selectAttribute(String name) {
            Reference<Resolver>[] template = new Reference[0];
            List<Reference<Resolver>> references = new ArrayList<Reference<Resolver>>();
            for (Class<?> bound : binding.getTypes()) {
                try {
                    references.add(new PropertyReference(this, bound, name,
                            BindingsContext.this, false));
                } catch (BindingException be) {
                    // Ok, let's skip this one.
                }
            }
            if (references.size() == 0) {
                throw new BindingException("Attribute " + name
                        + " not defined for any type.");
            } else {
                return new MultiReference<Resolver>(references
                        .toArray(template));
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.codehaus.preon.el.ReferenceContext#selectItem(java.lang.String)
         */

        public Reference<Resolver> selectItem(String index) {
            Expression<Integer, Resolver> expr;
            expr = Expressions.createInteger(BindingsContext.this, index);

            return selectItem(expr);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.codehaus.preon.el.ReferenceContext#selectItem(org.codehaus.preon.el.Expression
         * )
         */

        @SuppressWarnings("unchecked")
        public Reference<Resolver> selectItem(
                Expression<Integer, Resolver> index) {

            if (binding.getTypes().length > 1) {
                Reference<Resolver>[] references = new Reference[binding
                        .getTypes().length];

                for (int i = 0; i < binding.getTypes().length; i++) {
                    references[i] = new ArrayElementReference(this, binding
                            .getTypes()[i], index, BindingsContext.this);
                }

                return new MultiReference<Resolver>(references);
            } else {
                return new ArrayElementReference(this, binding.getType()
                        .getComponentType(), index, BindingsContext.this);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.codehaus.preon.el.Descriptive#document(org.codehaus.preon.el.Document)
         */

        public void document(final Document target) {
            binding.writeReference(new ParaContentsDocument(target));
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.codehaus.preon.el.Reference#getType()
         */

        public Class<?> getType() {
            return commonType;
        }

        public Reference<Resolver> narrow(Class<?> type) {
            if (type.isAssignableFrom(binding.getType())) {
                return this;
            } else {
                return null;
            }
        }

        public boolean isBasedOn(ReferenceContext<Resolver> context) {
            return BindingsContext.this.equals(context);
        }

        public Reference<Resolver> rescope(ReferenceContext<Resolver> context) {
            if (BindingsContext.this.equals(context)) {
                return this;
            } else {
                throw new IllegalStateException();
            }
        }

    }

    /**
     * A {@link Resolver} resolving to bindings. In addition, it also resolves outer.
     *
     * @author Wilfred Springer (wis)
     */
    private class BindingsResolver implements Resolver {

        /** The instance on which the objects need to be resolved. */
        private Object context;

        /** The outer Resolver. */
        private Resolver outer;

        /**
         * Constructs a new instance.
         *
         * @param context The object for resolving bindings.
         * @param outer   A reference to the outer context.
         */
        public BindingsResolver(Object context, Resolver outer) {
            this.context = context;
            this.outer = outer;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.codehaus.preon.Resolver#get(java.lang.String)
         */

        public Object get(String name) {
            if ("outer".equals(name)) {
                return outer;
            } else {

                if (bindingsByName.containsKey(name)) {
                    Binding binding = bindingsByName.get(name);

                    if (context == null) {
                        StringBuilderDocument document = new StringBuilderDocument();
                        // TODO:
//                        binding.describe(new ParaContentsDocument(document));
                        throw new BindingException("Failed to resolve "
                                + document.toString()
                                + " due to incomplete context.");
                    }

                    try {
                        return binding.get(context);
                    } catch (IllegalArgumentException e) {
                        throw new BindingException("Failed to bind to "
                                + binding.getName(), e);
                    } catch (IllegalAccessException e) {
                        throw new BindingException("Forbidded to access "
                                + binding.getName(), e);
                    }
                } else {
                    throw new BindingException("Failed to resolve " + name
                            + " on " + context.getClass());
                }
            }
        }

        /** Returns the "outer" {@link Resolver}. */
        public Resolver getOuter() {
            return outer;
        }

        /** Returns the {@link Resolver} from which all new references need to be constructed. */
        public Resolver getOriginalResolver() {
            return this;
        }

    }

}
