package org.vaadin.firitin.components;

import com.vaadin.flow.component.Component;
import org.vaadin.firitin.fluency.ui.FluentClickNotifier;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasComponents;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;

public class VDiv extends GenericDiv
        implements FluentComponent<VDiv>, FluentClickNotifier<GenericDiv, VDiv>, FluentHasComponents<VDiv>,
        FluentHasStyle<VDiv>
{
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
