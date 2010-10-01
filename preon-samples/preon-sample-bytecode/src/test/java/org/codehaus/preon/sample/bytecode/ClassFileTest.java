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
        File root = new File("/tmp");
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
