package org.vaadin.firitin.components;

import org.vaadin.firitin.fluency.ui.FluentClickNotifier;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasComponents;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;
import org.vaadin.firitin.fluency.ui.FluentThemableLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;

@Tag(Tag.DIV)
public class VDiv extends HtmlContainer implements FluentComponent<VDiv>, FluentClickNotifier<VDiv, VDiv>,
        FluentHasComponents<VDiv>, FluentHasStyle<VDiv>, FluentThemableLayout<VDiv> {
    public VDiv() {
        super();
    }

    public VDiv(Component... components) {
        super(components);
    }

    @Override
    public VDiv withId(String id) {
        setId(id);
        return this;
    }
}
