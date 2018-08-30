package org.vaadin.firitin.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import org.vaadin.firitin.fluency.ui.FluentHasComponents;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;

public class VH1 extends H1 implements FluentHasComponents<VH1>, FluentHasStyle<VH1> {
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
