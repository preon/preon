package nl.flotsam.preon.util;

import nl.flotsam.pecia.Para;
import nl.flotsam.pecia.builder.DocumentBuilder;
import nl.flotsam.pecia.builder.base.LifecycleListener;
import nl.flotsam.pecia.builder.html.HtmlDocumentBuilder.HtmlParaBuilder;
import nl.flotsam.pecia.builder.xml.XmlWriter;

public class CaseCorrectingParaBuilder<T> extends HtmlParaBuilder<T> {

    public CaseCorrectingParaBuilder(DocumentBuilder builder, T parent,
            LifecycleListener listener, XmlWriter xmlWriter) {
        super(builder, parent, listener, xmlWriter);
    }

    private boolean start = true;
    
    @Override
    public Para<T> text(String text) {
        if (start && text.length() > 0 && Character.isLowerCase(text.charAt(0))) {
            Para<T> para = 
            super.text(Character.toString(Character.toUpperCase(text.charAt(0))));
            para.text(text.substring(1));
            return para;
        } else {
            Para<T> para = super.text(text);
            return para;
        }
    }

}
