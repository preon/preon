package nl.flotsam.preon.descriptor;

import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;
import nl.flotsam.preon.CodecDescriptor2;

public class NullCodecDescriptor2 implements CodecDescriptor2 {

    public <T extends SimpleContents<?>> Documenter<T> details(String bufferReference) {
        return new Documenter<T>() {
            public void document(T target) {
            }
        };
    }

    public String getTitle() {
        return "";
    }

    public <T extends ParaContents<?>> Documenter<T> reference(
            Adjective adjective, boolean startWithCapital) {
        return new Documenter<T>() {
            public void document(T target) {
            }
        };
    }

    public boolean requiresDedicatedSection() {
        return false;
    }

    public <T extends ParaContents<?>> Documenter<T> summary() {
        return new Documenter<T>() {
            public void document(T target) {                
            }
        };
    }

}
