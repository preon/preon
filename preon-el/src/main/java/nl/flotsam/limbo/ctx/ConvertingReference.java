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

import nl.flotsam.limbo.BindingException;
import nl.flotsam.limbo.Document;
import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Reference;
import nl.flotsam.limbo.ReferenceContext;
import nl.flotsam.limbo.util.Converter;
import nl.flotsam.limbo.util.Converters;

public class ConvertingReference<T, E> implements Reference<E> {

    private Reference<E> reference;
    
    private Class<T> type;
    
    private Converter<Object, Class<T>> converter;
    
    public ConvertingReference(Class<T> type, Reference<E> reference) {
        this.type = type;
        this.reference = reference;
        this.converter = (Converter<Object, Class<T>>) Converters.get(reference.getType(), type);
    }
    
    public ReferenceContext<E> getReferenceContext() {
        return reference.getReferenceContext();
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isAssignableTo(Class<?> type) {
        return type.isAssignableFrom(this.type);
    }

    public Object resolve(E context) {
        return converter.convert(reference.resolve(context));
    }

    public Reference<E> selectAttribute(String name) throws BindingException {
        return reference.selectAttribute(name);
    }

    public Reference<E> selectItem(String index) throws BindingException {
        return reference.selectItem(index);
    }

    public Reference<E> selectItem(Expression<Integer, E> index) throws BindingException {
        return reference.selectItem(index);
    }

    public void document(Document target) {
        reference.document(target);
    }
    
    public static <T,E> ConvertingReference<T, E> create(Class<T> type, Reference<E> reference) {
        return new ConvertingReference<T, E>(type, reference);
    }

    public Reference<E> narrow(Class<?> type) throws BindingException {
        if (this.type.isAssignableFrom(type)) {
            return this;
        } else {
            return null;
        }
    }

}
