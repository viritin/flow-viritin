package org.vaadin.firitin.style;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import in.virit.color.Color;
import in.virit.color.HslColor;
import in.virit.color.NamedColor;
import in.virit.color.RgbColor;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.util.VStyle;

@Route
public class VStyleTypedColorView extends VerticalLayout {
    public VStyleTypedColorView() {

        add("VStyle supports typed colors");

        VButton testButton = new VButton("Test button");
        add(testButton);

        // VStyle is a wrapper for Style, with some enhancements like typed color support
        VStyle style = testButton.getStyle();

        style.setBackgroundColor(NamedColor.AQUAMARINE);
        style.setColor(new HslColor(0, 100, 70));

        // in addition to typing, the used color library supports simple color manipulations
        NamedColor basecolor = NamedColor.DARKGREEN;
        add(new VButton("base ('darkgreen')") {{
            getStyle().setColor(basecolor);
        }});
        add(new VButton("base ('dark green' + lighten 50%)") {{
            getStyle().setColor(basecolor.toRgbColor().toHslColor().lighten(0.5));
        }});

        RgbColor start = new RgbColor(100, 200, 0, 0.8);

        var block = new Div("Color block") {{
            getStyle().setWidth("100px");
            getStyle().setHeight("100px");

        }};
        add(block);

        VStyle blockStyle = VStyle.wrap(block.getStyle());
        blockStyle.setBackgroundColor(start);

        add(new VTextField("Color def(via CssColor.parse()") {{
            setValue(start.toString());
            addValueChangeListener(e -> {
                Color parsed = Color.parseCssColor(e.getValue());
                blockStyle.setBackgroundColor(parsed);
            });
        }});
    }
}
