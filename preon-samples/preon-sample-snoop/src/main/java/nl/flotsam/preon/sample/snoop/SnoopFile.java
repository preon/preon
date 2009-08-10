package nl.flotsam.preon.sample.snoop;

import java.util.List;

import nl.flotsam.preon.annotation.Bound;
import nl.flotsam.preon.annotation.BoundBuffer;
import nl.flotsam.preon.annotation.BoundEnumOption;
import nl.flotsam.preon.annotation.BoundList;
import nl.flotsam.preon.annotation.BoundNumber;
import nl.flotsam.preon.annotation.Slice;
import nl.flotsam.preon.buffer.ByteOrder;

public class SnoopFile {

	@Bound
	private FileHeader header;

	@BoundList(type=PacketRecord.class)
	private List<PacketRecord> records;

	public FileHeader getHeader() {
		return header;
	}

	public List<PacketRecord> getRecords() {
		return records;
	}
	
	public static class FileHeader {

		@BoundBuffer(match = { 0x73, 0x6e, 0x6f, 0x6f, 0x70, 0x00, 0x00, 0x00 })
		private byte[] identificationPattern;
		
		@BoundNumber(byteOrder=ByteOrder.BigEndian)
		private int versionNumber;
		
		@BoundNumber(size="32", byteOrder=ByteOrder.BigEndian)
		private DatalinkType datalinkType;

		public int getVersionNumber() {
			return versionNumber;
		}

		public DatalinkType getDatalinkType() {
			return datalinkType;
		}
		
	}

	public static class PacketRecord {

		@BoundNumber(byteOrder = ByteOrder.BigEndian, size="32")
		private long originalLength;
		
		@BoundNumber(byteOrder = ByteOrder.BigEndian, size="32")
		private long includedLength;
		
		@BoundNumber(byteOrder = ByteOrder.BigEndian, size="32")
		private long packetRecordLength;
		
		@BoundNumber(byteOrder = ByteOrder.BigEndian, size="32")
		private long cumulativeDrops;
		
		@BoundNumber(byteOrder = ByteOrder.BigEndian, size="32")
		private long timestampSeconds;
		
		@BoundNumber(byteOrder = ByteOrder.BigEndian, size="32")
		private long timestampMicroseconds;
		
		@Slice(size="(packetRecordLength - 24) * 8")
		@BoundList(size="includedLength")
		private byte[] packetData;

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

		public byte[] getPacketData() {
			return packetData;
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
