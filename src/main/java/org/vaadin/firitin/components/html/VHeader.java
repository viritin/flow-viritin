package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Header;
import org.vaadin.firitin.fluency.ui.FluentClickNotifier;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VHeader extends Header implements FluentHtmlContainer<VHeader>, FluentClickNotifier<Header, VHeader> {

    public VHeader() {
        super();
    }

    public VHeader(Component... components) {
        super(components);
    }
}
