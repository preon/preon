package nl.flotsam.limbo.ast;

import nl.flotsam.limbo.BindingException;
import nl.flotsam.limbo.Document;
import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Reference;
import nl.flotsam.limbo.ReferenceContext;
import nl.flotsam.limbo.ast.ArithmeticNode.Operator;
import nl.flotsam.limbo.util.StringBuilderDocument;
import junit.framework.TestCase;

/**
 * A number of tests for expression simplification.
 * 
 * @author Wilfred Springer (wis)
 * 
 */
public class NodeSimplificationTest extends TestCase {

    public void testSimplifySimple() {
        IntegerNode<Object> node1 = new IntegerNode<Object>(12);
        IntegerNode<Object> node2 = new IntegerNode<Object>(5);
        ArithmeticNode<Object> node3 = new ArithmeticNode<Object>(Operator.plus, node1, node2);
        node3.simplify();
        StringBuilderDocument doc = new StringBuilderDocument();
        node3.document(doc);
        assertEquals("the sum of 12 and 5", doc.toString());
        Node<Integer, Object> node4 = node3.simplify();
        doc = new StringBuilderDocument();
        node4.document(doc);
        assertEquals("17", doc.toString());
    }

    public void testSimplifyTriple() {
        IntegerNode<Object> node1 = new IntegerNode<Object>(12);
        IntegerNode<Object> node2 = new IntegerNode<Object>(5);
        IntegerNode<Object> node3 = new IntegerNode<Object>(5);
        ArithmeticNode<Object> node4 = new ArithmeticNode<Object>(Operator.plus, node1, node2);
        node4 = new ArithmeticNode<Object>(Operator.plus, node4, node3);
        Node<Integer, Object> result = node4.simplify();
        StringBuilderDocument doc = new StringBuilderDocument();
        result.document(doc);
        assertEquals("22", doc.toString());
    }

    public void testTripleWithVarible() {
        IntegerNode<Object> node1 = new IntegerNode<Object>(12);
        IntegerNode<Object> node2 = new IntegerNode<Object>(5);
        IntegerReferenceNode<Object> node3 = new IntegerReferenceNode<Object>(
                new TestReference<Object>("a"));
        ArithmeticNode<Object> node4 = new ArithmeticNode<Object>(Operator.plus, node1, node2);
        node4 = new ArithmeticNode<Object>(Operator.plus, node4, node3);
        Node<Integer, Object> result = node4.simplify();
        StringBuilderDocument doc = new StringBuilderDocument();
        result.document(doc);
        assertEquals("the sum of 17 and a", doc.toString());
    }

    private class TestReference<Object> implements Reference<Object> {

        private String name;

        public TestReference(String name) {
            this.name = name;
        }

        public ReferenceContext<Object> getReferenceContext() {
            return null;
        }

        public boolean isAssignableTo(Class<?> type) {
            return false;
        }

        public java.lang.Object resolve(Object context) {
            return null;
        }

        public Reference<Object> selectAttribute(String name) throws BindingException {
            return null;
        }

        public Reference<Object> selectItem(String index) throws BindingException {
            return null;
        }

        public Reference<Object> selectItem(Expression<Integer, Object> index)
                throws BindingException {
            return null;
        }

        public void document(Document target) {
            target.text(name);
        }

        public Class<?> getType() {
            return null;
        }

        public Reference<Object> narrow(Class<?> type) {
            return null;
        }

    }

}
