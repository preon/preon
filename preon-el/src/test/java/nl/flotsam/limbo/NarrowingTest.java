package nl.flotsam.limbo;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import nl.flotsam.limbo.ctx.MultiReference;
import junit.framework.TestCase;

public class NarrowingTest extends TestCase {

    private ReferenceContext<Object> context = createMock(ReferenceContext.class);
    private Reference<Object> reference1 = createMock(Reference.class);
    private Reference<Object> reference2 = createMock(Reference.class);

    public void testAssignable() {
        assertTrue(Number.class.isAssignableFrom(Long.class));
        assertFalse(Long.class.isAssignableFrom(Number.class));
    }
    
    public void testNoNarrowingPossible() {
        expect(context.selectAttribute("a")).andReturn(reference1);
        expect(reference1.getType()).andStubReturn(Integer.class);
        reference1.document((Document) anyObject());
        replay(context, reference1);
        try {
            Expression expr = Expressions.createBoolean(context, "a=='123'");
            fail("Expected binding exception.");
        } catch (BindingException be) {
            // Ok
        }
        verify(context, reference1);
    }

    public void testNoNarrowingNeeded() {
        expect(context.selectAttribute("a")).andReturn(reference1);
        expect(reference1.getType()).andStubReturn(String.class);
        replay(context, reference1);
        Expression expr = Expressions.createBoolean(context, "a=='123'");
        verify(context, reference1);
    }

    public void testNarrowingPossibleAndNeeded() {
        expect(context.selectAttribute("a")).andReturn(reference1);
        expect(reference1.getType()).andStubReturn(Object.class);
        expect(reference1.narrow(String.class)).andReturn(reference2);
        expect(reference2.getType()).andStubReturn(String.class);
        replay(context, reference1, reference2);
        Expression expr = Expressions.createBoolean(context, "a=='123'");
        verify(context, reference1, reference2);
    }
    
}
