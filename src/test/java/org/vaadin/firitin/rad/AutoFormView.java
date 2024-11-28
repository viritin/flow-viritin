package org.vaadin.firitin.rad;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.LocalDateTimeToDateConverter;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.router.Route;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Route
public class AutoFormView extends VerticalLayout {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    private boolean autovalidate = false;

    public AutoFormView() {
        PersonRecord person = new PersonRecord(
                "John", "Doe", 42, true
        );

        PersonPojo personPojo = new PersonPojo(
                "John", "Doe", 42,
                List.of(new PhoneNumber("Home", "1234567890"),
                        new PhoneNumber("Work", "12345666")
                )
        );
        {
            personPojo.setDescription("This is a person with soem description");
            //personPojo.setOldSchoolDate(new Date());
        }

        add(new H1("AutoFormView"));


        AutoForm autoForm = AutoForm.getDefault();
        HorizontalLayout horizontalLayout = new HorizontalLayout() {
            {

                Form<PersonPojo> form = autoForm.createForm(personPojo);
                //form.getBinder().setConverter("oldSchoolDate", new LocalDateToDateConverter(ZoneId.systemDefault()));

                form.getBinder().addValueChangeListener(e1 -> {
                    if (autovalidate && e1.isFromClient()) {
                        var violations = validator.validate(form.getValue());
                        form.getBinder().setConstraintViolations(violations);
                    }
                });


                add(form.getComponent());
                add(new VerticalLayout() {
                    {
                        add(
                                new Button("Show Value ->", e -> {
                                    if (getChildren().count() > 2)
                                        getChildren().toList().get(2).removeFromParent();
                                    if (form.getValue() != null) {
                                        add(PrettyPrinter.toVaadin(form.getValue()));
                                    }
                                }),
                                new Button("Validate", e -> {
                                    var violations = validator.validate(form.getValue());
                                    form.getBinder().setConstraintViolations(violations);
                                }),
                                new Checkbox("Toggle autovalidation", e -> {
                                    autovalidate = !autovalidate;
                                    Notification.show("Autovalidate is now " + autovalidate);
                                })
                        );
                    }
                });
            }
        };

        add(horizontalLayout);


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

        public PersonPojo(String firstName, String lastName, int age, List<PhoneNumber> phoneNumbers) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
            this.phoneNumbers = phoneNumbers;
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
    }

}
