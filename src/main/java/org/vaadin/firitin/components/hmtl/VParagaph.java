package org.vaadin.firitin.components.hmtl;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Paragraph;
import org.vaadin.firitin.fluency.ui.FluentClickNotifierWithoutTypedSource;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VParagaph extends Paragraph implements FluentHtmlContainer<VParagaph>, FluentClickNotifierWithoutTypedSource<VParagaph> {

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
