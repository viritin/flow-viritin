package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;

@MenuItem(title = "SpecialView (Third)", order = MenuItem.BEGINNING, icon = VaadinIcon.CAR)
public class MyThirdView extends MyAbstractView {
    public MyThirdView() {
        add(new Paragraph("Third content, with declarative title, icon, & ordering"));
    }
}
