package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Footer;
import org.vaadin.firitin.fluency.ui.FluentClickNotifier;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VFooter extends Footer implements FluentHtmlContainer<VFooter>, FluentClickNotifier<Footer, VFooter> {

    public VFooter() {
        super();
    }

    public VFooter(Component... components) {
        super(components);
    }
}
