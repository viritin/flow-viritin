package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H3;
import org.vaadin.firitin.fluency.ui.FluentClickNotifierWithoutTypedSource;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VH3 extends H3 implements FluentHtmlContainer<VH3>, FluentClickNotifierWithoutTypedSource<VH3> {
    public VH3() {
        super();
    }

    public VH3(Component... components) {
        super(components);
    }

    public VH3(String text) {
        super(text);
    }
}
