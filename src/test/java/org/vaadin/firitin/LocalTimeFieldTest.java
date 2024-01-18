package org.vaadin.firitin;

import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.datepicker.VDatePicker;
import org.vaadin.firitin.components.datetimepicker.VDateTimePicker;
import org.vaadin.firitin.components.timepicker.VTimePicker;

import java.time.LocalTime;
import java.util.Locale;

@Route
public class LocalTimeFieldTest extends VerticalLayout {

    public LocalTimeFieldTest() {

        TimePicker timePicker = new VTimePicker();
        LocalTime now = LocalTime.now().withNano(0).withSecond(0);

        now = LocalTime.of(now.getHour(), now.getMinute());


        timePicker.setValue(now);

        add(timePicker);

        VDateTimePicker dtp = new VDateTimePicker();
        dtp.setLocale(Locale.FRANCE);
        //dtp.setLocale(Locale.forLanguageTag("fi"));
        add(dtp);

        VDatePicker datePicker = new VDatePicker();
        datePicker.setLocale(Locale.FRANCE);
        add(datePicker);

        //add(cb);
    }
}
