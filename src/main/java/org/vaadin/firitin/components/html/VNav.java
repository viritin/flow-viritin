package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Nav;
import org.vaadin.firitin.fluency.ui.FluentClickNotifierWithoutTypedSource;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VNav extends Nav implements FluentHtmlContainer<VNav>, FluentClickNotifierWithoutTypedSource<VNav> {

    public VNav() {
        super();
    }

    public VNav(Component... components) {
        super(components);
    }

}
