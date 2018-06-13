package org.vaadin.firitin.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import org.vaadin.firitin.fluency.ui.FluentClickNotifier;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasComponents;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;
import org.vaadin.firitin.fluency.ui.FluentThemableLayout;

@Tag(Tag.SPAN)
public class VSpan extends HtmlContainer implements FluentComponent<VSpan>, FluentClickNotifier<VSpan, VSpan>,
        FluentHasComponents<VSpan>, FluentHasStyle<VSpan>, FluentThemableLayout<VSpan> {
    public VSpan() {
        super();
    }

    public VSpan(Component... components) {
        super(components);
    }
}
