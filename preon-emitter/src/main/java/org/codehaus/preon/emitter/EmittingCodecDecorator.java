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
package org.codehaus.preon.emitter;


import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.el.Expression;
import org.codehaus.preon.*;

import java.lang.reflect.AnnotatedElement;

/**
 * A {@link CodecDecorator} that will log a message before and after the invocation of every {@link Codec}.
 *
 * @author Wilfred Springer (wis)
 */
public class EmittingCodecDecorator implements CodecDecorator {

    /** The actual logging object. */
    private final Emitter emitter;

    /**
     * Constructs a new instance, accepting the {@link Emitter} to use.
     *
     * @param emitter The {@link Emitter} to use.
     */
    public EmittingCodecDecorator(Emitter emitter) {
        this.emitter = emitter;
    }

    /** Constructs a new instance that will log to System.out. */
    public EmittingCodecDecorator() {
        this(new LoggingEmitter());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.preon.CodecDecorator#decorate(org.codehaus.preon.Codec,
     * java.lang.reflect.AnnotatedElement, java.lang.Class,
     * org.codehaus.preon.ResolverContext)
     */

    public <T> Codec<T> decorate(Codec<T> decorated, AnnotatedElement metadata,
                                 Class<T> type, ResolverContext context) {
        return new EmittingCodec<T>(decorated, emitter);
    }

    /**
     * A default implementation of the {@link Emitter} interface, logging to System.out.
     *
     * @author Wilfred Springer (wis)
     */
    private static class LoggingEmitter implements Emitter {

        /** "Call-stack depth" */
        private int level = 0;

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.codehaus.preon.emitter.EmittingCodecDecorator.Emitter#markEnd(nl
         * .flotsam.preon.Codec, long, long, java.lang.Object)
         */

        public void markEnd(Codec<?> codec, long position, long read,
                                    Object result) {
            level--;
            // TODO: 
            printMessage("Done decoding at " + position
                    + " (" + read + " bits) : " + format(result) + " Codec: " + codec);
            System.out.println();
        }

        /**
         * Formats the result.
         *
         * @param result The object.
         * @return The result formatted as a String.
         */
        private String format(Object result) {
            if (result instanceof Integer) {
                return result.toString() + " (0x"
                        + Integer.toHexString((Integer) result) + ")";
            } else if (result instanceof Long) {
                return result.toString() + " (0x"
                        + Long.toHexString((Long) result) + ")";
            } else if (result != null) {
                return result.toString();
            } else {
                return "null";
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.codehaus.preon.emitter.EmittingCodecDecorator.Emitter#markFailure()
         */

        public void markFailure() {
            level--;
            printMessage("Failed decoding.");
        }

        public void markStartLoad(String name, Object object) {
            
        }

        public void markEndLoad() {
            
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.codehaus.preon.emitter.EmittingCodecDecorator.Emitter#markStart(nl
         * .flotsam.preon.Codec, long, long)
         */

        public void markStart(Codec<?> codec, long position, BitBuffer buffer) {
            StringBuilder builder = new StringBuilder();
            builder.append("Start decoding at ")
                    .append(position)
                    .append(" Codec: ")
                    .append(codec.toString());
            printMessage(builder.toString());
            level++;
        }

        /**
         * Prints the message. (Used both by {@link #markEnd(Codec, long, long, Object)}, {@link
         * Emitter#markStart(org.codehaus.preon.Codec}, as well as {@link #markFailure()}.
         *
         * @param message The message to be printed.
         */
        private void printMessage(String message) {
            for (int i = 0; i < level; i++) {
                System.out.print(' ');
            }
            System.out.println(message);
        }

    }

    private final static long getSize(Codec<?> codec, Resolver resolver) {
        Expression<Integer, Resolver> size = codec.getSize();
        if (size == null) {
            return -1;
        } else {
            if (size.isParameterized()) {
                return -1;
            } else {
                return size.eval(resolver);
            }
        }
    }

}
