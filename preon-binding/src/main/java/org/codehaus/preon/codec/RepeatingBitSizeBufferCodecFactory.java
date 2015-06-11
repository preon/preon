/**
 * Copyright (C) 2009-2010 Wilfred Springer
 * Copyright (C) 2015 Garth Dahlstrom, 2Keys Corporation
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
package org.codehaus.preon.codec;

import org.codehaus.preon.el.Expression;
import org.codehaus.preon.el.Expressions;

import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;

import org.codehaus.preon.*;
import org.codehaus.preon.annotation.BoundRepeatingBitSizeBuffer;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.buffer.DefaultBitBuffer;
import org.codehaus.preon.channel.BitChannel;

import com.sun.org.apache.xalan.internal.xsltc.dom.BitArray;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.nio.ByteBuffer;


/**
 * A {@link CodecFactory} capable of creating {@link Codec Codecs} that deal with RepeatingBitSizeBuffers.
 *
 * @author Garth Dahlstrom, 2Keys Corporation
 */
public class RepeatingBitSizeBufferCodecFactory implements CodecFactory {

	/*
	 * (non-Javadoc)
	 * @see org.codehaus.preon.CodecFactory#create(java.lang.reflect.AnnotatedElement, java.lang.Class, org.codehaus.preon.ResolverContext)
	 */

	@SuppressWarnings("unchecked")
	public <T> Codec<T> create(AnnotatedElement metadata, Class<T> type,
			ResolverContext context) {
		if (metadata == null || metadata.isAnnotationPresent(BoundRepeatingBitSizeBuffer.class)) {
			if (byte[].class.equals(type)) {
				BoundRepeatingBitSizeBuffer settings = null;
				if (metadata != null) {
					settings = metadata.getAnnotation(BoundRepeatingBitSizeBuffer.class);
				}

				Byte terminateBit = 0;
				Expression<Integer, Resolver> sizeExpr = null;
				if (settings != null) {
					if (settings.maxLength() != null && !settings.maxLength().equals("")) {
						sizeExpr = Expressions.createInteger(context, settings.maxLength());
					}
					terminateBit = settings.terminateBit();
				}

				return (Codec<T>) new RepeatingBitSizeBufferCodec(
						terminateBit,
						sizeExpr);
			} else {
				// System.err.println("RepeatingBitSizeBufferCodecFactory.create 1 -- (should never see this)");
				return null;
			}
		} else {
			// System.err.println("RepeatingBitSizeBufferCodecFactory.create 2 -- (should never see this)");
			return null;
		}
	}

	private static class RepeatingBitSizeBufferCodec implements Codec<byte[]> {

		private byte terminateBit;
		protected Expression<Integer, Resolver> maxLengthExpr;
		
		public RepeatingBitSizeBufferCodec(byte terminateBit, Expression<Integer, Resolver> maxCountExpr) {
			this.terminateBit = terminateBit;
			this.maxLengthExpr = maxCountExpr;
		}

		public byte[] decode(BitBuffer buffer, Resolver resolver,
				Builder builder) throws DecodingException {
			int size = 0;
			if (this.maxLengthExpr != null) {
				size = ((Number) (this.maxLengthExpr.eval(resolver))).intValue();
			}

			int count = 0;
			long readBit = terminateBit;
			do {
				readBit = buffer.readBits(1);
				count++;
			} while (readBit != terminateBit && (size <= 0 || count < size));
			
			
			byte[] result = null;
			if (count == 1) {
			    result = new byte[1];
			    result[0] = buffer.readAsByte(7); // , endian) // FIXME: handle Endianness
			} else { 
				byte[] residualBits = null;
				byte [] fullBytes = null;
				if (count % 8 != 0){				
				    residualBits = new byte[1];
				    residualBits[0] = buffer.readAsByte(8 - (count % 8)); // , endian) // FIXME: handle Endianness
					count--;
				} 
				
				fullBytes = new byte[count];
				// byte [] fullBytes = buffer.readAsByteBuffer(count - ((residualBits == null) ? 0 : 1)).array(); -- DOES NOT WORK

				int fbi = 0;
				do {
					fullBytes[fbi] = buffer.readAsByte(8);
					fbi++;
				} while (fbi < count);

				result = new byte[residualBits.length + fullBytes.length];
				System.arraycopy(residualBits, 0, result, 0, residualBits.length);
				System.arraycopy(fullBytes, 0, result, residualBits.length, fullBytes.length);
			}

			return result;
		}

