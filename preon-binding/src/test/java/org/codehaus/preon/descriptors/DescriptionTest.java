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
