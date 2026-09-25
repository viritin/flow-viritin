package org.vaadin.firitin.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;
import org.vaadin.firitin.util.style.VaadinCssProps;

/**
 * Supporting text on a line of its own, in the theme's secondary text color
 * ({@code --vaadin-text-color-secondary}, defined by both Aura and Lumo). For the
 * lines that sit under a headline value or a title: "Updated 10:56", "Humidity
 * 47 %", a unit or a short explanation.
 * <p>
 * Block-level, so several of them stack without a layout around them.
 */
public class SecondaryText extends Div implements FluentHtmlContainer<SecondaryText> {

    public SecondaryText() {
        getStyle().setColor(VaadinCssProps.TEXT_COLOR_SECONDARY.var());
    }

    public SecondaryText(String text) {
        this();
        setText(text);
    }

    public SecondaryText(Component... components) {
        this();
        add(components);
    }
}
