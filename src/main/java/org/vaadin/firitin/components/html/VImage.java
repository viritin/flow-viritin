package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.AbstractStreamResource;
import org.vaadin.firitin.fluency.ui.FluentClickNotifier;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VImage extends Image implements FluentHtmlContainer<VImage>, FluentClickNotifier<Image, VImage> {

    public VImage() {
        super();
    }

    public VImage(String src, String alt) {
        super(src, alt);
    }

    public VImage(AbstractStreamResource src, String alt) {
        super(src, alt);
    }

    public VImage withSrc(String src) {
        setSrc(src);
        return this;
    }

    public VImage withSrc(AbstractStreamResource src) {
        setSrc(src);
        return this;
    }

    public VImage withAlt(String alt) {
        setAlt(alt);
        return this;
    }

}
