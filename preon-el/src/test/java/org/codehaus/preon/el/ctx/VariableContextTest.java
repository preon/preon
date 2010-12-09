/**
 * Copyright (C) 2009-2010 Wilfred Springer
 *
 * This file is part of Preon.
 *
 * Preon is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * Preon is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Preon; see the file COPYING. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
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
