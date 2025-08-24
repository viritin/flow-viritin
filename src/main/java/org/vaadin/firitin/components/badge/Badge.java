package org.vaadin.firitin.components.badge;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.Optional;

/**
 * Badge component, build around the same styling as in Vaadin Lumo theme,
 * but:
 * <ul>
 * <li>Can be used in any Vaadin Flow application without Lumo theme</li>
 * <li>Has an actual Java API</li>
 * </ul>
 */
@Tag("viritin-badge")
@StyleSheet("context://frontend/org/vaadin/firitin/components/badge.css")
public class Badge extends Span {

    public Badge withTheme(Theme... themes) {
        for (Theme theme : themes) {
            addClassName(theme.name().toLowerCase());
        }
        return this;
    }

    public Badge withIcon(VaadinIcon vaadinIcon) {
        Icon icon = vaadinIcon.create();
        icon.getStyle().setPadding("var(--lumo-space-xs)"); // Why not in CSS 🤷‍♂️
        add(icon);
        return this;
    }

    public static enum Theme {
        SUCCESS, ERROR, WARNING, CONTRAST, PRIMARY, SMALL, PILL
    }

    public Badge() {
    }

    public Badge(String text) {
        this();
        add(new Span(text));
    }

    public void setText(String text) {
        getSpan().ifPresent(s -> remove(s));
        add(new Span(text));
    }

    private Optional<Span> getSpan() {
        return getChildren().filter(c -> c instanceof Span).findFirst().map(c -> (Span) c);
    }

    public String getText() {
        return getSpan().orElseGet(Span::new).getText();
    }

}
