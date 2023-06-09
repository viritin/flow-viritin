package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;

public class MySecondView extends MyAbstractView {
    public MySecondView() {
        add(new Paragraph("Second content"));

        add(new Button("Toggle fourth", e-> {
            findAncestor(MyMainLayout.class).getNavigationItems().get(3);
        }));

    }
}
