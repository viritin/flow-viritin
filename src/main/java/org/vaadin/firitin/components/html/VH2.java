package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import org.vaadin.firitin.fluency.ui.FluentClickNotifierWithoutTypedSource;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VH2 extends H2 implements FluentHtmlContainer<VH2>, FluentClickNotifierWithoutTypedSource<VH2> {
    public VH2() {
        super();
    }

    public VH2(Component... components) {
        super(components);
    }

    public VH2(String text) {
        super(text);
    }
}
