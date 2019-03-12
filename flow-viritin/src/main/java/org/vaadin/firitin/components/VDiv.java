package org.vaadin.firitin.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import org.vaadin.firitin.fluency.ui.FluentClickNotifierWithoutTypedSource;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VDiv extends Div implements FluentHtmlContainer<VDiv>, FluentClickNotifierWithoutTypedSource<VDiv> {
    public VDiv() {
        super();
    }

    public VDiv(Component... components) {
        super(components);
    }

}
