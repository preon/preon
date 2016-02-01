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
package org.codehaus.preon.descriptors;

import junit.framework.TestCase;
import org.codehaus.preon.Codec;
import org.codehaus.preon.Codecs;
import org.codehaus.preon.Codecs.DocumentType;
import org.codehaus.preon.annotation.*;
import org.codehaus.preon.binding.StandardBindingFactory;
import org.codehaus.preon.buffer.ByteOrder;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.List;


public class DescriptionTest extends TestCase {

    /**
     * Tests if a description is generated correctly.
     *
     * @throws XMLStreamException
     * @throws FileNotFoundException
     */
    public void testDescription() throws FileNotFoundException {
        Codec<PoiData> codec = Codecs.create(PoiData.class);
        File file = new File(System.getProperty("java.io.tmpdir"));
        file = new File(file, "test.html");
        System.out.println("Writing " + file.getAbsolutePath() + "...");
        Codecs.document(codec, DocumentType.Html, file);
    }

    public void testDescriptionWithInitCodec() throws Exception {
        resetBindingId();
        Codec<SimpleData> codec1 = Codecs.create(SimpleData.class);
        resetBindingId();
        Codec<Wrapper.SimpleData> codec2 = Codecs.create(Wrapper.SimpleData.class);
        ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();

        Codecs.document(codec1, DocumentType.Html, out1);
        Codecs.document(codec2, DocumentType.Html, out2);

        assertEquals(out1.toString(), out2.toString());

    }

    public void resetBindingId() throws Exception {
        Field field = StandardBindingFactory.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(null, 0);
    }

    @Purpose("Captures point of interest data.")
    public static class PoiData {

        @BoundString(size = "3", match = "POI")
        private String magicNumber;

        /** The longitude of the POI. */
        @Bound
        private int longitude;

        @Bound
        private Poi poi;

        @BoundNumber(size = "3", byteOrder = ByteOrder.LittleEndian)
        private int version;

        @If("poi.longitude > 2")
        @BoundNumber(size = "8")
        private int numberOfPois;

        @BoundList(size = "numberOfPois", types = {ScenicViewPoi.class, HotelPoi.class})
        private List<Poi> pois;

    }

    @Purpose("Captures a single point of interest.")
    public static class Poi {

        @Bound
        @Purpose("The longitude of the location of the point of interest.")
        private long longitude;

        @Bound
        @Purpose("The latitude of the location of the point of interest.")
        private long latitude;

    }

    @TypePrefix(size = 2, value = "0b01")
    public static class ScenicViewPoi extends Poi {

    }

    @TypePrefix(size = 2, value = "0b02")
    public static class HotelPoi extends Poi {

    }


    public static class SimpleData {

        @Bound
        byte b;
    }

    public static class Wrapper {
        public static class SimpleData {

            @Bound
            byte b;

            @Init
            public void init() {
            }

            ;
        }
    }

}
