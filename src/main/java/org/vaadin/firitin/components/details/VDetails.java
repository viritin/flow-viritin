package org.vaadin.firitin.components.details;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasEnabled;
import org.vaadin.firitin.fluency.ui.FluentHasTheme;

public class VDetails extends Details implements FluentComponent<VDetails>, FluentHasEnabled<VDetails>, FluentHasTheme<VDetails> {

    public VDetails() {
    }

    public VDetails(String summary, Component content) {
        super(summary, content);
    }

    public VDetails(Component summary, Component content) {
        super(summary, content);
    }

    public VDetails withSummary(Component summary) {
        setSummary(summary);
        return this;
    }


    public VDetails withSummaryText(String summary) {
        setSummaryText(summary);
        return this;
    }

    public VDetails withContent(Component content) {
        setContent(content);
        return this;
    }


    public VDetails withOpened(boolean opened) {
        setOpened(opened);
        return this;
    }

    public VDetails withThemeVariants(DetailsVariant... variants) {
        addThemeVariants(variants);
        return this;
    }

    public VDetails withOpenedChangeListener(ComponentEventListener<OpenedChangeEvent> listener) {
        addOpenedChangeListener(listener);
        return this;
    }

}
