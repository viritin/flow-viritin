package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H4;
import org.vaadin.firitin.fluency.ui.FluentClickNotifierWithoutTypedSource;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VH4 extends H4 implements FluentHtmlContainer<VH4>, FluentClickNotifierWithoutTypedSource<VH4> {
    public VH4() {
        super();
    }

    public VH4(Component... components) {
        super(components);
    }

    public VH4(String text) {
        super(text);
    }
}
