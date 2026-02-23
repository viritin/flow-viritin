package org.vaadin.firitin;

import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.util.style.VaadinCssProps;

@Route("vaadin_css_props")
public class VaadinCssPropsView extends VerticalLayout {

    public VaadinCssPropsView() {
        // Global: custom colors and spacing
        VaadinCssProps.TEXT_COLOR.define("#1a237e");
        VaadinCssProps.BACKGROUND_COLOR.define("#fafafa");
        VaadinCssProps.BORDER_COLOR.define("#3949ab");

        var h1 = new H1("VaadinCssProps Demo");
        h1.getStyle().setColor(VaadinCssProps.TEXT_COLOR.var());

        var intro = new Paragraph(
                "This view demonstrates VaadinCssProps helper for shared Vaadin base CSS properties.");

        var button = new VButton("Primary Button")
                .withThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(h1, intro, button, new SpacingSection(), new RadiusSection());
    }

    static class SpacingSection extends VerticalLayout {{
        getStyle()
                .setBackground(VaadinCssProps.BACKGROUND_CONTAINER.var())
                .setPadding(VaadinCssProps.PADDING_L.var())
                .setBorderRadius(VaadinCssProps.RADIUS_L.var())
                .set("border", "1px solid " + VaadinCssProps.BORDER_COLOR.var())
                .set("gap", VaadinCssProps.GAP_M.var());

        add(
                new H2("Spacing & Padding"),
                new Paragraph("This section uses base style gap and padding properties."),
                new Paragraph("Padding: " + VaadinCssProps.PADDING_L.getCssName()
                        + " = " + VaadinCssProps.PADDING_L.var()),
                new Paragraph("Gap: " + VaadinCssProps.GAP_M.getCssName()
                        + " = " + VaadinCssProps.GAP_M.var())
        );
    }}

    static class RadiusSection extends VerticalLayout {{
        VaadinCssProps.BORDER_COLOR.define(this, "#e65100");
        getStyle()
                .setBackground(VaadinCssProps.BACKGROUND_CONTAINER_STRONG.var())
                .setPadding(VaadinCssProps.PADDING_M.var())
                .setBorderRadius(VaadinCssProps.RADIUS_M.var())
                .set("border", "2px solid " + VaadinCssProps.BORDER_COLOR.var());

        add(
                new H2("Radius & Colors"),
                new Paragraph("This section uses scoped border color and different radius/background."),
                new VButton("Another Button").withThemeVariants(ButtonVariant.LUMO_PRIMARY),
                new Paragraph("Radius: " + VaadinCssProps.RADIUS_M.getCssName()
                        + " = " + VaadinCssProps.RADIUS_M.var())
        );
    }}
}
