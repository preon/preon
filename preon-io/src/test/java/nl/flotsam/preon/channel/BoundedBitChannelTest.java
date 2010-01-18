package nl.flotsam.preon.channel;

import nl.flotsam.preon.buffer.ByteOrder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


public class BoundedBitChannelTest {

    private BitChannel channel;

    private BoundedBitChannel boundedChannel;

    @Before
    public void configureBoundedChannel() {
        channel = mock(BitChannel.class);
        boundedChannel = new BoundedBitChannel(channel, 9);
    }

    @Test
    public void shouldAccept9BitsAsInt() throws IOException {
        boundedChannel.write(9, Integer.MAX_VALUE, ByteOrder.BigEndian);
        verify(channel).write(9, Integer.MAX_VALUE, ByteOrder.BigEndian);
        verifyNoMoreInteractions(channel);
    }

    @Test(expected = IOException.class)
    public void shouldReject10BitsAsInt() throws IOException {
        boundedChannel.write(10, Integer.MAX_VALUE, ByteOrder.BigEndian);
    }

    @Test
    public void shouldAccept9BitsAsLong() throws IOException {
        boundedChannel.write(9, Long.MAX_VALUE, ByteOrder.BigEndian);
        verify(channel).write(9, Long.MAX_VALUE, ByteOrder.BigEndian);
        verifyNoMoreInteractions(channel);
    }

    @Test(expected = IOException.class)
    public void shouldReject10BitsAsLong() throws IOException {
        boundedChannel.write(10, Long.MAX_VALUE, ByteOrder.BigEndian);
    }

    @Test
    public void shouldAccept9BitsAsShort() throws IOException {
        boundedChannel.write(9, Short.MAX_VALUE, ByteOrder.BigEndian);
        verify(channel).write(9, Short.MAX_VALUE, ByteOrder.BigEndian);
        verifyNoMoreInteractions(channel);
    }

    @Test(expected = IOException.class)
    public void shouldReject10BitsAsShort() throws IOException {
        boundedChannel.write(10, Short.MAX_VALUE, ByteOrder.BigEndian);
    }

    @Test
    public void shouldAccept9BitsAsBytesAccummulated() throws IOException {
        boundedChannel.write(8, Byte.MAX_VALUE, ByteOrder.BigEndian);
        boundedChannel.write(1, Byte.MAX_VALUE, ByteOrder.BigEndian);
        verify(channel).write(8, Byte.MAX_VALUE, ByteOrder.BigEndian);
        verify(channel).write(1, Byte.MAX_VALUE, ByteOrder.BigEndian);
        verifyNoMoreInteractions(channel);
    }

    @Test(expected = IOException.class)
    public void shouldReject10BitsAsBytesAccummulated() throws IOException {
        boundedChannel.write(8, Byte.MAX_VALUE, ByteOrder.BigEndian);
        boundedChannel.write(2, Byte.MAX_VALUE, ByteOrder.BigEndian);
    }

    @Test
    public void shouldCloseCorrectly() throws IOException {
        boundedChannel.close();
        verify(channel).close();
    }

    @Test
    public void shouldReportRelativeBitPosCorrectly() throws IOException {
        when(channel.getRelativeBitPos()).thenReturn(4);
        assertThat(boundedChannel.getRelativeBitPos(), is(4));
        verify(channel).getRelativeBitPos();
    }

}
