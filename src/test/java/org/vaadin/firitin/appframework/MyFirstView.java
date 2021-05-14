package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouteAlias;

// This gets into menu and title by default
@PageTitle("First")
@RouteAlias(value="", layout = MyMainLayout.class)
public class MyFirstView extends MyAbstractView {
    public MyFirstView() {
        add(new Paragraph("First content"));
    }
}
