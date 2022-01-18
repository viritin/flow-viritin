package org.vaadin.firitin;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.Route;
import java.time.LocalTime;

@Route
public class LocalTimeFieldTest extends VerticalLayout {

    public LocalTimeFieldTest() {

        TimePicker timePicker = new TimePicker();
        LocalTime now = LocalTime.now().withNano(0).withSecond(0);

        now = LocalTime.of(now.getHour(), now.getMinute());
        

        timePicker.setValue(now);

        add(timePicker);

        //add(cb);
    }
}
