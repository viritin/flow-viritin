package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VLabel extends Label implements FluentHtmlContainer<VLabel> {

    public VLabel() {
        super();
    }

    public VLabel(String text) {
        super(text);
    }

    public VLabel withFor(Component forComponent) {
        setFor(forComponent);
        return this;
    }

    public VLabel withFor(String forId) {
        setFor(forId);
        return this;
    }
}
