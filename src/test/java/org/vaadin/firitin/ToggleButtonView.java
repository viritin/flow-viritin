package org.vaadin.firitin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.aura.Aura;
import com.vaadin.flow.theme.lumo.Lumo;
import org.vaadin.firitin.components.button.DefaultButton;
import org.vaadin.firitin.components.checkbox.ToggleButton;

@Route
public class ToggleButtonView extends VerticalLayout {

    ToggleButton toggle = new ToggleButton("Toggle");

    public ToggleButtonView() {
        add(toggle);

        add(new ToggleButton("Switch first (reverse variant removed)") {{
            removeThemeVariants(CheckboxVariant.AURA_REVERSE);
        }});

        add(new Checkbox("Use aura version", event -> {
            boolean aura = event.getValue();
            if(aura) {
                // Add some color
                UI.getCurrent().addClassName("aura-accent-purple");
                Uihacker.adjustUITheme(UI.getCurrent(), Aura.class);
            } else {
                Uihacker.adjustUITheme(UI.getCurrent(), Lumo.class);
            }

        }));

        add(new DefaultButton("Default button as a reference"));

    }
}
