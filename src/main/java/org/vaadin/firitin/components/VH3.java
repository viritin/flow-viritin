package org.vaadin.firitin.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H3;
import org.vaadin.firitin.fluency.ui.FluentHasComponents;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;

public class VH3 extends H3 implements FluentHasComponents<VH3>, FluentHasStyle<VH3> {
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
