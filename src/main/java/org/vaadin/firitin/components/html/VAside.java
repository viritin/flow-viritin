package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Aside;
import org.vaadin.firitin.fluency.ui.FluentClickNotifier;
import org.vaadin.firitin.fluency.ui.FluentClickNotifierWithoutTypedSource;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VAside extends Aside implements FluentHtmlContainer<VAside>, FluentClickNotifier<Aside, VAside> {

    public VAside() {
        super();
    }

    public VAside(Component... components) {
        super(components);
    }
}
