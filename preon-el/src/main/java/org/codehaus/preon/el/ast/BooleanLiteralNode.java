package org.codehaus.preon.el.ast;

import org.codehaus.preon.el.Document;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.ReferenceContext;

import java.util.Set;

public class BooleanLiteralNode<E> extends AbstractNode<Boolean, E> {

    private boolean value;

    public BooleanLiteralNode(boolean value) {
        this.value = value;
    }
    
    public Boolean eval(E context) {
        return value;
    }

    public Class<Boolean> getType() {
        return Boolean.class;
    }

    public Node<Boolean, E> simplify() {
        return this;
    }

    public Node<Boolean, E> rescope(ReferenceContext<E> eReferenceContext) {
        return this;
    }

    public void gather(Set<Reference<E>> references) {
        // Nothing to do
    }

    public boolean isParameterized() {
        return false;
    }

    public void document(Document target) {
        target.text(Boolean.toString(value));
    }
}
