package nl.flotsam.preon.descriptor;

import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import nl.flotsam.preon.CodecDescriptor2;

public class PassThroughCodecDescriptor2 implements CodecDescriptor2 {

    private CodecDescriptor2 delegate;
    private boolean requiresDedicatedSection;

    public PassThroughCodecDescriptor2(CodecDescriptor2 delegate, boolean requiresDedicatedSection) {
        this.delegate = delegate;
        this.requiresDedicatedSection = requiresDedicatedSection;
    }
    
    public <C extends SimpleContents<?>> Documenter<C> details(String bufferReference) {
        return delegate.details(bufferReference);
    }

    public String getTitle() {
        return delegate.getTitle();
    }

    public <C extends ParaContents<?>> Documenter<C> reference(
            Adjective adjective) {
        return delegate.reference(adjective);
    }

    public boolean requiresDedicatedSection() {
        return requiresDedicatedSection;
    }

    public <C extends ParaContents<?>> Documenter<C> summary() {
        return delegate.summary();
    }
    
}
