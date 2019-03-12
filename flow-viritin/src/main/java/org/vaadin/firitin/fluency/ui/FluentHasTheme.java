package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.HasTheme;

public interface FluentHasTheme<S extends FluentHasTheme<S>> extends HasTheme {

    default S withThemeNames(String... themeName) {
        addThemeNames(themeName);
        return (S) this;
    }
}
