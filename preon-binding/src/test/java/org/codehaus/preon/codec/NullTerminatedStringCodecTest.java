package org.codehaus.preon.codec;

import org.codehaus.preon.Codec;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.util.InputStreamReaderFactory;
import org.codehaus.preon.util.StandardCharsets;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class NullTerminatedStringCodecTest {
    @Test
    public void decodesSingleNullTerminationCharacterAsEmptyString() throws Exception {
        final InputStreamReader reader          = mockInputStreamReaderReturningValues('\0');
        final InputStreamReaderFactory factory  = mockFactoryWithReader(reader);
        final Charset arbitraryCharset          = StandardCharsets.US_ASCII;
        final Codec<String> codec               = new NullTerminatedStringCodec(arbitraryCharset, factory, null);
        final BitBuffer bitBufferDummy          = mock(BitBuffer.class); // Not used since we mock the reader
            
        final String decodedString = codec.decode(bitBufferDummy, null, null);

        assertThat(decodedString, is(""));
        
        verify(factory).createBitBufferInputStreamReader(bitBufferDummy, arbitraryCharset);
        verify(reader).read();
    }

    @Test
    public void canDecodeSingleByteCharsetStrings() throws Exception {
        final InputStreamReader reader            = mockInputStreamReaderReturningValues('T','ß','東','\0');
        final InputStreamReaderFactory factory    = mockFactoryWithReader(reader);
        final Charset arbitrarySingleByteCharset  = StandardCharsets.UTF_8;
        final Codec<String> codec                 = new NullTerminatedStringCodec(arbitrarySingleByteCharset, factory, "");
        final BitBuffer bitBufferDummy            = mock(BitBuffer.class); // Not used since we mock the reader

        final String decodedString = codec.decode(bitBufferDummy, null, null);

        assertThat(decodedString, is("Tß東"));
        
        verify(factory).createBitBufferInputStreamReader(bitBufferDummy, arbitrarySingleByteCharset);
        verify(reader, times(4)).read();
    }

    @Test
    public void canDecodeMultiByteCharsetStrings() throws Exception {
        final InputStreamReader reader          = mockInputStreamReaderReturningValues('ß','東','\0');
        final InputStreamReaderFactory factory  = mockFactoryWithReader(reader);
        final Charset arbitraryMultiByteCharset = StandardCharsets.UTF_16BE;
        final Codec<String> codec               = new NullTerminatedStringCodec(arbitraryMultiByteCharset, factory, "");
        final BitBuffer bitBufferDummy          = mock(BitBuffer.class); // Not used since we mock the reader

        final String decodedString = codec.decode(bitBufferDummy, null, null);

        assertThat(decodedString, is("ß東"));
        
        verify(factory).createBitBufferInputStreamReader(bitBufferDummy, arbitraryMultiByteCharset);
        verify(reader, times(3)).read();
    }

    @Test(expected = DecodingException.class)
    public void throwsDecodingExceptionWhenReaderReachesEndOfStream() throws Exception {
        final InputStreamReader reader          = mockInputStreamReaderReturningValues(-1,'\0');
        final InputStreamReaderFactory factory  = mockFactoryWithReader(reader);
        final Charset arbitraryCharset          = StandardCharsets.US_ASCII;
        final Codec<String> codec               = new NullTerminatedStringCodec(arbitraryCharset, factory, null);
        final BitBuffer bitBufferDummy          = mock(BitBuffer.class);

        codec.decode(bitBufferDummy, null, null);
    }

    private static InputStreamReaderFactory mockFactoryWithReader(final InputStreamReader reader)  {
        final InputStreamReaderFactory factory = mock(InputStreamReaderFactory.class);

        when(
                factory.createBitBufferInputStreamReader(any(BitBuffer.class), any(Charset.class))
        ).thenReturn(reader);

        return factory;
    }

    private static InputStreamReader mockInputStreamReaderReturningValues(final int... values) throws IOException {
        final InputStreamReader inputStreamReader = mock(InputStreamReader.class);

        // Due to silly Mockito API of thenReturn() we need to split the array in two
        final int firstValue = values[0];
        final Integer[] otherValues = removeFirstElementFromArray(values);

        when(
                inputStreamReader.read()
        ).thenReturn(firstValue, otherValues);
        
        return inputStreamReader;
    }

    private static Integer[] removeFirstElementFromArray(final int[] values) {
        final Integer[] newArray = new Integer[values.length-1];

        for (int i = 1; i < values.length; i++) {
            newArray[i-1] = values[i];
        }

        return newArray;
    }
}
