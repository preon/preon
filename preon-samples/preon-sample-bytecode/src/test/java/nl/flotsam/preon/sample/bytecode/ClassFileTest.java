package nl.flotsam.preon.sample.bytecode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import nl.flotsam.preon.Codec;
import nl.flotsam.preon.Codecs;
import nl.flotsam.preon.DecodingException;
import nl.flotsam.preon.codec.LoggingDecorator;

import junit.framework.TestCase;

public class ClassFileTest extends TestCase {

    public void testDecoding() throws FileNotFoundException, IOException, DecodingException {
        Codec<ClassFile> codec = Codecs.create(ClassFile.class, new LoggingDecorator());
//        ClassFile classFile = Codecs.decode(codec, new File(getBasedir(),
//                "target/classes/nl/flotsam/preon/sample/bytecode/ClassFile.class"));
    }

    public File getBasedir() {
        String basedir = System.getProperty("basedir");
        if (basedir == null) {
            basedir = System.getProperty("user.dir");
        }
        return new File(basedir);
    }

}
