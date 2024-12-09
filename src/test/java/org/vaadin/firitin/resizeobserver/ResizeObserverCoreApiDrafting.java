package org.vaadin.firitin.resizeobserver;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.components.html.VDiv;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.rad.PrettyPrinter;
import org.vaadin.firitin.util.ResizeObserver;
import org.vaadin.firitin.util.style.LumoProps;

@Route
public class ResizeObserverCoreApiDrafting extends VVerticalLayout {

    private Registration registration;

    public ResizeObserverCoreApiDrafting() {
        setSizeFull();
        add(new RichText().withMarkDown("""
            This is a test of a potential API to Flow core (HasSize interface).
            The ResizeObserver itself could be an internal helper to Flow.
            """));

        var sizeDisplay = new SizeDisplay();

        Button toggle = new Button("Start observing");
        toggle.addClickListener(e -> {
            if (registration == null) {
                registration = addResizeListener(event -> {
                    // This is what users would most often fetch
                    int width = event.getWidht();

                    // print the full dimentions to the screen as a demo of the API
                    sizeDisplay.updateDimentions(event.getDimensions());
                });
                toggle.setText("Stop observing");
            } else {
                registration.remove();
                registration = null;
                toggle.setText("Start observing");
            }
        });

        add(toggle, sizeDisplay);

    }

    class SizeDisplay extends Div {
        {
            getStyle().setBackground(LumoProps.CONTRAST_5PCT.var());
            setWidthFull();
        }

        public void updateDimentions(ResizeObserver.Dimensions dim) {
            removeAll();
            add(PrettyPrinter.toVaadin(dim));
        }

    }
}
