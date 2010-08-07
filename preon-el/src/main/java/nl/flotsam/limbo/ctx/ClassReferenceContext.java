package nl.flotsam.limbo.ctx;

import nl.flotsam.limbo.BindingException;
import nl.flotsam.limbo.Document;
import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Reference;
import nl.flotsam.limbo.ReferenceContext;

public class ClassReferenceContext<T> implements Reference<T> {

    private Class<T> type;

    public ClassReferenceContext(Class<T> type) {
        this.type = type;
    }

    public ReferenceContext<T> getReferenceContext() {
        return this;
    }

    public boolean isAssignableTo(Class<?> type) {
        return this.type.isAssignableFrom(type);
    }

    public Object resolve(T context) {
        return context;
    }

    public Reference<T> selectAttribute(String name) throws BindingException {
        return new PropertyReference<T>(this, type, name, this);
    }

    public Reference<T> selectItem(String index) throws BindingException {
        throw new BindingException("Items not supported.");
    }

    public Reference<T> selectItem(Expression<Integer, T> index)
            throws BindingException {
        throw new BindingException("Items not supported.");
    }

    public void document(Document target) {
        target.text("a " + type.getSimpleName());
    }

    public Class<?> getType() {
        return type;
    }

    public Reference<T> narrow(Class<?> type) {
        if (type.isAssignableFrom(type)) {
            return this;
        } else {
            return null;
        }
    }

}
