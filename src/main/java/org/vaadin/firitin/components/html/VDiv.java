package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import org.vaadin.firitin.fluency.ui.FluentClickNotifier;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VDiv extends Div implements FluentHtmlContainer<VDiv>, FluentClickNotifier<Div, VDiv> {
    public VDiv() {
        super();
    }

    public VDiv(Component... components) {
        super(components);
    }

}
