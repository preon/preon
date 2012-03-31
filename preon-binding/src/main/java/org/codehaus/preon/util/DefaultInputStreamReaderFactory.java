package org.codehaus.preon.util;

import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.stream.BitBufferInputStream;

import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class DefaultInputStreamReaderFactory implements InputStreamReaderFactory {
    public InputStreamReader createBitBufferInputStreamReader(final BitBuffer bitBuffer, final Charset charset) {
        return new InputStreamReader(new BitBufferInputStream(bitBuffer), charset);
    }
}
