package org.vaadin.firitin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.VFormLayout;
import org.vaadin.firitin.fields.SubListSelector;
import org.vaadin.firitin.testdomain.Person;
import org.vaadin.firitin.testdomain.Service;

import java.util.List;

/**
 * Created by mstahv
 */
@Route
public class SubListSelectorView extends VFormLayout {

    public SubListSelectorView() {

        List<Person> listOfPersons = Service.getListOfPersons(100);

        List<Person> personList = listOfPersons.subList(3, 7);

        SubListSelector<Person> persons = new SubListSelector<>(Person.class);

        persons.setAvailableOptions(listOfPersons);
        persons.setValue(personList);

        addFormItem(persons, "Select a list of persons from a larger set of people.", 2);

        addFormItem(new Button("Show value", e-> Notification.show(persons.getValue().toString())), "");

    }
}
