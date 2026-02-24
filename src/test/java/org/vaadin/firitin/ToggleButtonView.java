package org.vaadin.firitin;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.aura.Aura;
import com.vaadin.flow.theme.lumo.Lumo;
import org.vaadin.firitin.components.button.DefaultButton;
import org.vaadin.firitin.components.checkbox.ToggleButton;
import org.vaadin.firitin.util.style.AuraProps;

@Route
public class ToggleButtonView extends VerticalLayout {

    ToggleButton toggle = new ToggleButton("Toggle");

    public ToggleButtonView() {
        Class<?> initialTheme = Uihacker.getUiTheme(UI.getCurrent());
        if(initialTheme == Aura.class) {
            // global flag for base theme
            ToggleButton.baseTheme = Aura.class;
        } else {
            // This is the default, setting explicitly here to make testing easier
            ToggleButton.baseTheme = Lumo.class;
        }

        add(toggle);

        add(new Checkbox("Use aura version", event -> {
            boolean aura = event.getValue();
            if(aura) {
                // Add some color
                UI.getCurrent().addClassName("aura-accent-purple");
                Uihacker.adjustUITheme(UI.getCurrent(), Aura.class);
                toggle.removeFromParent();
                ToggleButton.baseTheme = Aura.class;
                addComponentAsFirst(toggle);
            } else {
                Uihacker.adjustUITheme(UI.getCurrent(), Lumo.class);
                toggle.removeFromParent();
                ToggleButton.baseTheme = Lumo.class;
                addComponentAsFirst(toggle);
            }

        }));

        add(new DefaultButton("Default button as a reference"));

    }
}
