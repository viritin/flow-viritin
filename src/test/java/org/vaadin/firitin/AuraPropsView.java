package org.vaadin.firitin;

import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.aura.Aura;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.util.style.AuraProps;
import org.vaadin.firitin.util.style.VaadinCssProps;


@TestTheme(Aura.class)
// With default this view only works if navigating from another, thus fixed url. TODO Figure out what causes this
@Route("theme_props_aura")
public class AuraPropsView extends VerticalLayout {

    public AuraPropsView() {
        // Global: custom accent and background colors
        AuraProps.ACCENT_COLOR_LIGHT.define("#e65100");
        AuraProps.BACKGROUND_COLOR_LIGHT.define("#fff8e1");
        AuraProps.FONT_FAMILY.define("Comic Sans MS, Georgia, serif");
        AuraProps.BASE_RADIUS.define("8");

        var h1 = new H1("Aura Props Demo");
        h1.getStyle().setColor(AuraProps.ACCENT_TEXT_COLOR.var());

        var intro = new Paragraph("This view demonstrates AuraProps helper for Aura theme CSS properties.");

        var accentButton = new VButton("Accent Button")
                .withThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Scoped section with different surface level and palette color
        var scopedSection = new ScopedSection();

        add(h1, intro, accentButton, scopedSection);
    }

    static class ScopedSection extends VerticalLayout {{
        AuraProps.SURFACE_LEVEL.define(this, "3");
        AuraProps.ACCENT_COLOR_LIGHT.define(this, AuraProps.PURPLE.var());
        getStyle()
                .setBackground(AuraProps.SURFACE_COLOR.var())
                .setPadding(VaadinCssProps.PADDING_L.var())
                .setBorderRadius(VaadinCssProps.RADIUS_L.var());

        add(
                new H2("Scoped Section"),
                new Paragraph("This section uses a different surface level and purple accent color, scoped to this layout."),
                new VButton("Scoped Accent Button").withThemeVariants(ButtonVariant.LUMO_PRIMARY),
                new Paragraph("Font family: " + AuraProps.FONT_FAMILY.getCssName()
                        + " resolves to " + AuraProps.FONT_FAMILY.var())
        );
    }}
}
