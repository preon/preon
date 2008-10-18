/*
 * Copyright (C) 2008 Wilfred Springer
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

package nl.flotsam.preon.annotation;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for adding metadata to String fields, informing the framework
 * how to decode and encode the data.
 * 
 * @author Wilfred Springer
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BoundString {

    public enum Encoding {

        ASCII {

            public String decode(byte[] buffer)
                    throws UnsupportedEncodingException {
                return new String(buffer, "US-ASCII");
            }

        },

        ISO_8859_1 {

            public String decode(byte[] buffer)
                    throws UnsupportedEncodingException {
                return new String(buffer, "ISO-8859-1");
            }

        },

        AZ09 {
            public String decode(byte[] buffer)
                    throws UnsupportedEncodingException {
                StringBuilder builder = new StringBuilder();
                int i = 0;
                while (i < buffer.length - 1) {
                    int value = (0xFF & buffer[i])
                            + ((0xFF & buffer[i + 1]) << 8);
                    char c;
                    if ((c = AZ09CHARS[value % 40]) == 0) {
                        break;
                    } else {
                        builder.append(c);
                    }
                    if ((c = AZ09CHARS[(value / 40) % 40]) == 0) {
                        break;
                    } else {
                        builder.append(c);
                    }
                    if ((c = AZ09CHARS[value / 1600]) == 0) {
                        break;
                    } else {
                        builder.append(c);
                    }
                    i = i + 2;
                }
                return builder.toString();
            }

        },

        TRIPLE {

            public String decode(byte[] buffer)
                    throws UnsupportedEncodingException {
                StringBuilder builder = new StringBuilder();
                int i = 0;
                while (i < buffer.length) {
                    char c = (char) (0xFF & buffer[i]);
                    if ((c & 128) > 0) {
                        if (c == 96 + '\\') {
                            c = (char) (0xFF & buffer[++i]);
                            builder.append(c);
                        } else if (c >= 219) {
                            builder.append(c);
                        } else {
                            builder.append((char) (c - 96));
                        }
                        i++;
                    } else {
                        if ((c = TRIPLECHARS[buffer[i] >> 2]) == 0) {
                            break;
                        } else {
                            builder.append(c);
                        }
                        if (i + 2 > buffer.length
                                || (c = TRIPLECHARS[((buffer[i] << 3) & 31)
                                        | ((0xFF & buffer[i + 1]) >> 5)]) == 0) {
                            break;
                        } else {
                            builder.append(c);
                        }
                        if ((c = TRIPLECHARS[buffer[i + 1] & 31]) == 0) {
                            break;
                        } else {
                            builder.append(c);
                        }
                        i += 2;
                    }
                }
                return builder.toString();
            }

        },

        NAME_PHONE {

            public String decode(byte[] buffer) {
                char c;
                int i = 0;
                int r = 0;
                int bitPos = 0;
                int bytePos = 0;
                StringBuilder builder = new StringBuilder();
                try {
                    do {
                        int j = (int) readAsLongBigEndianRL(bytePos, bitPos,
                                buffer, 5);
                        c = PNONE_NAME_CHARS[j];
                        r += 5;
                        builder.append(c);
                        bitPos += 5;
                        if (bitPos >= 8) {
                            bytePos++;
                            bitPos %= 8;
                        }
                    } while (c != '>');
                    while ((c = PNONE_PHONE_CHARS[(i = (int) readAsLongBigEndianRL(
                            bytePos, bitPos, buffer, 4))]) != '\0') {
                        builder.append(c);
                        bitPos += 4;
                        if (bitPos >= 8) {
                            bytePos++;
                            bitPos %= 8;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return builder.toString();
            }

            private long readAsLongBigEndianRL(int bytePos, int bitPos,
                    byte[] buffer, int nrBits) {
                // Long implies less than 64 bits
                assert nrBits < 64;
                // The number of bits available must be higher than requested
                assert (buffer.length - bytePos) * 8 - bitPos >= 0;

                // Byte index
                int index = 0;

                // Process first chunk
                long result = 0;
                try {
                    result |= (0xFF & buffer[bytePos]) >> bitPos;
                    if (bitPos + nrBits < 8) {
                        result = result & (0xFF >> (8 - nrBits));
                    }
                    nrBits -= 8 - bitPos;
                } catch (ArrayIndexOutOfBoundsException e) {
                    for (int i = 0; i < buffer.length; i++) {
                        if (i != 0) {
                            System.out.print(", ");
                        }
                        System.out.print(Integer.toHexString(0xFF & buffer[i]));
                    }
                }

                // Process middle bytes
                while (nrBits > 8) {
                    index++;
                    result |= (0xFF & buffer[bytePos + index]) << (index * 8 - bitPos);
                    nrBits -= 8;
                }

                // Process last byte
                try {
                    index++;
                    if (nrBits > 0) {
                        result |= ((0xFF >> (8 - nrBits)) & buffer[bytePos
                                + index]) << (index * 8 - bitPos);
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    for (int i = 0; i < buffer.length; i++) {
                        if (i != 0) {
                            System.out.print(", ");
                        }
                        System.out.print(Integer.toHexString(0xFF & buffer[i]));
                    }
                }
                return result;
            }

        };

        private static char[] AZ09CHARS = ("\0abcdefghijklmnopqrstuvwxyz0123456789 .-")
                .toCharArray();

        private static char[] TRIPLECHARS = ("\0. SaerionstldchumgpbkfzvACBMPG-")
                .toCharArray();

        private static char[] PNONE_NAME_CHARS = "abcdefghijklmnoprstuvwxyz >()&'-"
                .toCharArray();

        private static char[] PNONE_PHONE_CHARS = { '\0', '0', '1', '2', '3',
                '4', '5', '6', '7', '8', '9', '-', '(', ')', '+', '#' };

        public abstract String decode(byte[] buffer)
                throws UnsupportedEncodingException;

    }

    /**
     * Returns the number of bytes to be interpreted as a String.
     * 
     * @return The number of bytes to be interpreted as a String. (Can be a
     *         Limbo expression.)
     */
    String size() default "";

    /**
     * Returns the type of encoding used for the String.
     * 
     * @return The type of encoding used for the String.
     */
    Encoding encoding() default Encoding.ASCII;

    /**
     * The String that needs to be matched.
     * 
     * @return The String that needs to be matched. Or the empty String if
     *         matching is not important.
     */
    String match() default "";

    Class<? extends ByteConverter> converter() default NullConverter.class;

    public interface ByteConverter {

        byte convert(byte in);

        String getDescription();

    }

    public class NullConverter implements ByteConverter {

        public byte convert(byte in) {
            return in;
        }

        public String getDescription() {
            return "";
        }

    }

}
