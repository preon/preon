package org.codehaus.preon.el;

/**
 * Reference to boolean literal values.
 *
 * @param <E>
 */
public class BooleanLiteralReference<E> implements Reference<E> {

    private boolean value;
    private ReferenceContext<E> context;

    public BooleanLiteralReference(boolean value, ReferenceContext<E> context) {
        this.value = value;
        this.context = context;
    }
    
    public Object resolve(E context) {
        return value;
    }

    public ReferenceContext<E> getReferenceContext() {
        return context;
    }

    public boolean isAssignableTo(Class<?> type) {
        return type.isAssignableFrom(Boolean.class);
    }

    public Class<?> getType() {
        return Boolean.class;
    }

    public Reference<E> narrow(Class<?> type) {
        return this;
    }

    public boolean isBasedOn(ReferenceContext<E> eReferenceContext) {
        return false;
    }

    public Reference<E> rescope(ReferenceContext<E> eReferenceContext) {
        return this;
    }

    public Reference<E> selectAttribute(String name) throws BindingException {
        throw new BindingException("No such attribute");
    }

    public Reference<E> selectItem(String index) throws BindingException {
        throw new BindingException("No such indexed value");
    }

    public Reference<E> selectItem(Expression<Integer, E> index) throws BindingException {
        throw new BindingException("No such indexed value");
    }

    public void document(Document target) {
        target.text(Boolean.toString(value));
    }
}
