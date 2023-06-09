package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouteAlias;

// PageTitle annotation gets into menu and title by default.
// If missing, one is derived from the class name, "My First" would be the title with this class.
// Overrideable in MainLayout.
@PageTitle("First")
@RouteAlias(value="", layout = MyMainLayout.class)
public class MyFirstView extends MyAbstractView {
    public MyFirstView() {
        add(new Paragraph("First content"));

        add(new Button("Navigate to second", event -> {
            getUI().ifPresent(ui -> ui.navigate(MySecondView.class));
        }));
    }
}
