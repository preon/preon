package org.codehaus.preon.emitter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileOutputStreamFactory implements OutputStreamFactory {

    private final File file;
    private final Listener listener;

    public FileOutputStreamFactory(File file, Listener listener) {
        this.file = file;
        this.listener = listener;
    }

    public FileOutputStreamFactory(File file) {
        this(file, new NullListener());
    }

    public OutputStream create() throws IOException {
        return new FileOutputStream(file);
    }

    public interface Listener {

        void created(File file);    

    }

    private static class NullListener implements Listener {

        public void created(File file) {
            // NOOP
        }
    }

}
