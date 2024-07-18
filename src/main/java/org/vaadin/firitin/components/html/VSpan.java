package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;
import org.vaadin.firitin.fluency.ui.FluentClickNotifier;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VSpan extends Span implements FluentHtmlContainer<VSpan>, FluentClickNotifier<Span, VSpan> {
    public VSpan() {
        super();
    }

    public VSpan(Object text) {
        super(text.toString());
    }

    public VSpan(String text) {
        super(text);
    }

    public VSpan(Component... components) {
        super(components);
    }
}
