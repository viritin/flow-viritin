package org.vaadin.firitin.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H6;
import org.vaadin.firitin.fluency.ui.FluentHasComponents;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;

public class VH6 extends H6 implements FluentHasComponents<VH6>, FluentHasStyle<VH6> {
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
