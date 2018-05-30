package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.ThemableLayout;

public interface FluentThemableLayout<S extends FluentThemableLayout<S>> extends ThemableLayout {
    
    @SuppressWarnings("unchecked")
    default S withMargin(boolean margin) {
        setMargin(margin);
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    default S withPadding(boolean padding) {
        setPadding(padding);
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    default S withSpacing(boolean spacing) {
        setSpacing(spacing);
        return (S) this;
    }

    @SuppressWarnings("unchecked")
    default S withBoxSizing(BoxSizing boxSizing) {
        setBoxSizing(boxSizing);
        return (S) this;
    }

}
