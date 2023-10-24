package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@MenuItem(order = MenuItem.END, icon = VaadinIcon.ACADEMY_CAP, enabled = false)
public class MyFourthView extends MyAbstractView {

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (attachEvent.isInitialAttach()) {
            add(new Paragraph("Fourth view content, with declarative title, icon, & ordering"));
            MyMainLayout mainLayout = (MyMainLayout) UI.getCurrent().getChildren().findFirst().get();

            VerticalLayout subview = new VerticalLayout(new Paragraph("Sub view content"), new Button("Close sub view & return to main view", e -> {
                mainLayout.closeSubView();
            }));

            add(new Button("Open sub view", e -> mainLayout.openSubView(subview, "Sub view title")));
        }

    }
}
