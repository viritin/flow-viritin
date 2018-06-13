package org.vaadin.firitin.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H4;
import org.vaadin.firitin.fluency.ui.FluentHasComponents;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;

public class VH4 extends H4 implements FluentHasComponents<VH4>, FluentHasStyle<VH4> {
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
