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
package org.codehaus.preon.emitter;

import org.apache.commons.io.IOUtils;
import org.codehaus.preon.*;
import org.codehaus.preon.binding.BindingDecorator;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.ByteBuffer;

public class Exporter {

    private Exporter() {
    }

    public static <T> T decodeAndExport(Class<T> type,
                                        ByteBuffer buffer,
                                        File structure)
            throws DecodingException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Emitter emitter = new XmlEmitter(new ByteArrayOutputStreamFactory(out));
        EmittingCodecDecorator codecDecorator = new EmittingCodecDecorator(emitter);
        EmittingBindingDecorator bindingDecorator = new EmittingBindingDecorator(emitter);
        Codec<T> codec = Codecs.create(type,
                new CodecFactory[0],
                new CodecDecorator[]{codecDecorator},
                new BindingDecorator[]{bindingDecorator});
        T result = Codecs.decode(codec, buffer);
        saveEmitted(out.toByteArray(), structure);
        exportResource("/jquery-1.4.2.min.js", structure.getParentFile());
        exportResource("/jquery.callout-min.js", structure.getParentFile());
        return result;
    }

    private static void saveEmitted(byte[] bytes, File structure) {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        OutputStream out = null;
        try {
            out = new FileOutputStream(structure);
            StreamSource xslt = new StreamSource(Exporter.class.getResourceAsStream("/structure-to-html.xsl"));
            Transformer transformer = TransformerFactory.newInstance().newTransformer(xslt);
            transformer.transform(new StreamSource(in), new StreamResult(out));
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TransformerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    private static void exportResource(String resource, File directory) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        File target = new File(directory, resource);
        try {
            in = Exporter.class.getResourceAsStream(resource);
            out = new FileOutputStream(target);
            IOUtils.copy(in, out);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

}
