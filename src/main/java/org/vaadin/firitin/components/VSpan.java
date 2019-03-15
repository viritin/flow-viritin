package org.vaadin.firitin.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import org.vaadin.firitin.fluency.ui.FluentClickNotifierWithoutTypedSource;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VSpan extends Span implements FluentHtmlContainer<VSpan>, FluentClickNotifierWithoutTypedSource<VSpan> {
    public VSpan() {
        super();
    }

    public VSpan(String text) {
        super(text);
    }

    public VSpan(Component... components) {
        super(components);
    }
}
