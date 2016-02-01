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
package org.codehaus.preon.sample.bmp;

import junit.framework.TestCase;
import org.codehaus.preon.Codec;
import org.codehaus.preon.Codecs;
import org.codehaus.preon.Codecs.DocumentType;
import org.codehaus.preon.DecodingException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class BitmapFileTest extends TestCase {

    public void setUp() {
    }

    public void testHeightWidth() throws FileNotFoundException,
            DecodingException, IOException {
        Codec<BitmapFile> codec = Codecs.create(BitmapFile.class);
        File file = new File(BitmapFileTest.class.getClassLoader().getResource("test.bmp").getFile());
        System.out.println(file.getAbsolutePath());
        BitmapFile bitmap = Codecs.decode(codec, file);
        assertEquals(48, bitmap.getHeight());
        assertEquals(48, bitmap.getWidth());
        for (RgbQuad quad : bitmap.getColors()) {
            System.out.print("Color ");
            System.out.print(quad.getRed());
            System.out.print(", ");
            System.out.print(quad.getGreen());
            System.out.print(", ");
            System.out.print(quad.getBlue());
            System.out.println();
        }
        System.out.println("Data " + bitmap.getData().length);
        File directory = new File(System.getProperty("java.io.tmpdir"));
        File document = new File(directory, "bitmap.html");
        Codecs.document(codec, DocumentType.Html, document);
    }

    public File getBasedir() {
        String basedir = System.getProperty("basedir");
        if (basedir == null) {
            basedir = System.getProperty("user.dir");
        }
        return new File(basedir);
    }

}
