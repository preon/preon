package org.codehaus.preon.codec;

import java.io.IOException;

import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.buffer.ByteOrder;
import org.codehaus.preon.channel.BitChannel;

public interface INumericType {

	int getDefaultSize();

	Object decode(BitBuffer buffer, int size, ByteOrder endian);

	void encode(BitChannel channel, int size, ByteOrder endian, Object value) throws IOException;

	Class<?> getType();
	
	Class<?> getPrimitiveType();
	
	default Class<?>[] getNumericTypes() {
		Class<?>[] numTypes = {getType()};
		return numTypes;
	}

}