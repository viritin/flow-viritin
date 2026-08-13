package org.vaadin.firitin.form;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.theme.lumo.LumoUtility;

@StyleSheet("context://assets/org/vaadin/firitin/components/formbinder.css")
class ErrorMessage extends Paragraph {
    public ErrorMessage(String message) {
        super(message);
        /*
           The colour comes from formbinder.css, which picks the token the theme in
           use actually defines. The Lumo utility class is kept because it says the
           same thing for an application that has loaded Lumo's utility.css — but it
           cannot be relied on, since that stylesheet is not part of the theme.
        */
        addClassNames("viritin-error-message", LumoUtility.TextColor.ERROR);
    }
}
