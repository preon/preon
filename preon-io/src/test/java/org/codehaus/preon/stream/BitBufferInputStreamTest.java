package org.codehaus.preon.stream;

import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.buffer.BitBufferUnderflowException;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class BitBufferInputStreamTest {
    @Test
    public void readsBytesAsUnsignedAndDirectlyFromBackingBitBuffer() throws IOException {
        final BitBuffer bitBuffer = mock(BitBuffer.class);

        when(
                bitBuffer.readAsByte(8)
        ).thenReturn((byte)0x00,(byte)0xFF);

        final BitBufferInputStream stream = new BitBufferInputStream(bitBuffer);

        assertThat(stream.read(), is(0x00));
        assertThat(stream.read(), is(0xFF));
        verify(bitBuffer, times(2)).readAsByte(8);
    }

    @Test
    public void returnsMinusOneWhenReadingPastEndOfBackingBitBuffer() throws IOException {
        final BitBuffer bitBuffer = mock(BitBuffer.class);

        when(
                bitBuffer.readAsByte(8)
        ).thenThrow(new BitBufferUnderflowException(0, 0));

        final BitBufferInputStream stream = new BitBufferInputStream(bitBuffer);

        assertThat(stream.read(), is(-1));
        verify(bitBuffer).readAsByte(8);
    }
}
