package org.vaadin.firitin.rad;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Route
public class AutoFormView extends VerticalLayout {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    private boolean autovalidate = false;

    public AutoFormView() {
        PersonPojo personPojo = new PersonPojo(
                "John", "Doe", 42,
                List.of(new PhoneNumber("Home", "1234567890"),
                        new PhoneNumber("Work", "12345666")
                )
        );

        List<PersonPojo> friends = new ArrayList<>();
        {
            personPojo.setDescription("This is a person with soem description");
            //personPojo.setOldSchoolDate(new Date());

            friends.add(new PersonPojo("Jane", "Doe", 42, List.of(new PhoneNumber("Home", "1234567890"))));
            friends.add(new PersonPojo("Jack", "Doe", 43, List.of(new PhoneNumber("Home", "1234567890"))));
            friends.add(new PersonPojo("Jill", "Doe", 44, List.of(new PhoneNumber("Home", "1234567890"))));
            friends.add(new PersonPojo("Jim", "Doe", 45, List.of(new PhoneNumber("Home", "1234567890"))));
        }

        add(new H1("AutoFormView"));

        AutoFormContext context = new AutoFormContext();

        // Relations as hard for automatic form generation. No good heuristic to decide
        // what to offer as options for ComboBoxes (or whatever used to select one)
        // Thus some intervetion by developer needed.
        // TODO consider if providing the options alone makes sense, probably not as here can also choose the component type

        context.withPropertyEditor(ctx -> {
            if (ctx.beanPropertyDefinition().getPrimaryType().getRawClass() == PersonPojo.class) {
                return new ComboBox<PersonPojo>() {{
                    setItems(friends);
                    setItemLabelGenerator(p -> p.getFirstName() + " " + p.getLastName());
                }};
            }
            return null;
        });
        // Another simple customisation example, change one textfield to textarea
        // Here checking both type and property name, although just the property nam alone would be enough
        context.withPropertyEditor(ctx -> {
            if (ctx.beanPropertyDefinition().getPrimaryType().getRawClass() == String.class
                    && ctx.beanPropertyDefinition().getName().equals("description")) {
                return new TextArea();
            }
            return null;
        });

        add(new Button("Show Dialog...", e -> {
            AutoForm<PersonPojo> form = context.createForm(personPojo);
            form.setSaveHandler(person -> Notification.show("Save action!"));
            form.setResetHandler(person -> Notification.show("Reset action!"));
            form.setDeleteHandler(person -> Notification.show("Delete action!"));
            form.withBeanValidation().openInDialog();
        }));


        HorizontalLayout horizontalLayout = new HorizontalLayout() {
            {

                AutoForm<PersonPojo> form = context.createForm(personPojo)
                        .withBeanValidation();

                form.setSaveHandler(person -> {
                    Notification.show("Save action!");
                    if (getChildren().count() > 2)
                        getChildren().toList().get(2).removeFromParent();
                    if (form.getValue() != null) {
                        add(PrettyPrinter.toVaadin(form.getValue()));
                    }
                });

                form.setResetHandler(person -> {
                    Notification.show("Reset action!");
                });

                form.setDeleteHandler(person -> {
                    Notification.show("Delete action!");
                });

                add(new VerticalLayout(
                        form.getActions(),
                        form.getFormBody()
                ));

                add(
                        new Button("Show Value ->", e -> {
                            if (getChildren().count() > 2)
                                getChildren().toList().get(2).removeFromParent();
                            if (form.getValue() != null) {
                                add(PrettyPrinter.toVaadin(form.getValue()));
                            }
                        })
                );
            }
        };

        add(horizontalLayout);


    }

    public enum Category {
        A, B, C, D
    }

    public record PersonRecord(
            String firstName,
            String lastName,
            int age,
            boolean active

            /* TODO , List<PhoneNumber> phoneNumbers*/) {
    }

    public record PhoneNumber(String name, String number) {
    }

    public static class PersonPojo {

        @NotEmpty
        private String firstName;
        @Size(min = 2, max = 20)
        private String lastName;
        private String description;
        private boolean active;
        @Min(0)
        @Max(110)
        private int age;
        private LocalDate birthDate;
        private LocalDateTime joinTimeStamp;
        private Date oldSchoolDate;
        private List<PhoneNumber> phoneNumbers;
        private Category category;
        private PersonPojo friend;
        private BigDecimal salary;

        public PersonPojo(String firstName, String lastName, int age, List<PhoneNumber> phoneNumbers) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
            this.phoneNumbers = new ArrayList<>(phoneNumbers);
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public List<PhoneNumber> getPhoneNumbers() {
            return phoneNumbers;
        }

        public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
            this.phoneNumbers = phoneNumbers;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public LocalDate getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
        }

        public LocalDateTime getJoinTimeStamp() {
            return joinTimeStamp;
        }

        public void setJoinTimeStamp(LocalDateTime joinTimeStamp) {
            this.joinTimeStamp = joinTimeStamp;
        }

        public Date getOldSchoolDate() {
            return oldSchoolDate;
        }

        public void setOldSchoolDate(Date oldSchoolDate) {
            this.oldSchoolDate = oldSchoolDate;
        }

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }

        public PersonPojo getFriend() {
            return friend;
        }

        public void setFriend(PersonPojo friend) {
            this.friend = friend;
        }

        public BigDecimal getSalary() {
            return salary;
        }

        public void setSalary(BigDecimal salary) {
            this.salary = salary;
        }

    }

}
