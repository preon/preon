package org.codehaus.preon.el;

/**
 * A context that 'imports' constant references such as true and false.
 *
 * @param <E>
 */
public class ImplicitsContext<E> implements ReferenceContext<E> {

    private ReferenceContext<E> nested;

    public ImplicitsContext(ReferenceContext<E> nested) {
        this.nested = nested;
    }

    public Reference<E> selectAttribute(String name) throws BindingException {
        if ("true".equals(name) || "false".equals(name)) {
            return new BooleanLiteralReference<E>(Boolean.parseBoolean(name), this);
        } else {
            return nested.selectAttribute(name);
        }
    }

    public Reference<E> selectItem(String index) throws BindingException {
        return nested.selectItem(index);
    }

    public Reference<E> selectItem(Expression<Integer, E> index) throws BindingException {
        return nested.selectItem(index);
    }

    public void document(Document target) {
        nested.document(target);
    }

}

