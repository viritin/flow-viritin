package org.vaadin.firitin.util;

import com.vaadin.flow.component.dialog.Dialog;

public final class VStyles {
    
    public static final String STYLE_PADDING_FIX_FOR_DIALOG = "./org/vaadin/firitin/padding-fix-for-dialog.css";

    private VStyles() {

    }

    public static void applyDialogNoPaddingStyle(Dialog dialog) {
        dialog.getElement().getNode().runWhenAttached(
                ui -> ui.getPage().executeJs("$0.$.overlay.classList.add('no-padding')", dialog.getElement()));
    }
}
