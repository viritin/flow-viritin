package org.vaadin.firitin.components.accordion;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.details.DetailsVariant;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasEnabled;
import org.vaadin.firitin.fluency.ui.FluentHasTheme;

public class VAccordionPanel extends AccordionPanel implements FluentComponent<VAccordionPanel>, FluentHasEnabled<VAccordionPanel>, FluentHasTheme<VAccordionPanel> {

    public VAccordionPanel() {
    }

    public VAccordionPanel(String summary, Component content) {
        super(summary, content);
    }

    public VAccordionPanel(Component summary, Component content) {
        super(summary, content);
    }

    public VAccordionPanel withSummary(Component summary) {
        setSummary(summary);
        return this;
    }


    public VAccordionPanel withSummaryText(String summary) {
        setSummaryText(summary);
        return this;
    }

    public VAccordionPanel withContent(Component content) {
        setContent(content);
        return this;
    }


    public VAccordionPanel withOpened(boolean opened) {
        setOpened(opened);
        return this;
    }

    public VAccordionPanel withThemeVariants(DetailsVariant... variants) {
        addThemeVariants(variants);
        return this;
    }

    public VAccordionPanel withOpenedChangeListener(ComponentEventListener<OpenedChangeEvent> listener) {
        addOpenedChangeListener(listener);
        return this;
    }

}
