package org.vaadin.firitin.fluency.ui;

import org.vaadin.firitin.util.style.HasPadding;

import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.ThemableLayout;

@SuppressWarnings("unchecked")
public interface FluentThemableLayout<S extends FluentThemableLayout<S>> extends ThemableLayout, HasPadding<S> {

    default S withMargin(boolean margin) {
        setMargin(margin);
        return (S) this;
    }

    default S withPadding(boolean padding) {
        setPadding(padding);
        return (S) this;
    }

    default S withSpacing(boolean spacing) {
        setSpacing(spacing);
        return (S) this;
    }

    default S withBoxSizing(BoxSizing boxSizing) {
        setBoxSizing(boxSizing);
        return (S) this;
    }
}
