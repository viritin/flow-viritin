package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;

@MenuItem(order = MenuItem.DEFAULT + 3, icon = VaadinIcon.PUZZLE_PIECE, title = "Dynamic")
@Route(value = "dynamic", layout = MyMainLayout.class, registerAtStartup = false)
public class DynamicView extends MyAbstractView {

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (attachEvent.isInitialAttach()) {
            add(new Paragraph("Dynamically registered view content."));
        }
    }
}
