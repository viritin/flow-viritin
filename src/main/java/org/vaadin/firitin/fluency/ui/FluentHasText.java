package org.vaadin.firitin.fluency.ui;

import com.vaadin.flow.component.HasText;

public interface FluentHasText<S extends FluentHasText<S>> extends HasText {
    
    @SuppressWarnings("unchecked")
    default S withText(String text) {
        setText(text);
        return (S) this;
    }

}
