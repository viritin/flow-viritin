package org.vaadin.firitin.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H5;
import org.vaadin.firitin.fluency.ui.FluentHasComponents;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;

public class VH5 extends H5 implements FluentHasComponents<VH5>, FluentHasStyle<VH5> {
    public VH5() {
        super();
    }

    public VH5(Component... components) {
        super(components);
    }

    public VH5(String text) {
        super(text);
    }
}
