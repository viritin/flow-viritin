package org.vaadin.firitin.appframework;

import com.vaadin.flow.component.html.Paragraph;

public class MySecondView extends MyAbstractView {
    public MySecondView() {
        add(new Paragraph("Second content"));
    }
}
