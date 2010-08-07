package nl.flotsam.limbo.ctx;

import static nl.flotsam.limbo.Bindings.EarlyBinding;
import junit.framework.TestCase;
import nl.flotsam.limbo.BindingException;
import nl.flotsam.limbo.Expression;
import nl.flotsam.limbo.Expressions;
import nl.flotsam.limbo.Reference;
import nl.flotsam.limbo.ReferenceContext;
import nl.flotsam.limbo.util.StringBuilderDocument;

public class ClassReferenceContextTest extends TestCase {

    public void testFromBindings() {
        Expression<Integer, Person> expr = Expressions.from(Person.class)
                .using(EarlyBinding).toInteger("age * 2");
        Person wilfred = new Person();
        wilfred.name = "Wilfred";
        wilfred.age = 35;
        assertEquals(70, expr.eval(wilfred).intValue());
    }

    public void testValidReferences() {
        ReferenceContext<Person> context = new ClassReferenceContext<Person>(
                Person.class);
        Reference<Person> personsName = context.selectAttribute("name");
        Reference<Person> fathersName = context.selectAttribute("father")
                .selectAttribute("name");
        Person wilfred = new Person();
        wilfred.name = "Wilfred";
        wilfred.age = 35;
        Person levi = new Person();
        levi.name = "Levi";
        levi.age = 8;
        levi.father = wilfred;
        assertEquals("Levi", personsName.resolve(levi));
        assertEquals("Wilfred", fathersName.resolve(levi));
        assertEquals("Wilfred", personsName.resolve(wilfred));
        StringBuilder builder = new StringBuilder();
        fathersName.document(new StringBuilderDocument(builder));
        System.out.println(builder.toString());
    }

    public void testInvalidReferences() {
        ReferenceContext<Person> context = new ClassReferenceContext<Person>(
                Person.class);
        try {
            context.selectAttribute("gender");
            fail("Expected binding exception.");
        } catch (BindingException be) {
            // Ok!
        }
    }

    private static class Person {
        String name;
        int age;
        Person father;
        Person mother;
    }

}
