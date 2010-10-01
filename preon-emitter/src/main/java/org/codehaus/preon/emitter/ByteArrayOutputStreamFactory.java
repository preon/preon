package org.codehaus.preon.emitter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Semaphore;

public class ByteArrayOutputStreamFactory implements OutputStreamFactory {

    private final ByteArrayOutputStream out;
    private final Semaphore latch = new Semaphore(1);

    public ByteArrayOutputStreamFactory(ByteArrayOutputStream out) {
        this.out = out;
    }

    public OutputStream create() throws IOException {
        return out;
    }

}
