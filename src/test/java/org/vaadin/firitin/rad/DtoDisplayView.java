package org.vaadin.firitin.rad;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route
public class DtoDisplayView extends VerticalLayout {

    public record PersonRecord(String firstName, String lastName, int age, List<PhoneNumber> phoneNumbers) {
    }

    public record PhoneNumber(String name, String number) {
    }


    public DtoDisplayView() {

        PersonRecord person = new PersonRecord("John", "Doe", 42,List.of(new PhoneNumber("Home", "1234567890"),new PhoneNumber("Work", "12345666")));

        DtoDisplay dtoDisplay = new DtoDisplay(person);

        add(dtoDisplay);


    }



}
