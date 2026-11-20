package org.vaadin.firitin.fields;

import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

/**
 * Width handling of DurationField: the inner text field must follow the width
 * set to the field itself, and keep its default width otherwise.
 */
@Route
public class DurationFieldWidthView extends VVerticalLayout {

    public DurationFieldWidthView() {
        add(new VHorizontalLayout(
                new DurationField("Default") {{
                    setId("default");
                }}
        ));
        add(new VHorizontalLayout(
                new DurationField("Narrow") {{
                    setId("narrow");
                    setWidth("6.5em");
                }},
                new NumberField("Amount") {{
                    setId("amount");
                }}
        ));
        add(new VVerticalLayout(
                new DurationField("Full") {{
                    setId("full");
                    setWidthFull();
                }}
        ) {{
            setWidth("400px");
            setPadding(false);
        }});
        add(new VHorizontalLayout(
                new DurationField("Reset to undefined") {{
                    setId("reset");
                    setWidth("20em");
                    setWidth(null);
                }}
        ));
    }
}
