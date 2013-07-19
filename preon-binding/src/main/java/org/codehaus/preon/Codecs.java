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
package org.codehaus.preon;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.xml.stream.XMLStreamException;

import nl.flotsam.pecia.builder.ArticleDocument;
import nl.flotsam.pecia.builder.base.DefaultArticleDocument;
import nl.flotsam.pecia.builder.base.DefaultDocumentBuilder;
import nl.flotsam.pecia.builder.html.HtmlDocumentBuilder;
import nl.flotsam.pecia.builder.xml.StreamingXmlWriter;
import nl.flotsam.pecia.builder.xml.XmlWriter;
import org.codehaus.preon.binding.BindingDecorator;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.buffer.DefaultBitBuffer;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.channel.OutputStreamBitChannel;

import org.apache.commons.io.IOUtils;

import com.ctc.wstx.stax.WstxOutputFactory;

/**
 * A utility class, providing some convenience mechanisms for using and documenting {@link Codec Codecs}.
 *
 * @author Wilfred Springer
 */
public class Codecs {

    private static final Builder DEFAULT_BUILDER = new DefaultBuilder();


    /**
     * An enumeration of potential documentation types.
     *
     * @see Codecs#document(Codec, org.codehaus.preon.Codecs.DocumentType, File)
     * @see Codecs#document(Codec, org.codehaus.preon.Codecs.DocumentType, OutputStream)
     */
    public enum DocumentType {

        Html {

            @Override
            public DefaultDocumentBuilder createDocumentBuilder(XmlWriter writer) {
                return new HtmlDocumentBuilder(writer, this.getClass()
                        .getResource("/default.css")) {

                };
            }
        };

        /**
         * Returns a {@link DefaultDocumentBuilder} instance for the given type of document.
         *
         * @param writer The {@link XmlWriter} to which the document will be written.
         * @return A {@link DefaultDocumentBuilder} capable of rendering the desired document format.
         */
        public abstract DefaultDocumentBuilder createDocumentBuilder(
                XmlWriter writer);

    }

    /**
     * Documents the codec, writing a document of the given type to the given file.
     *
     * @param <T>   The type of objects decoded by the {@link Codec}.
     * @param codec The actual codec.
     * @param type  The type of document type of the document generated.
     * @param file  The file to which all output needs to be written.
     * @throws FileNotFoundException If the file cannot be written.
     */
    public static <T> void document(Codec<T> codec, DocumentType type, File file)
            throws FileNotFoundException {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            document(codec, type, out);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * Documents the codec, writing a document of the given type to the given {@link OutputStream}.
     *
     * @param <T>   The type of objects decoded by the {@link Codec}.
     * @param codec The actual codec.
     * @param type  The type of document type of the document generated.
     * @param out   The {@link OutputStream} receiving the document.
     */
    public static <T> void document(Codec<T> codec, DocumentType type,
                                    OutputStream out) {
        WstxOutputFactory documentFactory = new WstxOutputFactory();
        XmlWriter writer;
        try {
            writer = new StreamingXmlWriter(documentFactory
                    .createXMLStreamWriter(out));
            DefaultDocumentBuilder builder = type.createDocumentBuilder(writer);
            ArticleDocument document = new DefaultArticleDocument(builder,
                    codec.getCodecDescriptor().getTitle());
            document(codec, document);
            document.end();
        } catch (XMLStreamException e) {
            // In the unlikely event this happens:
            throw new RuntimeException("Failed to create stream writer.");
        }
    }

    /**
     * Documents the codec, writing contents to the {@link ArticleDocument} passed in.
     *
     * @param <T>      The type of objects decoded by the {@link Codec}.
     * @param codec    The actual codec.
     * @param document The document in which the documentation of the Codec needs to be generated.
     */
    public static <T> void document(Codec<T> codec, ArticleDocument document) {
        CodecDescriptor descriptor = codec.getCodecDescriptor();
        if (descriptor.requiresDedicatedSection()) {
            document.document(descriptor.details("buffer"));
        } else {
            document.para().text("Full description missing.").end();
        }
    }

    /**
     * Decodes an object from the buffer passed in.
     *
     * @param <T>    The of object to be decoded.
     * @param codec  The {@link Codec} that will take care of the actual work.
     * @param buffer An array of bytes holding the encoded data.
     * @return The decoded object.
     * @throws DecodingException If the {@link Codec} fails to decode a value from the buffer passed in.
     */
    public static <T> T decode(Codec<T> codec, byte... buffer)
            throws DecodingException {
        return decode(codec, ByteBuffer.wrap(buffer));
    }

    public static <T> T decode(Codec<T> codec, Builder builder, byte... buffer)
            throws DecodingException {
        return decode(codec, ByteBuffer.wrap(buffer), builder);
    }

    /**
     * Decodes an object from the buffer passed in.
     *
     * @param <T>    The of object to be decoded.
     * @param codec  The {@link Codec} that will take care of the actual work.
     * @param buffer An array of bytes holding the encoded data.
     * @return The decoded object.
     * @throws DecodingException If the {@link Codec} fails to decode a value from the buffer passed in.
     */
    public static <T> T decode(Codec<T> codec, ByteBuffer buffer)
            throws DecodingException {
        return decode(codec, new DefaultBitBuffer(buffer), null, null);
    }

    public static <T> T decode(Codec<T> codec, ByteBuffer buffer, Builder builder)
            throws DecodingException {
        return decode(codec, new DefaultBitBuffer(buffer), builder, null);
    }

    public static <T> T decode(Codec<T> codec, BitBuffer buffer, Builder builder, Resolver resolver)
            throws DecodingException {
        if (builder == null) {
            builder = DEFAULT_BUILDER;
        }
        return codec.decode(buffer, resolver, builder);
    }

    /**
     * Decodes an object from the buffer passed in.
     *
     * @param <T>   The of object to be decoded.
     * @param codec The {@link Codec} that will take care of the actual work.
     * @param file  The {@link File} providing the data to be decoded.
     * @return The decoded object.
     * @throws FileNotFoundException If the {@link File} does not exist.
     * @throws IOException           If the system fails to read data from the file.
     * @throws DecodingException     If the {@link Codec} fails to decode a value from the buffer passed in.
     */
    public static <T> T decode(Codec<T> codec, File file)
            throws FileNotFoundException, IOException, DecodingException {
        return decode(codec, null, file);
    }

    public static <T> T decode(Codec<T> codec, Builder builder, File file)
            throws FileNotFoundException, IOException, DecodingException {
        FileInputStream in = null;
        FileChannel channel = null;
        try {
            in = new FileInputStream(file);
            channel = in.getChannel();
            int fileSize = (int) channel.size();
            ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0,
                    fileSize);
            return decode(codec, buffer, builder);
        } finally {
            if (channel != null) {
                channel.close();
            }
        }
    }

