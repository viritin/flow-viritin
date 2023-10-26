package org.vaadin.firitin;

import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.html.VH2;
import org.vaadin.firitin.util.style.LumoProps;

@Route
public class LumoPropsView extends VerticalLayout {

    public LumoPropsView() {
        VerticalLayout layout = new VerticalLayout();
        H1 h1 = new H1("Hello there!");

        layout.add(h1);
        layout.add(new VButton("The primary button").withThemeVariants(ButtonVariant.LUMO_PRIMARY));

        layout.add(new VH2("I'm here"));

        add(layout);

        add(new H2("I'm at top level"));

        add(new VButton("The primary button").withThemeVariants(ButtonVariant.LUMO_PRIMARY));

        styleMyView(layout, h1);

    }

    private static void styleMyView(VerticalLayout layout, H1 h1) {
        // "trivial" enum implementation: easy to maintain
        // Not everybody likes me violating Java naming conventions,
        // camel cased variants just looks too ugly to me :-)
        // Not a biggie to change though
        LumoProps.fontFamily.define(layout, "roboto");
        // Global (UI) scope
        LumoProps.primaryColor.define("green");
        LumoProps.primaryColor.define(layout, "red");
        LumoProps.headerTextColor.define(layout, "blue");

        // Using the variable in inline-css is handy with enums
        LumoProps.primaryColor50pct.define("cyan");
        h1.getStyle().setColor(LumoProps.primaryColor.var());
        h1.getStyle().setBackground(LumoProps.primaryColor50pct.var());
    }
}
