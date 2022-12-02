package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.shared.HasTooltip;

public interface FluentHasTooltip<S extends FluentHasTooltip<S>> extends HasTooltip {

    default S withTooltip(String tooltip) {
        setTooltipText(tooltip);
        return (S) this;
    }
}