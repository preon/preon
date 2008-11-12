package nl.flotsam.preon.rendering;

import junit.framework.TestCase;

public class CamelCaseRewriterTest extends TestCase {

    public void testRewriting() {
        CamelCaseRewriter rewriter = new CamelCaseRewriter();
        assertEquals(rewriter.rewrite("abcDefGhi"), "Abc def ghi");
        rewriter = new CamelCaseRewriter(false);
        assertEquals(rewriter.rewrite("abcDefGhi"), "abc def ghi");
        rewriter = new CamelCaseRewriter(true);
        assertEquals(rewriter.rewrite("abcDefGhi"), "Abc def ghi");
    }

}
