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
