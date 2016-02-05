package org.codehaus.preon.codec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.codehaus.preon.Codec;
import org.codehaus.preon.Codecs;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.annotation.BoundNumber;
import org.codehaus.preon.annotation.ByteAlign;
import org.codehaus.preon.buffer.ByteOrder;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.channel.OutputStreamBitChannel;
import org.junit.BeforeClass;
import org.junit.Test;

public class ByteAlignTest {
    private static final Codec<DecodeObject> codec = Codecs.create(DecodeObject.class);
    private static final ByteArrayOutputStream out = new ByteArrayOutputStream();

    // Some values that we can test against later on
    public static final byte id = 45;
    public static final long timeStart = System.currentTimeMillis();
    public static final short aligned = 300;
    public static final byte alignedNext = 120;

    /**
     * Hand encode a bitstream that we expect will correspond to the static values above when
     * decoded.
     */
    @BeforeClass
    public static void createValidBytes() {
        final BitChannel channel = new OutputStreamBitChannel(out);

        try {
            channel.write(8, id);
            channel.write(64, timeStart, ByteOrder.BigEndian);
            channel.write(12, aligned, ByteOrder.BigEndian);
            // byte alignment
            channel.write(4, (byte) 0);
            channel.write(8, alignedNext);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Test that the hand-encoded bitstream decodes to expected values via preon.
     */
    @Test
    public void testDecoder() throws DecodingException {
        DecodeObject object = DecodeObject.decode(out.toByteArray());
        assertEquals(id, object.id);
        assertEquals(timeStart, object.timeStart);
        assertEquals(aligned, object.aligned);
        assertEquals(alignedNext, object.alignedNext);
    }

    /**
     * Test that an encode/decode round-trip results in the expected values.
     */
    @Test
    public void testRoundTrip() throws IOException, DecodingException {
        final DecodeObject a = new DecodeObject(id, timeStart, aligned, alignedNext);
        assertEquals(a, DecodeObject.decode(a.encode()));
    }
    
    /**
     * A simple test codec.
     */
    public static class DecodeObject {
        @BoundNumber(size = "8", byteOrder = ByteOrder.BigEndian)
        private short id;

        @BoundNumber(byteOrder = ByteOrder.BigEndian)
        private long timeStart;

        @ByteAlign
        @BoundNumber(size = "12", byteOrder = ByteOrder.BigEndian)
        private short aligned;
        
        @BoundNumber()
        private byte alignedNext;
        
        public DecodeObject() {
        }
        
        public DecodeObject(short id, long timeStart, short aligned, byte alignedNext) {
            this.id = id;
            this.timeStart = timeStart;
            this.aligned = aligned;
            this.alignedNext = alignedNext;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof DecodeObject)) {
                return false;
            }
            final DecodeObject do_ = (DecodeObject) o;
            return this.id == do_.id
                && this.timeStart == do_.timeStart
                && this.aligned == do_.aligned
                && this.alignedNext == do_.alignedNext;
        }

        @Override
        public int hashCode() {
            long xorred = (long) this.id
                ^ ((long)this.alignedNext << 8)
                ^ ((long)this.aligned << 16)
                ^ this.timeStart;
            return (int)(xorred ^ (xorred >> 32));
        }

        public final byte[] encode() throws IOException {
            return Codecs.encode(this, codec);
        }
        
        public static final DecodeObject decode(byte[] bytes) throws DecodingException {
            return Codecs.decode(codec, bytes);
        }
    }
}
