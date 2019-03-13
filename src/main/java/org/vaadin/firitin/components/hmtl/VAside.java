package org.vaadin.firitin.components.hmtl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Aside;
import org.vaadin.firitin.fluency.ui.FluentClickNotifierWithoutTypedSource;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VAside extends Aside implements FluentHtmlContainer<VAside>, FluentClickNotifierWithoutTypedSource<VAside> {

    public VAside() {
        super();
    }

    public VAside(Component... components) {
        super(components);
    }
}
