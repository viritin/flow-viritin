package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Paragraph;
import org.vaadin.firitin.fluency.ui.FluentClickNotifier;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VParagaph extends Paragraph implements FluentHtmlContainer<VParagaph>, FluentClickNotifier<Paragraph, VParagaph> {

    public VParagaph() {
        super();
    }

    public VParagaph(Component... components) {
        super(components);
    }

    public VParagaph(String text) {
        super(text);
    }

}
