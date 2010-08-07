/**
 * Copyright (C) 2009-2010 Wilfred Springer
 *
 * This file is part of Preon.
 *
 * Preon is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * Preon is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Preon; see the file COPYING. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
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
