package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Main;
import org.vaadin.firitin.fluency.ui.FluentClickNotifier;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VMain extends Main implements FluentHtmlContainer<VMain>, FluentClickNotifier<Main, VMain> {

    public VMain() {
        super();
    }

    public VMain(Component... components) {
        super(components);
    }

}
