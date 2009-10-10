package nl.flotsam.preon.channel;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.hamcrest.CoreMatchers.is;
import nl.flotsam.preon.channel.OutputStreamBitChannel;

@RunWith(MockitoJUnitRunner.class)
public class OutputStreamBitChannelTest {

    @Mock
    private OutputStream out;

    @Test
    public void shouldAcceptBooleans() throws IOException {
        OutputStreamBitChannel channel = new OutputStreamBitChannel(out);
        channel.write(true);
        channel.write(true);
        channel.write(true);
        channel.write(true);
        channel.write(false);
        channel.write(false);
        channel.write(false);
        channel.write(false);
        channel.write(false);
        verify(out).write((byte) 0xf0);
        verifyNoMoreInteractions(out);
    }

    @Test
    public void shouldAcceptFullBytes() throws IOException {
        OutputStreamBitChannel channel = new OutputStreamBitChannel(out);
        channel.write(8, (byte) 32);
        verify(out).write((byte) 32);
        verifyNoMoreInteractions(out);
    }

    @Test
    public void shouldAcceptPartialBytes() throws IOException {
        OutputStreamBitChannel channel = new OutputStreamBitChannel(out);
        channel.write(4, (byte) 0xff); // 1111
        channel.write(4, (byte) 0x00); // 0000
        verify(out).write((byte) 0xf0);
        verifyNoMoreInteractions(out);
    }

    @Test
    public void shouldDealWithNonAlignedBytes() throws IOException {
        OutputStreamBitChannel channel = new OutputStreamBitChannel(out);
        channel.write(3, (byte) 0xff); // 111
        channel.write(7, (byte) 0x00); // 0000000
        verify(out).write((byte) Integer.parseInt("11100000", 2));
        verifyNoMoreInteractions(out);
    }

    @Test
    public void shouldDealWithNonAlignedMultipleBytes() throws IOException {
        OutputStreamBitChannel channel = new OutputStreamBitChannel(out);
        channel.write(3, (byte) 0xff); // 111
        channel.write(7, (byte) 0x00); // 0000000
        channel.write(8, (byte) 0xff); // 11111111
        channel.write(6, (byte) 0x00); // 000000
        verify(out).write((byte) Integer.parseInt("11100000", 2));
        verify(out).write((byte) Integer.parseInt("00111111", 2));
        verify(out).write((byte) Integer.parseInt("11000000", 2));
        verifyNoMoreInteractions(out);
    }

    @Test
    public void shouldAcceptInts() throws IOException {
        OutputStreamBitChannel channel = new OutputStreamBitChannel(out);
        channel.write(12, (int) 0xfff, ByteOrder.BIG_ENDIAN); // 1111 1111 1111
        channel.write(4, (int) 0x0, ByteOrder.BIG_ENDIAN); // 0000
        verify(out).write((byte) Integer.parseInt("11111111", 2));
        verify(out).write((byte) Integer.parseInt("11110000", 2));
        verifyNoMoreInteractions(out);
    }
    
    @Test
    public void shouldAcceptIntsAndBytes() throws IOException {
        OutputStreamBitChannel channel = new OutputStreamBitChannel(out);
        channel.write(12, (int) 0xfff, ByteOrder.BIG_ENDIAN); // 1111 1111 1111
        channel.write(5, (byte) 0x0); // 0000 0
        verify(out).write((byte) Integer.parseInt("11111111", 2));
        verify(out).write((byte) Integer.parseInt("11110000", 2));
        verifyNoMoreInteractions(out);
    }
    
    @Test
    public void shouldAcceptLittleEndian() throws IOException {
        OutputStreamBitChannel channel = new OutputStreamBitChannel(out);
        channel.write(12, (int) 0xf00, ByteOrder.LITTLE_ENDIAN); // 1111 0000 0000 
        channel.write(4, (int) 0x0, ByteOrder.LITTLE_ENDIAN); // 0000
        // What I expect:
        // 0000 0000 1111 0000
        verify(out).write((byte) Integer.parseInt("00000000", 2));
        verify(out).write((byte) Integer.parseInt("11110000", 2));
        verifyNoMoreInteractions(out);
    }
    
    @Test
    public void shouldAcceptLongs() throws IOException {
        OutputStreamBitChannel channel = new OutputStreamBitChannel(out);
        channel.write(12, Long.MAX_VALUE / 2, ByteOrder.BIG_ENDIAN); // 1111 1111 1111
        channel.write(5, (byte) 0x0); // 0000 0
        verify(out).write((byte) Integer.parseInt("11111111", 2));
        verify(out).write((byte) Integer.parseInt("11110000", 2));
        verifyNoMoreInteractions(out);
    }

    @Test
    public void shouldTellPositionCorrectly() throws IOException {
        OutputStreamBitChannel channel = new OutputStreamBitChannel(out);
        channel.write(4, (byte) 12);
        assertThat(channel.getRelativeBitPos(), is(4));
        channel.write(4, (byte) 0);
        assertThat(channel.getRelativeBitPos(), is(0));
    }

}
