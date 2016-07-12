package org.codehaus.preon.codec;

import java.io.IOException;

import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.buffer.ByteOrder;
import org.codehaus.preon.channel.BitChannel;

public enum NumericType implements INumericType {

    Float {
        public int getDefaultSize() {
            return 32;
        }

        public Float decode(BitBuffer buffer, int size, ByteOrder endian) {
            int value = buffer.readAsInt(size, endian);
            return java.lang.Float.intBitsToFloat(value);
        }

        public void encode(BitChannel channel, int size, ByteOrder endian, Object value) {
            throw new UnsupportedOperationException("Encoding not supported for floats.");
        }

        public Class<?> getType() {
            return Float.class;
        }

		@Override
		public Class<?> getPrimitiveType() {
			return java.lang.Float.TYPE;
		}
        

    },

    Double {

        public int getDefaultSize() {
            return 64;
        }

        public Double decode(BitBuffer buffer, int size, ByteOrder endian) {
            return java.lang.Double.longBitsToDouble(buffer.readAsLong(
                    size, endian));
        }

        public void encode(BitChannel channel, int size, ByteOrder endian, Object value) {
            throw new UnsupportedOperationException("Encoding not supported for doubles.");
        }

        public Class<?> getType() {
            return Double.class;
        }

		@Override
		public Class<?> getPrimitiveType() {
			return java.lang.Double.TYPE;
		}
    },

    Integer {
        public int getDefaultSize() {
            return 32;
        }

        public Integer decode(BitBuffer buffer, int size, ByteOrder endian) {
            return buffer.readAsInt(size, endian);
        }

        public void encode(BitChannel channel, int size, ByteOrder endian, Object value) throws IOException {
            channel.write(size, (Integer) value, endian);
        }

        public Class<?> getType() {
            return Integer.class;
        }
        
		@Override
		public Class<?> getPrimitiveType() {
			return java.lang.Integer.TYPE;
		}
    },

    Long {
        public int getDefaultSize() {
            return 64;
        }

        public Long decode(BitBuffer buffer, int size, ByteOrder endian) {
            return buffer.readAsLong(size, endian);
        }

        public void encode(BitChannel channel, int size, ByteOrder endian, Object value) throws IOException {
            channel.write(size, (Long) value, endian);
        }

        public Class<?> getType() {
            return Long.class;
        }
        
		@Override
		public Class<?> getPrimitiveType() {
			return java.lang.Long.TYPE;
		}
    },

    Short {
        public int getDefaultSize() {
            return 16;
        }

        public Short decode(BitBuffer buffer, int size, ByteOrder endian) {
            return buffer.readAsShort(size, endian);
        }

        public void encode(BitChannel channel, int size, ByteOrder endian, Object value) throws IOException {
            channel.write(size, (Short) value, endian);
        }

        public Class<?> getType() {
            return Short.class;
        }
        
		@Override
		public Class<?> getPrimitiveType() {
			return java.lang.Short.TYPE;
		}
    },

    Byte {
        public int getDefaultSize() {
            return 8;
        }

        public Byte decode(BitBuffer buffer, int size, ByteOrder endian) {
            return buffer.readAsByte(size, endian);
        }

        public void encode(BitChannel channel, int size, ByteOrder endian, Object value) throws IOException {
            channel.write(size, (Byte) value);
        }

        public Class<?> getType() {
            return Byte.class;
        }
        
		@Override
		public Class<?> getPrimitiveType() {
			return java.lang.Byte.TYPE;
		}
        
    };

}