    /**
     * Encodes the value to the channel passed in, using the given Codec. So why not have this operation on codec
     * instead? Well, it <em>is</em> actually there. However, there will be quite a few overloaded versions of this
     * operation, and Java would force you to implement these operations on <em>every Codec</em>. That's not a very
     * attractive objection. So instead of inheritance, it's all delegation now.
     *
     * @param value   The object that needs to be encoded.
     * @param codec   The codec to be used.
     * @param channel The target {@link org.codehaus.preon.channel.BitChannel}.
     * @param <T>     The type of object to be encoded.
     * @throws IOException If the {@link org.codehaus.preon.channel.BitChannel} no longer receives the data.
     */
    public static <T> void encode(T value, Codec<T> codec, BitChannel channel) throws IOException {
        codec.encode(value, channel, new NullResolver());
    }

    public static <T> byte[] encode(T value, Codec<T> codec) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        encode(value, codec, out);
        return out.toByteArray();
    }

    public static <T> void encode(T value, Codec<T> codec, OutputStream out) throws IOException {
        encode(value, codec, new OutputStreamBitChannel(out));
    }

    /**
     * Creates a {@link Codec} for the given type.
     *
     * @param <T>  The of object constructed using the {@link Codec}.
     * @param type The type of object constructed using the {@link Codec}.
     * @return A {@link Codec} capable of decoding/encoding instances of the type passed in.
     */
    public static <T> Codec<T> create(Class<T> type) {
        return new DefaultCodecFactory().create(type);
    }

    /**
     * Creates a {@link Codec} for the given type, accepting an number of {@link CodecFactory CodecFactories} to be taken
     * into account while constructing the {@link Codec}.
     *
     * @param <T>       The of object constructed using the {@link Codec}.
     * @param type      The type of object constructed using the {@link Codec}.
     * @param factories Additional {@link CodecFactory CodecFactories} to be used while constructing the {@link Codec}.
     * @return A {@link Codec} capable of decoding instances of the type passed in.
     */
    public static <T> Codec<T> create(Class<T> type, CodecFactory... factories) {
        return create(type, factories, new CodecDecorator[0]);
    }

    /**
     * Creates a {@link Codec} for the given type, accepting an number of {@link CodecDecorator CodecDecorators} to be
     * taken into account while constructing the {@link Codec}.
     *
     * @param <T>        The type of object constructed using the {@link Codec}.
     * @param decorators Additional {@link CodecDecorator CodecDecorators} to be used while constructing the {@link
     *                   Codec}.
     * @return A {@link Codec} capable of decoding instances of the type passed in, taking the {@link CodecDecorator
     *         CodecDecorators} into account.
     */
    public static <T> Codec<T> create(Class<T> type,
                                      CodecDecorator... decorators) {
        return create(type, new CodecFactory[0], decorators);
    }

    /**
     * Creates a {@link Codec} for the given type, accepting an number of {@link CodecDecorator CodecDecorators} to be
     * taken into account while constructing the {@link Codec}.
     *
     * @param <T>        The type of object constructed using the {@link Codec}.
     * @param factories  Additional {@link CodecFactory CodecFactories} to be used while constructing the {@link Codec}.
     * @param decorators Additional {@link CodecDecorator CodecDecorators} to be used while constructing the {@link
     *                   Codec}.
     * @return A {@link Codec} capable of decoding instances of the type passed in, taking the {@link CodecDecorator
     *         CodecDecorators} into account.
     */
	public static <T> Codec<T> create(Class<T> type, CodecFactory[] factories,
			CodecDecorator[] decorators) {
        return create(type, factories, decorators, new BindingDecorator[0]);
	}

    public static <T> Codec<T> create(Class<T> type, CodecFactory[] factories,
            CodecDecorator[] decorators, BindingDecorator[] bindingDecorators) {
        return new DefaultCodecFactory().create(null, type,
                factories, decorators, bindingDecorators);
    }

}
