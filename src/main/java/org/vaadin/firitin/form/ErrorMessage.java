package org.vaadin.firitin.form;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.theme.lumo.LumoUtility;

@StyleSheet("context://frontend/org/vaadin/firitin/components/formbinder.css")
class ErrorMessage extends Paragraph {
    public ErrorMessage(String message) {
        super(message);
        // TODO add default styling to some place or figure out some vaadin base/aura equivalent
        addClassNames("viritin-error-message", LumoUtility.TextColor.ERROR);
    }
}
