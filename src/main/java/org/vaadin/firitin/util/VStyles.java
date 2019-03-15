package org.vaadin.firitin.util;

import com.vaadin.flow.component.dialog.Dialog;

public final class VStyles {
    public static final String STYLE_MODULE_PATH = "org/vaadin/firitin/firitin-styles.html";

    private VStyles() {

    }

    public static void applyDialogNoPaddingStyle(Dialog dialog) {
        dialog.getElement().getNode().runWhenAttached(
                ui -> ui.getPage().executeJavaScript("$0.$.overlay.classList.add('no-padding')", dialog.getElement()));
    }
}
