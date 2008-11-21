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
