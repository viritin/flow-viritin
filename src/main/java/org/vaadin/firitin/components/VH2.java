package org.vaadin.firitin.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import org.vaadin.firitin.fluency.ui.FluentHasComponents;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;

public class VH2 extends H2 implements FluentHasComponents<VH2>, FluentHasStyle<VH2> {
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
