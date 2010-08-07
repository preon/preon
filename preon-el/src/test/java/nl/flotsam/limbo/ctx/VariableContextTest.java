/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.flotsam.limbo.ctx;

import nl.flotsam.limbo.BindingException;
import nl.flotsam.limbo.Document;
import nl.flotsam.limbo.InvalidExpressionException;
import nl.flotsam.limbo.Reference;
import nl.flotsam.limbo.ctx.VariableContext;
import nl.flotsam.limbo.ctx.VariableDefinitions;
import nl.flotsam.limbo.ctx.VariableResolver;
import nl.flotsam.limbo.util.StringBuilderDocument;
import junit.framework.TestCase;


public class VariableContextTest extends TestCase {

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