		public void encode(byte [] value, BitChannel channel, Resolver resolver) throws IOException {
			int length = -1;
			if (this.maxLengthExpr != null) {
				length = ((Number) (this.maxLengthExpr.eval(resolver))).intValue();
			} else {
				length = value.length;
			}

			// Empty value
			if (value.length == 0 || (length <= 1)) { // FIXME: handle Endianness
				channel.write(1, terminateBit); // , byteOrder); 
				channel.write(7, (byte) 0); // , byteOrder); // Pad out to 1 Byte
				return;
			}
			BitBuffer buffer = new DefaultBitBuffer(ByteBuffer.wrap(value, 0, length));
			
			// Look for empty space at the beginning of buffer in which we can "fill-in" length information as a series of repeating bits
			int emptySpaceBits = 0;
			while (emptySpaceBits < length && !buffer.readAsBoolean(emptySpaceBits)) { // FIXME: need to handle Endianness here
				emptySpaceBits++;
			}
			if (emptySpaceBits == length) { // Enough empty space to prefix length
				// 127 -> 0111 1111
				if (emptySpaceBits > 1) {
					channel.write(length - 1, (byte) (((terminateBit+1) % 2) * 255)); 
				}
				channel.write(1, terminateBit);	

				buffer.setBitPos(emptySpaceBits);
			} else { // Not enough empty space to prefix length, so we must pad 1 or more octets
				// 255 -> 1000 0000 1111 1111
				channel.write(length, (byte) ((terminateBit+1) % 2)); 
				channel.write(1, terminateBit);				
				channel.write(8 - ((length+1) % 8), (byte) 0);
				
				buffer.setBitPos(0);
			}

			// Write out the rest of the buffer
			while(buffer.getBitPos() < (length * 8)) { // && (size > 0 && ((buffer.getBitPos() / 8) < size))
				channel.write(1, (byte) buffer.readBits(1));
			}
		}

		public CodecDescriptor getCodecDescriptor() {
			return new CodecDescriptor() {

				public <T extends SimpleContents<?>> Documenter<T> details(
						String bufferReference) {
					return new Documenter<T>() {
						public void document(T target) {
						}
					};
				}

				public String getTitle() {
					return null;
				}

				public <T extends ParaContents<?>> Documenter<T> reference(
						final Adjective adjective, boolean startWithCapital) {
					return new Documenter<T>() {
						public void document(T target) {
							target.text(adjective == Adjective.A ? "a " : "the ");
							target.text("Repeating Bit Size Buffer");
						}
					};
				}

				public boolean requiresDedicatedSection() {
					return false;
				}

				public <T extends ParaContents<?>> Documenter<T> summary() {
					return new Documenter<T>() {
						public void document(T target) {
							target.text("A buffer who's size in bytes is determined by an embedded series of repeating bits immediately before the buffers data.");
							target.text("i.e. 110|11111 11111111 11111111 -> (3 data bytes) 0x1FFFFF, where terminateBit = 0, no maxLength provided (unlimited)");
						}
					};
				}

			};
		}

		public Expression<Integer, Resolver> getSize() {
			return maxLengthExpr;
		}

		public Class<?> getType() {
			return Integer.class;
		}

		public Class<?>[] getTypes() {
			return new Class[]{Integer.class};
		}

	}

}
