package org.codehaus.preon.codec;

import java.io.IOException;
import java.math.BigInteger;

import javax.annotation.Nonnegative;

import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.buffer.ByteOrder;
import org.codehaus.preon.channel.BitChannel;

public enum NumericUnsignedType implements INumericType {
	UByte (NumericType.Byte),
	UShort (NumericType.Short),
	UInteger (NumericType.Integer),
	ULong (NumericType.Long),
	;
	
	private final NumericType delegate;
	
	private NumericUnsignedType(NumericType delegate) {
		this.delegate = delegate;
	}

	public int getDefaultSize() {
		if (delegate.getDefaultSize() % 2 == 0)
			return delegate.getDefaultSize() / 2;
		throw new IllegalArgumentException("Size of " + delegate.getDefaultSize() + " can not be divided by two.");
	}

	public Object decode(BitBuffer buffer, int size, ByteOrder endian) {
		return delegate.decode(buffer, size, endian);
	}

	public void encode(BitChannel channel, int size, ByteOrder endian, Object value) throws IOException {
		//calculate the maximum value that can be stored with given size
		//maximum size that can be stored: 32bits
		//TODO special treatment for BigInteger
		//TODO use guavas ints
		long longVal = ((Number) value).longValue();
		if (longVal < 0) {
			throw new IllegalArgumentException("Value " + longVal + " is negative, can not be encoded in unsiged.");
		}
		final BigInteger maxVal = BigInteger.valueOf(2).pow(size);
		if (maxVal.compareTo(BigInteger.valueOf(longVal)) == -1) {
			throw new IllegalArgumentException("Value " + longVal + " is too big to be encoded in " + size + " bits.");
		}
		delegate.encode(channel, size, endian, value);
	}

	public Class<?> getType() {
		return delegate.getType();
	}
	
	public Class<?> getPrimitiveType() {
		return delegate.getPrimitiveType();
	}
	
	public Class<?>[] getNumericTypes() {
		return delegate.getNumericTypes();
	}

}