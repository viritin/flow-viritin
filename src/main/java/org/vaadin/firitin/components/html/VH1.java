package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import org.vaadin.firitin.fluency.ui.FluentClickNotifier;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VH1 extends H1 implements FluentHtmlContainer<VH1>, FluentClickNotifier<H1, VH1> {
    public VH1() {
        super();
    }

    public VH1(Component... components) {
        super(components);
    }

    public VH1(String text) {
        super(text);
    }
}
