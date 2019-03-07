package org.vaadin.firitin.components.hmtl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Main;
import org.vaadin.firitin.fluency.ui.FluentClickNotifierWithoutTypedSource;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VMain extends Main implements FluentHtmlContainer<VMain>, FluentClickNotifierWithoutTypedSource<VMain> {

    public VMain() {
        super();
    }

    public VMain(Component... components) {
        super(components);
    }

}
