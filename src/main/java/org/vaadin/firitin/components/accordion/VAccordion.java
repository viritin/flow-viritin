package org.vaadin.firitin.components.accordion;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;

public class VAccordion extends Accordion implements FluentComponent<VAccordion>, FluentHasStyle<VAccordion> {

    public VAccordion withOpen(int index) {
        open(index);
        return this;
    }

    public VAccordion withOpen(AccordionPanel panel) {
        open(panel);
        return this;
    }

    public VAccordion withOpenedChangeListener(ComponentEventListener<OpenedChangeEvent> listener) {
        addOpenedChangeListener(listener);
        return this;
    }

}
