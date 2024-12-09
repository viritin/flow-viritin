package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DomEvent;
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

    public void addErrorListener(ComponentEventListener<LoadingErrorEvent> listener) {
        addListener(LoadingErrorEvent.class, listener);
    }

    @DomEvent("error")
    public static class LoadingErrorEvent extends ComponentEvent<Image> {
        public LoadingErrorEvent(Image source, boolean fromClient) {
            super(source, fromClient);
        }
    }

}
