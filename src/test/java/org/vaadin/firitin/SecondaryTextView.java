package org.vaadin.firitin;

import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.SecondaryText;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route
public class SecondaryTextView extends VVerticalLayout {

    public SecondaryTextView() {
        add(new Paragraph("SecondaryText is supporting text on its own line, in the theme's secondary text "
                + "color. Several stack without a layout around them."));

        // The typical use: lines under a headline value in a card
        add(new Card() {{
            setTitle("Living room");
            add(new H2("21.4 °C"));
            add(new SecondaryText("Humidity 47 % RH"));
            add(new SecondaryText("Pressure 1014.7 hPa"));
            add(new SecondaryText("Updated 10:56:41"));
        }});

        // Mixed content: text and a link on the same secondary line
        add(new SecondaryText(new Span("Readings come from a BME280. "),
                new Anchor("https://vaadin.com", "Read more")));

        // Fluent API from FluentHtmlContainer
        add(new SecondaryText()
                .withText("Built with the fluent API")
                .withId("fluent-secondary-text"));
    }
}
