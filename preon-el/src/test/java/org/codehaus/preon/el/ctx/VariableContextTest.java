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

import org.junit.Test;
import static org.junit.Assert.*;
import org.codehaus.preon.el.BindingException;
import org.codehaus.preon.el.Document;
import org.codehaus.preon.el.InvalidExpressionException;
import org.codehaus.preon.el.Reference;
import org.codehaus.preon.el.util.StringBuilderDocument;


public class VariableContextTest {

    @Test
    public void testPropertySelectors() {
        final Data data = new Data();
        data.data = new Data();
        data.data.str = "foobar";
        VariableDefinitions defs = new DataVariableDefinitions();
        VariableContext context = new VariableContext(defs);
        Reference<VariableResolver> reference = context.selectAttribute("data");
        reference = reference.selectAttribute("data");
        reference = reference.selectAttribute("str");
        assertEquals("foobar", reference.resolve(new DataResolver(data)));
    }

    @Test
    public void testIndexSelectors() throws InvalidExpressionException {
        final Data data = new Data();
        data.datas = new Data[2];
        data.datas[0] = new Data();
        data.datas[1] = new Data();
        data.datas[0].str = "foo";
        data.datas[1].str = "bar";
        VariableDefinitions defs = new DataVariableDefinitions();
        VariableContext context = new VariableContext(defs);
        Reference<VariableResolver> reference = context.selectAttribute("data");
        reference = reference.selectAttribute("datas");
        reference = reference.selectItem("1");
        reference = reference.selectAttribute("str");
        assertEquals("bar", reference.resolve(new DataResolver(data)));
    }

    @Test
    public void testReferenceEquality() throws InvalidExpressionException {
        VariableDefinitions defs = new DataVariableDefinitions();
        VariableContext context = new VariableContext(defs);
        Reference<VariableResolver> reference = context.selectAttribute("data");

        // VariableReference equality
        assertEquals(reference, reference);

        // PropertyReference equality
        reference = reference.selectAttribute("data");
        assertEquals(reference, reference);

        // IndexReference equality
        reference = reference.selectItem("data.number");
        assertEquals(reference, reference);
    }

    @Test
    public void testDocumentation() throws InvalidExpressionException {
        VariableDefinitions defs = new DataVariableDefinitions();
        VariableContext context = new VariableContext(defs);
        Reference<VariableResolver> reference = context.selectAttribute("data");
        Document document = null;

        document = new StringBuilderDocument();
        reference.document(document);
        assertEquals("the data", document.toString());

        documentProperty(reference);
        documentArrayElement(context, reference);
        documentPropertyOfArrayElement(context, reference);
    }


    private void documentArrayElement(VariableContext context,
            Reference<VariableResolver> reference)
            throws InvalidExpressionException {
        Document document;
        reference = reference.selectAttribute("datas");
        reference = reference.selectItem("1");
        document = new StringBuilderDocument();
        reference.document(document);
        assertEquals("the second element of the datas (a Data[]) of the data",
                document.toString());
    }

    private void documentPropertyOfArrayElement(VariableContext context,
            Reference<VariableResolver> reference)
            throws InvalidExpressionException {
        Document document;
        reference = reference.selectAttribute("datas");
        reference = reference.selectItem("1");
        reference = reference.selectAttribute("number");
        document = new StringBuilderDocument();
        reference.document(document);
        assertEquals(
                "the number (a int) of the second element of the datas (a Data[]) of the data",
                document.toString());
    }

    private void documentProperty(Reference<VariableResolver> reference) {
        Document document;
        reference = reference.selectAttribute("number");
        document = new StringBuilderDocument();
        reference.document(document);
        assertEquals("the number (a int) of the data", document.toString());
    }

    public static class Data {

        public int number;

        public Data data;

        public String str;

        public Data[] datas;

        public int[] ints;

    }

    public static class DataResolver implements VariableResolver {

        private Data data;

        public DataResolver(Data data) {
            this.data = data;
        }

        public Object get(String name) {
            if ("data".equals(name)) {
                return data;
            } else {
                throw new BindingException("No such variable: " + name); // TODO
            }
        }

    }

    private static class DataVariableDefinitions implements VariableDefinitions {

        public String[] getNames() {
            return new String[] { "data" };
        }

        public Class getType(String name) {
            return Data.class;
        }

        public void document(Document target) {
            target.text("an object with variable data");
        }

    }

}
