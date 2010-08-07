package nl.flotsam.limbo;

import nl.flotsam.limbo.ctx.ClassReferenceContext;

public enum Bindings implements Binding {

    LateBinding {

        public <C> ReferenceContext<C> create(Class<C> type) {
            return new ClassReferenceContext<C>(type);
        }

    },
    EarlyBinding {
        public <C> ReferenceContext<C> create(Class<C> type) {
            return new ClassReferenceContext<C>(type);
        }

    };

    public abstract <C> ReferenceContext<C> create(Class<C> type);

}
