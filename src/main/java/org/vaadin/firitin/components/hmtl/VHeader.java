package org.vaadin.firitin.components.hmtl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Header;
import org.vaadin.firitin.fluency.ui.FluentClickNotifierWithoutTypedSource;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VHeader extends Header implements FluentHtmlContainer<VHeader>, FluentClickNotifierWithoutTypedSource<VHeader> {

    public VHeader() {
        super();
    }

    public VHeader(Component... components) {
        super(components);
    }
}
