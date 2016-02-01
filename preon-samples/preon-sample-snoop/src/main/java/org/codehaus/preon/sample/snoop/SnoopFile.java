/**
 * Copyright (c) 2009-2016 Wilfred Springer
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package org.codehaus.preon.sample.snoop;

import org.codehaus.preon.annotation.*;
import org.codehaus.preon.annotation.Choices.Choice;
import org.codehaus.preon.buffer.ByteOrder;
import org.codehaus.preon.el.ImportStatic;

import java.util.List;

public class SnoopFile {

    @Bound
    private FileHeader header;

    @BoundList(type = PacketRecord.class)
    private List<PacketRecord> records;

    public FileHeader getHeader() {
        return header;
    }

    public List<PacketRecord> getRecords() {
        return records;
    }

    public static class FileHeader {

        @BoundBuffer(match = {0x73, 0x6e, 0x6f, 0x6f, 0x70, 0x00, 0x00, 0x00})
        private byte[] identificationPattern;

        @BoundNumber(byteOrder = ByteOrder.BigEndian)
        private int versionNumber;

        @BoundNumber(size = "32", byteOrder = ByteOrder.BigEndian)
        private DatalinkType datalinkType;

        public int getVersionNumber() {
            return versionNumber;
        }

        public DatalinkType getDatalinkType() {
            return datalinkType;
        }

    }

    @ImportStatic(DatalinkType.class)
    public static class PacketRecord {

        @BoundNumber(byteOrder = ByteOrder.BigEndian, size = "32")
        private long originalLength;

        @BoundNumber(byteOrder = ByteOrder.BigEndian, size = "32")
        private long includedLength;

        @BoundNumber(byteOrder = ByteOrder.BigEndian, size = "32")
        private long packetRecordLength;

        @BoundNumber(byteOrder = ByteOrder.BigEndian, size = "32")
        private long cumulativeDrops;

        @BoundNumber(byteOrder = ByteOrder.BigEndian, size = "32")
        private long timestampSeconds;

        @BoundNumber(byteOrder = ByteOrder.BigEndian, size = "32")
        private long timestampMicroseconds;

        @Slice(size = "(packetRecordLength - 24) * 8")
        @BoundObject(selectFrom =
        @Choices(alternatives =
        @Choice(condition = "outer.header.datalinkType==DatalinkType.ETHERNET", type = EthernetFrame.class)
        )
        )
        private Object packetData;

        public long getOriginalLength() {
            return originalLength;
        }

        public long getIncludedLength() {
            return includedLength;
        }

        public long getPacketRecordLength() {
            return packetRecordLength;
        }

        public long getCumulativeDrops() {
            return cumulativeDrops;
        }

        public long getTimestampSeconds() {
            return timestampSeconds;
        }

        public long getTimestampMicroseconds() {
            return timestampMicroseconds;
        }

        public Object getPacketData() {
            return packetData;
        }

        public static class EthernetFrame {

            @BoundList(size = "6")
            private byte[] destinationAddress;

            @BoundList(size = "6")
            private byte[] sourceAddress;

            @BoundNumber(size = "16")
            private int type;

            @BoundList(size = "outer.includedLength - (6 + 6 + 2)")
            private byte[] data;

            public String getDestinationAddress() {
                return render(destinationAddress);
            }

            public String getSourceAddress() {
                return render(sourceAddress);
            }

            private String render(byte[] address) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < address.length; i++) {
                    if (i != 0) {
                        builder.append(':');
                    }
                    builder.append(Integer.toHexString(0xff & address[i]));
                }
                return builder.toString();
            }

        }

    }

    public static enum DatalinkType {

        @BoundEnumOption(0)
        IEEE_802_3,

        @BoundEnumOption(1)
        IEEE_802_4_TOKEN_BUS,

        @BoundEnumOption(2)
        IEEE_802_5_TOKEN_RING,

        @BoundEnumOption(3)
        IEEE_802_6_METRO_NET,

        @BoundEnumOption(4)
        ETHERNET,

        @BoundEnumOption(5)
        HLDC,

        @BoundEnumOption(6)
        CHARACTER_SYNCHRONOUS,

        @BoundEnumOption(7)
        IBM_CHANNEL_TO_CHANNEL,

        @BoundEnumOption(8)
        FDDI,

        @BoundEnumOption(9)
        OTHER,

        UNASSIGNED
    }

}
