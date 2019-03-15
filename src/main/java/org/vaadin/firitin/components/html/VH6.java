package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H6;
import org.vaadin.firitin.fluency.ui.FluentClickNotifierWithoutTypedSource;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VH6 extends H6 implements FluentHtmlContainer<VH6>, FluentClickNotifierWithoutTypedSource<VH6> {
    public VH6() {
        super();
    }

    public VH6(Component... components) {
        super(components);
    }

    public VH6(String text) {
        super(text);
    }
}
