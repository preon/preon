/**
 * Copyright (C) 2009 Wilfred Springer
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
package nl.flotsam.preon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;

import com.ctc.wstx.stax.WstxOutputFactory;
import nl.flotsam.pecia.builder.ArticleDocument;
import nl.flotsam.pecia.builder.DocumentBuilder;
import nl.flotsam.pecia.builder.ParaBuilder;
import nl.flotsam.pecia.builder.base.DefaultArticleDocument;
import nl.flotsam.pecia.builder.base.DefaultDocumentBuilder;
import nl.flotsam.pecia.builder.html.HtmlDocumentBuilder;
import nl.flotsam.pecia.builder.xml.StreamingXmlWriter;
import nl.flotsam.pecia.builder.xml.XmlWriter;
import nl.flotsam.preon.buffer.DefaultBitBuffer;
import nl.flotsam.preon.util.CaseCorrectingParaBuilder;

/**
 * A utility class, providing some convenience mechanisms for using and
 * documenting {@link Codec Codecs}.
 * 
 * @author Wilfred Springer
 * 
 */
public class Codecs {

    /**
     * An enumeration of potential documentation types.
     * 
     * @see Codecs#document(Codec, nl.flotsam.preon.Codecs.DocumentType, File)
     * @see Codecs#document(Codec, nl.flotsam.preon.Codecs.DocumentType,
     *      OutputStream)
     * 
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
         * Returns a {@link DefaultDocumentBuilder} instance for the given type
         * of document.
         * 
         * @param writer
         *            The {@link XmlWriter} to which the document will be
         *            written.
         * @return A {@link DefaultDocumentBuilder} capable of rendering the
         *         desired document format.
         */
        public abstract DefaultDocumentBuilder createDocumentBuilder(
                XmlWriter writer);

    }

    /**
     * Documents the codec, writing a document of the given type to the given
     * file.
     * 
     * @param <T>
     *            The type of objects decoded by the {@link Codec}.
     * @param codec
     *            The actual codec.
     * @param type
     *            The type of document type of the document generated.
     * @param file
     *            The file to which all output needs to be written.
     * @throws FileNotFoundException
     *             If the file cannot be written.
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
     * Documents the codec, writing a document of the given type to the given
     * {@link OutputStream}.
     * 
     * @param <T>
     *            The type of objects decoded by the {@link Codec}.
     * @param codec
     *            The actual codec.
     * @param type
     *            The type of document type of the document generated.
     * @param out
     *            The {@link OutputStream} receiving the document.
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
     * Documents the codec, writing contents to the {@link ArticleDocument}
     * passed in.
     * 
     * @param <T>
     *            The type of objects decoded by the {@link Codec}.
     * @param codec
     *            The actual codec.
     * @param document
     *            The document in which the documentation of the Codec needs to
     *            be generated.
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
     * @param <T>
     *            The of object to be decoded.
     * @param codec
     *            The {@link Codec} that will take care of the actual work.
     * @param buffer
     *            An array of bytes holding the encoded data.
     * @return The decoded object.
     * @throws DecodingException
     *             If the {@link Codec} fails to decode a value from the buffer
     *             passed in.
     */
    public static <T> T decode(Codec<T> codec, byte[] buffer)
            throws DecodingException {
        return decode(codec, ByteBuffer.wrap(buffer));
    }

    /**
     * Decodes an object from the buffer passed in.
     * 
     * @param <T>
     *            The of object to be decoded.
     * @param codec
     *            The {@link Codec} that will take care of the actual work.
     * @param buffer
     *            An array of bytes holding the encoded data.
     * @return The decoded object.
     * @throws DecodingException
     *             If the {@link Codec} fails to decode a value from the buffer
     *             passed in.
     */
    public static <T> T decode(Codec<T> codec, ByteBuffer buffer)
            throws DecodingException {
        return codec.decode(new DefaultBitBuffer(buffer), null,
                new DefaultBuilder());
    }

    /**
     * Decodes an object from the buffer passed in.
     * 
     * @param <T>
     *            The of object to be decoded.
     * @param codec
     *            The {@link Codec} that will take care of the actual work.
     * @param file
     *            The {@link File} providing the data to be decoded.
     * @return The decoded object.
     * @throws FileNotFoundException
     *             If the {@link File} does not exist.
     * @throws IOException
     *             If the system fails to read data from the file.
     * @throws DecodingException
     *             If the {@link Codec} fails to decode a value from the buffer
     *             passed in.
     */
    public static <T> T decode(Codec<T> codec, File file)
            throws FileNotFoundException, IOException, DecodingException {
        FileInputStream in = null;
        FileChannel channel = null;
        try {
            in = new FileInputStream(file);
            channel = in.getChannel();
            int fileSize = (int) channel.size();
            ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0,
                    fileSize);
            return decode(codec, buffer);
        } finally {
            if (channel != null) {
                channel.close();
            }
        }
    }

    /**
     * Creates a {@link Codec} for the given type.
     * 
     * @param <T>
     *            The of object constructed using the {@link Codec}.
     * @param type
     *            The type of object constructed using the {@link Codec}.
     * @return A {@link Codec} capable of decoding/encoding instances of the
     *         type passed in.
     */
    public static <T> Codec<T> create(Class<T> type) {
        return new DefaultCodecFactory().create(type);
    }

    /**
     * Creates a {@link Codec} for the given type, accepting an number of
     * {@link CodecFactory CodecFactories} to be taken into account while
     * constructing the {@link Codec}.
     * 
     * @param <T>
     *            The of object constructed using the {@link Codec}.
     * @param type
     *            The type of object constructed using the {@link Codec}.
     * @param factories
     *            Additional {@link CodecFactory CodecFactories} to be used
     *            while constructing the {@link Codec}.
     * @return A {@link Codec} capable of decoding instances of the type passed
     *         in.
     */
    public static <T> Codec<T> create(Class<T> type, CodecFactory... factories) {
        return new DefaultCodecFactory().create(null, type, factories,
                new CodecDecorator[0]);
    }

    /**
     * Creates a {@link Codec} for the given type, accepting an number of
     * {@link CodecDecorator CodecDecorators} to be taken into account while
     * constructing the {@link Codec}.
     * 
     * @param <T>
     *            The type of object constructed using the {@link Codec}.
     * @param decorators
     *            Additional {@link CodecDecorator CodecDecorators} to be used
     *            while constructing the {@link Codec}.
     * @return A {@link Codec} capable of decoding instances of the type passed
     *         in, taking the {@link CodecDecorator CodecDecorators} into
     *         account.
     */
    public static <T> Codec<T> create(Class<T> type,
            CodecDecorator... decorators) {
        return new DefaultCodecFactory().create(null, type,
                new CodecFactory[0], decorators);
    }

}
