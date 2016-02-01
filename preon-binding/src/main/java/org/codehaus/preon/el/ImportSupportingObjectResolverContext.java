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
package org.codehaus.preon.el;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.preon.el.BindingException;
import org.codehaus.preon.el.Document;
import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.ReferenceContext;
import org.codehaus.preon.Resolver;
import org.codehaus.preon.binding.Binding;

public class ImportSupportingObjectResolverContext implements
        ObjectResolverContext {

    private ObjectResolverContext context;

    private Map<String, Reference<Resolver>> references;

    public void add(String name, Binding binding) {
        context.add(name, binding);
    }

    public List<Binding> getBindings() {
        return context.getBindings();
    }

    public Resolver getResolver(Object context, Resolver resolver) {
        return this.context.getResolver(context, resolver);
    }

    public Reference<Resolver> selectAttribute(String name)
            throws BindingException {
        if (references.containsKey(name)) {
            return references.get(name);
        } else {
            return context.selectAttribute(name);
        }
    }

    public Reference<Resolver> selectItem(String expr) throws BindingException {
        throw new BindingException("No indexes supported.");
    }

    public Reference<Resolver> selectItem(Expression<Integer, Resolver> expr)
            throws BindingException {
        throw new BindingException("No indexes supported.");
    }

    public void document(Document doc) {
        // Not expected to be called
    }

    public static ObjectResolverContext decorate(ObjectResolverContext context,
                                                 Class<?> type) {
        if (type.isAnnotationPresent(ImportStatic.class)) {
            ImportSupportingObjectResolverContext replacement = new ImportSupportingObjectResolverContext();
            Map<String, Reference<Resolver>> references = new HashMap<String, Reference<Resolver>>();
            for (Class<?> imported : type.getAnnotation(ImportStatic.class)
                    .value()) {
                references.put(imported.getSimpleName(), new ClassReference(
                        imported, replacement));
            }
            replacement.context = context;
            replacement.references = references;
            return replacement;
        } else {
            return context;
        }
    }

    public static class ClassReference implements Reference<Resolver> {

        private final Class<?> imported;

        private ReferenceContext<Resolver> context;

        public ClassReference(Class<?> imported,
                              ReferenceContext<Resolver> context) {
            this.imported = imported;
            this.context = context;
        }

        public ReferenceContext<Resolver> getReferenceContext() {
            return context;
        }

        public Class<?> getType() {
            return imported;
        }

        public boolean isAssignableTo(Class<?> other) {
            return false;
        }

        public Reference<Resolver> narrow(Class<?> other) {
            return this; // Forgot how to implement this
        }

        public boolean isBasedOn(ReferenceContext<Resolver> resolverReferenceContext) {
            return true;
        }

        public Reference<Resolver> rescope(ReferenceContext<Resolver> resolverReferenceContext) {
            return this;
        }

        public Object resolve(Resolver resolver) {
            return imported;
        }

        public Reference<Resolver> selectAttribute(String name)
                throws BindingException {
            Field fld = null;
            try {
                fld = imported.getField(name);
                if (fld == null || !Modifier.isStatic(fld.getModifiers())) {
                    throw new BindingException("Class "
                            + imported.getSimpleName()
                            + " does not define field " + name);
                } else {
                    fld.setAccessible(true);
                    return new StaticFieldReference(fld, context);
                }
            }
            catch (SecurityException e) {
                throw new BindingException("Not allowed to access "
                        + fld.getName());
            }
            catch (NoSuchFieldException e) {
                throw new BindingException("No attribute called " + name
                        + " defined.");
            }
        }

        public Reference<Resolver> selectItem(String expr)
                throws BindingException {
            throw new BindingException("No indexed values on class "
                    + imported.getSimpleName());
        }

        public Reference<Resolver> selectItem(Expression<Integer, Resolver> expr)
                throws BindingException {
            throw new BindingException("No indexed values on class "
                    + imported.getSimpleName());
        }

        public void document(Document document) {
            // Not expected to be called
            assert false;
        }

    }

    private static class StaticFieldReference implements Reference<Resolver> {

        private final Field fld;

        private final ReferenceContext<Resolver> context;

        public StaticFieldReference(Field fld,
                                    ReferenceContext<Resolver> context) {
            this.fld = fld;
            this.context = context;
        }

        public ReferenceContext<Resolver> getReferenceContext() {
            return context;
        }

        public Class<?> getType() {
            return fld.getType();
        }

        public boolean isAssignableTo(Class<?> other) {
            return other.isAssignableFrom(fld.getType());
        }

        public Reference<Resolver> narrow(Class<?> other) {
            return this;
        }

        public boolean isBasedOn(ReferenceContext<Resolver> resolverReferenceContext) {
            return true;
        }

        public Reference<Resolver> rescope(ReferenceContext<Resolver> resolverReferenceContext) {
            return this;
        }

        public Object resolve(Resolver resolver) {
            try {
                return fld.get(null);
            }
            catch (IllegalArgumentException e) {
                throw new BindingException("Failed to resolve field value.", e);
            }
            catch (IllegalAccessException e) {
                throw new BindingException("Failed to resolve field value.", e);
            }
        }

        public Reference<Resolver> selectAttribute(String name)
                throws BindingException {
            throw new BindingException("No more attributes supported.");
        }

        public Reference<Resolver> selectItem(String expr)
                throws BindingException {
            throw new BindingException("No indexes supported.");
        }

        public Reference<Resolver> selectItem(Expression<Integer, Resolver> arg0)
                throws BindingException {
            throw new BindingException("No indexes supported.");
        }

        public void document(Document doc) {
            doc.text(fld.getName());
        }

    }

}
