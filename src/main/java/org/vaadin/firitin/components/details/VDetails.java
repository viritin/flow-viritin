package org.vaadin.firitin.components.details;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.function.SerializableSupplier;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasEnabled;
import org.vaadin.firitin.fluency.ui.FluentHasTheme;
import org.vaadin.firitin.fluency.ui.FluentHasTooltip;

public class VDetails extends Details implements FluentComponent<VDetails>, FluentHasEnabled<VDetails>, FluentHasTheme<VDetails>, FluentHasTooltip<VDetails> {

    /**
     * Creates a new instance of Details with the given summary text and content component supplier.
     *
     * This is the recommended way to create a new Details component as it will only create the content component when
     * the details gets opened.
     *
     * @param previewString the summary text that is shown even when the details is closed
     * @param componentSupplier the supplier that creates the content component when the details is opened
     */
    public VDetails(String previewString, SerializableSupplier<Component> componentSupplier) {
        setSummaryText(previewString);
        addOpenedChangeListener(event -> {
            removeAll();
            if (event.isOpened()) {
                add(componentSupplier.get());
            }
        });
    }

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
