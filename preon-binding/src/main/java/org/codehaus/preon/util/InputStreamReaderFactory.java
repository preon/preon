package org.codehaus.preon.util;

import org.codehaus.preon.buffer.BitBuffer;

import java.io.InputStreamReader;
import java.nio.charset.Charset;

public interface InputStreamReaderFactory {
    InputStreamReader createBitBufferInputStreamReader(BitBuffer bitBuffer, Charset charset);
}
