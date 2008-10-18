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

package nl.flotsam.preon.codec;

import java.lang.reflect.AnnotatedElement;

import nl.flotsam.limbo.Expression;
import nl.flotsam.preon.Builder;
import nl.flotsam.preon.Codec;
import nl.flotsam.preon.CodecDecorator;
import nl.flotsam.preon.CodecDescriptor;
import nl.flotsam.preon.DecodingException;
import nl.flotsam.preon.Resolver;
import nl.flotsam.preon.ResolverContext;
import nl.flotsam.preon.buffer.BitBuffer;


public class LoggingDecorator implements CodecDecorator {

    private Logger logger;

    public LoggingDecorator(Logger logger) {
        this.logger = logger;
    }

    public LoggingDecorator() {
        this(new DefaultLogger());
    }

    public <T> Codec<T> decorate(Codec<T> decorated, AnnotatedElement metadata,
            Class<T> type, ResolverContext context) {
        return new LoggingCodec<T>(decorated, logger);
    }

    public interface Logger {

        void logStartDecoding(Codec<?> codec, long position, long size);

        void logDoneDecoding(Codec<?> codec, long position, long read,
                Object result);

        void logFailedDecoding();

    }

    private static class DefaultLogger implements Logger {

        private int level = 0;

        public void logDoneDecoding(Codec<?> codec, long position, long read,
                Object result) {
            level--;
            printMessage("Done decoding "
                    + codec.getCodecDescriptor().getLabel() + " at " + position
                    + " (" + read + " bits) : " + result);
        }

        public void logFailedDecoding() {
            level--;
            printMessage("Failed decoding.");
        }

        public void logStartDecoding(Codec<?> codec, long position, long size) {
            if (codec.getCodecDescriptor() == null) {
                System.err.println("Descriptor of "
                        + codec.getClass().toString() + " is null.");
            }
            printMessage("Start decoding "
                    + codec.getCodecDescriptor().getLabel() + " at " + position
                    + (size >= 0 ? " (maximal up to " + (position + size) + ")" : ""));
            level++;
        }

        private void printMessage(String message) {
            for (int i = 0; i < level; i++) {
                System.out.print(' ');
            }
            System.out.println(message);
        }

    }

    private static class LoggingCodec<T> implements Codec<T> {

        private Codec<T> codec;

        private Logger logger;

        public LoggingCodec(Codec<T> codec, Logger logger) {
            this.codec = codec;
            this.logger = logger;
        }

        public T decode(BitBuffer buffer, Resolver resolver, Builder builder)
                throws DecodingException {
            T result = null;
            long pos = buffer.getActualBitPos();
            logger.logStartDecoding(codec, pos, codec.getSize(resolver));
            try {
                result = codec.decode(buffer, resolver, builder);
            } catch (DecodingException de) {
                logger.logFailedDecoding();
                throw de;
            } finally {
                logger.logDoneDecoding(codec, buffer.getActualBitPos(), buffer
                        .getActualBitPos()
                        - pos, result);
            }
            return result;
        }

        public CodecDescriptor getCodecDescriptor() {
            return codec.getCodecDescriptor();
        }

        public int getSize(Resolver resolver) {
            return codec.getSize(resolver);
        }

        public Class<?>[] getTypes() {
            return codec.getTypes();
        }

        public Expression<Integer, Resolver> getSize() {
            return codec.getSize();
        }

    }

}
