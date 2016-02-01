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
package org.codehaus.preon.sample.bytecode;

import org.apache.commons.io.IOUtils;
import org.codehaus.preon.Codec;
import org.codehaus.preon.Codecs;
import org.codehaus.preon.Codecs.DocumentType;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.emitter.Exporter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class ClassFileTest {

    private Codec<ClassFile> codec;
    private static byte[] bytecode;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void loadBytecode() throws IOException {
        InputStream in = null;
        try {
            in = ClassFileTest.class.getResourceAsStream("/Foo.class");
            bytecode = IOUtils.toByteArray(in);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    @Before
    public void constructCodec() {
        codec = Codecs.create(ClassFile.class);
    }

    @Test
    public void printDocumentation() throws FileNotFoundException {
        File directory = new File(System.getProperty("java.io.tmpdir"));
        File file = new File(directory, "documentation.html");
        Codecs.document(codec, DocumentType.Html, file);
    }

    @Test
    public void shouldDecodeClassFile() throws IOException, DecodingException {
        ClassFile classFile = Codecs.decode(codec, bytecode);
        assertThat(classFile, is(not(nullValue())));
    }

    @Test
    public void shouldCorrectlyDocument() throws IOException {
        File file = File.createTempFile("preon", ".html");
        file.deleteOnExit();
        Codecs.document(codec, DocumentType.Html, file);
    }

    @Test
    public void shouldExportCorrectly() throws DecodingException, IOException {
        File root = new File(System.getProperty("java.io.tmpdir"));
        Exporter.decodeAndExport(ClassFile.class, ByteBuffer.wrap(bytecode), new File(root, "hello.html"));
//        assertThat(Arrays.asList(root.list()),
//                hasItems("hello-structure.xml", "hello-contents.txt"));
//        System.out.println(readFileToString(new File(root, "hello-structure.xml"), "UTF-8"));
//        File contentsFile = new File(root, "hello-contents.txt");
//        assertThat(contentsFile.exists(), is(true));
//        assertThat(contentsFile.length(), is(greaterThan(0L)));
//        System.out.println(readFileToString(new File(root, "hello-contents.txt"), "UTF-8"));
    }



}
