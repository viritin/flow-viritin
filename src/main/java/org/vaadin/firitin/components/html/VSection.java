package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Section;
import org.vaadin.firitin.fluency.ui.FluentClickNotifier;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VSection extends Section implements FluentHtmlContainer<VSection>, FluentClickNotifier<Section, VSection> {

    public VSection() {
        super();
    }

    public VSection(Component... components) {
        super(components);
    }

}
