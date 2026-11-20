package org.vaadin.firitin.fields.searchselect;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * Test and demo view: picking a training type out of ~100, like in NettiHPK's
 * training editor.
 */
@Route("searchselect")
public class TrainingTypesView extends VerticalLayout {

    public TrainingTypesView() {
        SearchSelect<TrainingType> type = new SearchSelect<>("Training type", TrainingType.all());
        type.setItemLabelGenerator(TrainingType::label);
        type.setPlaceholder("Type e.g. pk juo");
        type.setWidth("22em");

        Paragraph selected = new Paragraph("Selected: –");
        selected.setId("selected");
        type.addValueChangeListener(event -> selected.setText("Selected: "
                + (event.getValue() == null ? "–" : event.getValue().label())
                + (event.isFromClient() ? " (user)" : " (server)")));

        Button selectOnServer = new Button("Select on server: Hiihto – Vaellus",
                event -> type.setValue(new TrainingType("Hiihto", "Vaellus")));

        add(type, selected, selectOnServer);
    }
}
