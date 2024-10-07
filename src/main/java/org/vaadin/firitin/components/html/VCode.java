package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Tag;
import org.vaadin.firitin.fluency.ui.FluentClickNotifier;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

// TODO add PR to Flow to include Code component, seems to be missing...
@Tag("code")
public class VCode extends HtmlContainer implements FluentHtmlContainer<VCode>, FluentClickNotifier<VCode, VCode> {
    public VCode(String code) {
        setText(code);
    }
}
