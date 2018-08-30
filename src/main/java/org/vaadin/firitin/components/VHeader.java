package org.vaadin.firitin.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import org.vaadin.firitin.fluency.ui.FluentHasComponents;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;

public class VHeader extends H1 implements FluentHasComponents<VHeader>, FluentHasStyle<VHeader> {
    public VHeader() {
        super();
    }

    public VHeader(Component... components) {
        super(components);
    }

    public VHeader(String text) {
        super(text);
    }
}
