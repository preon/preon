package org.codehaus.preon.acceptancetests;

import org.codehaus.preon.Codec;
import org.codehaus.preon.Codecs;
import org.codehaus.preon.DecodingException;

class TestUtils {
    private TestUtils() {} // Prevent construction

    static <T> T decodeObjectFromInput(final Class<T> objectClass, final byte[] input) throws DecodingException {
        final Codec<T> codec = Codecs.create(objectClass);

        return Codecs.decode(codec, input);
    }

    static byte[] generateArrayContaining(final byte data, final int size) {
        final byte[] result = new byte[size];

        for (int i = 0; i < size; i++) {
            result[i] = data;
        }

        return result;
    }
}
