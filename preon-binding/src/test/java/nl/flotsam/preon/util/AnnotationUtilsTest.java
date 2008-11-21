package nl.flotsam.preon.util;

import junit.framework.TestCase;
import nl.flotsam.preon.annotation.Purpose;
import nl.flotsam.preon.annotation.TypePrefix;

/**
 * Just checking the walls for some Annotation properties.
 * 
 * @author Wilfred Springer (wis)
 * 
 */
public class AnnotationUtilsTest extends TestCase {

    public void testEquals() {
        assertTrue(AnnotationUtils.equivalent(Test1.class, Test2.class));
        assertFalse(AnnotationUtils.equivalent(Test2.class, Test3.class));
        assertFalse(AnnotationUtils.equivalent(Test3.class, Test4.class));
        assertFalse(AnnotationUtils.equivalent(Test4.class, Test5.class));
        assertFalse(AnnotationUtils.equivalent(Test5.class, Test4.class));
    }

    @TypePrefix(size = 2, value = "blaat")
    @Purpose("whatever")
    public static class Test1 {
    }

    @TypePrefix(size = 2, value = "blaat")
    @Purpose("whatever")
    public static class Test2 {
    }
    
    @TypePrefix(size = 2, value = "blaat")
    @Purpose("foobar")
    public static class Test3 {
    }

    @Purpose("foobar")
    public static class Test4 {
    }

    @TypePrefix(size = 2, value = "blaat")
    public static class Test5 {
    }

}
