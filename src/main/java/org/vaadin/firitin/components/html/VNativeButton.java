package org.vaadin.firitin.components.html;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.NativeButton;
import org.vaadin.firitin.fluency.ui.FluentClickNotifier;
import org.vaadin.firitin.fluency.ui.FluentFocusable;
import org.vaadin.firitin.fluency.ui.FluentHtmlContainer;

public class VNativeButton extends NativeButton implements FluentHtmlContainer<VNativeButton>, FluentClickNotifier<NativeButton, VNativeButton>, FluentFocusable<NativeButton, VNativeButton> {

    public VNativeButton() {
        super();
    }

    public VNativeButton(String text) {
        super(text);
    }

    public VNativeButton(String text, ComponentEventListener<ClickEvent<NativeButton>> clickListener) {
        super(text, clickListener);
    }

}
