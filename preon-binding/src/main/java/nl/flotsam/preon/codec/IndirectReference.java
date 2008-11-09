/**
 * 
 */
package nl.flotsam.preon.codec;

import nl.flotsam.limbo.BindingException;
import nl.flotsam.limbo.Document;
import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Reference;
import nl.flotsam.limbo.ReferenceContext;
import nl.flotsam.preon.Resolver;

/**
 * A {@link Reference} that will dereference from a {@link Reference} passed in,
 * but return an alternative {@link ReferenceContext} in
 * {@link #getReferenceContext()}.
 * 
 * @author Wilfred Springer (wis)
 * 
 */
public class IndirectReference implements Reference<Resolver> {

    private ReferenceContext<Resolver> original;

    private Reference<Resolver> redirected;

    public IndirectReference(ReferenceContext<Resolver> original,
            Reference<Resolver> redirected) {
        this.original = original;
        this.redirected = redirected;
    }

    public ReferenceContext<Resolver> getReferenceContext() {
        return original;
    }

    public Class<?> getType() {
        return redirected.getType();
    }

    public boolean isAssignableTo(Class<?> type) {
        return type.isAssignableFrom(getType());
    }

    public Object resolve(Resolver resolver) {
        System.err.println("Resolving from indirect reference: "
                + resolver.getClass());
        Thread.currentThread().dumpStack();
        return redirected.resolve((Resolver) resolver.get("outer"));
    }

    public Reference<Resolver> selectAttribute(String name)
            throws BindingException {
        return new IndirectReference(original, redirected.selectAttribute(name));
    }

    public Reference<Resolver> selectItem(String index) throws BindingException {
        return new IndirectReference(original, redirected.selectItem(index));
    }

    public Reference<Resolver> selectItem(Expression<Integer, Resolver> index)
            throws BindingException {
        return new IndirectReference(original, redirected.selectItem(index));
    }

    public void document(Document target) {
        redirected.document(target);
    }

}