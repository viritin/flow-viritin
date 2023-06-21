package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Optional;

@MenuItem(title = "SpecialView (Third)", order = MenuItem.DEFAULT + 2, icon = VaadinIcon.CAR)
public class MyThirdView extends MyAbstractView {

    @Override
    protected void onAttach(AttachEvent e) {
        super.onAttach(e);
        if (e.isInitialAttach()) {
            add(new Paragraph("Third content, with declarative title, icon, & ordering"));
            MyMainLayout mainLayout = e.getUI().findAncestor(MyMainLayout.class);

            VerticalLayout subview = new VerticalLayout(new Paragraph("Sub view content"), new Button("Close sub view & return to main view", e1 -> {
                mainLayout.closeSubView();
            }));

            add(new Button("Open sub view", e1 -> mainLayout.openSubView(subview, "Sub view title")));
        }

    }
}
