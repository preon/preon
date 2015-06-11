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
package org.codehaus.preon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.codehaus.preon.buffer.ByteOrder;

/**
 * An annotation for marking a repeating bit size buffer to be bound.
 * This annotation allows you to load a buffer who's length is defined as an embedded series of repeating bits.
 *
 * NOTE: that the remainder of bits not used in the size+terminatorBit count as the first byte to be read.
 *
 * Examples: 
 * 1) terminateBit = 0 (T), no maxLength
 * 0000 0000 -> T000 0000 -> (1 byte/7 bits) 0 
 *
 * 2) terminateBit = 0 (T), no maxLength
 * 1000 0001, 0000 0000 -> xT00 0001 0000 0000 -> (2 byte/14 bits) 256 
 * 
 * 3) terminateBit = 1 (T), no maxLength
 * 0100 0001, 0000 0000 -> xT00 0001 0000 0000 -> (2 byte/14 bits) 256 
 *
 * 
 * @author Garth Dahlstrom
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BoundRepeatingBitSizeBuffer {

	String maxLength() default ""; // MaxLength, if defined, will ignore data beyond MaxLength * 8 bits for encoding/decoding
	public byte terminateBit() default 0; // 1 or 0 which terminates a series of size bits

	// TODO: Handle Endianness
	/**
     * The type of endianness: either {@link ByteOrder#LittleEndian} or {@link ByteOrder#BigEndian}.
     *
     * @return The type of endianness. Defaults to {@link ByteOrder#LittleEndian}.
     */
	// ByteOrder byteOrder() default ByteOrder.LittleEndian;

}
