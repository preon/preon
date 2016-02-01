/**
 * Copyright (c) 2009-2016 Wilfred Springer
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package org.codehaus.preon.el.ctx;

import org.codehaus.preon.el.*;
import org.codehaus.preon.el.util.StringBuilderDocument;
import static org.junit.Assert.*;
import org.junit.Test;

import static org.codehaus.preon.el.Bindings.EarlyBinding;

public class ClassReferenceContextTest {

    @Test
    public void testFromBindings() {
        Expression<Integer, Person> expr = Expressions.from(Person.class)
                .using(EarlyBinding).toInteger("age * 2");
        Person wilfred = new Person();
        wilfred.name = "Wilfred";
        wilfred.age = 35;
        assertEquals(70, expr.eval(wilfred).intValue());
    }
    @Test
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

    @Test(expected=BindingException.class)
    public void testInvalidReferences() {
        ReferenceContext<Person> context = new ClassReferenceContext<Person>(
                Person.class);
        context.selectAttribute("gender");
    }

    private static class Person {
        String name;
        int age;
        Person father;
        Person mother;
    }

}
