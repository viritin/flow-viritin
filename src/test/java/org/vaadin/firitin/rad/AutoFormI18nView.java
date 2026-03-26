package org.vaadin.firitin.rad;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Demonstrates i18n support for AutoForm using a ResourceBundle-based FormTranslationProvider.
 */
@Route
public class AutoFormI18nView extends VerticalLayout {

    public enum Status {
        ACTIVE, INACTIVE, PENDING
    }

    public static class Employee {
        private String firstName;
        private String lastName;
        private int age;
        private Status status;

        public Employee() {
        }

        public Employee(String firstName, String lastName, int age, Status status) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
            this.status = status;
        }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public Status getStatus() { return status; }
        public void setStatus(Status status) { this.status = status; }
    }

    public AutoFormI18nView() {
        Locale finnishLocale = new Locale("fi");

        FormTranslationProvider provider = (key, locale) -> {
            try {
                ResourceBundle bundle = ResourceBundle.getBundle("autoform-translations", locale);
                return bundle.containsKey(key) ? bundle.getString(key) : null;
            } catch (MissingResourceException e) {
                return null;
            }
        };

        AutoFormContext context = new AutoFormContext();
        context.setLocale(finnishLocale);
        context.withTranslationProvider(provider);

        Employee employee = new Employee("Matti", "Virtanen", 35, Status.ACTIVE);

        add(new Button("Open Finnish form in dialog...", e -> {
            AutoForm<Employee> form = context.createForm(employee);
            form.setSaveHandler(emp -> Notification.show("Tallennettu: " + emp.getFirstName()));
            form.setDeleteHandler(emp -> Notification.show("Poistettu: " + emp.getFirstName()));
            form.setResetHandler(emp -> Notification.show("Nollattu"));
            form.openInDialog();
        }));

        // Also show inline form
        AutoForm<Employee> inlineForm = context.createForm(employee);
        inlineForm.setSaveHandler(emp -> Notification.show("Tallennettu: " + emp.getFirstName()));
        inlineForm.setDeleteHandler(emp -> Notification.show("Poistettu: " + emp.getFirstName()));
        inlineForm.setResetHandler(emp -> Notification.show("Nollattu"));

        add(inlineForm.getFormBody());
        add(inlineForm.getActions());
    }
}
