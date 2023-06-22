package org.vaadin.firitin.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;

public final class VStyles {

    private VStyles() {

    }

    public static void applyDialogNoPaddingStyle(Dialog dialog) {
        dialog.getElement().getNode().runWhenAttached(
                ui -> ui.getPage().executeJs("$0.$.overlay.classList.add('no-padding')", dialog.getElement()));
    }

    public static Component applyWhiteSpaceNoWrap(Component component) {
        component.getElement().getStyle()
            .set("white-space", "nowrap")
            .set("overflow", "hidden")
            .set("text-overflow", "ellipsis");
        return component;
    }
}
