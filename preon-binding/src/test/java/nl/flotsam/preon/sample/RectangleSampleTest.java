/**
 * Copyright (C) 2009 Wilfred Springer
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
package nl.flotsam.preon.sample;

import java.io.File;
import java.io.FileNotFoundException;

import junit.framework.TestCase;

import nl.flotsam.preon.Codec;
import nl.flotsam.preon.Codecs;
import nl.flotsam.preon.Codecs.DocumentType;
import nl.flotsam.preon.annotation.Bound;

public class RectangleSampleTest extends TestCase {

    public void testRectangleDocumentation() throws FileNotFoundException {
        Codec<Rectangle> codec = Codecs.create(Rectangle.class);
        File file = new File(new File(System.getProperty("java.io.tmpdir")), "rectangle.html");
        Codecs.document(codec, DocumentType.Html, file);
    }
    
    public static class Shape {
        @Bound RgbColor fillColor;
        @Bound RgbColor borderColor;
    }
    
    public static class Rectangle extends Shape {
        @Bound int x1;
        @Bound int y1;
        @Bound int x2;
        @Bound int y2;
    }
    
    public static class RgbColor {
        @Bound int red;
        @Bound int green;
        @Bound int blue;
    }
    
}
