package org.vaadin.firitin.rad;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Emphasis;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.junit.jupiter.api.Test;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.rad.datastructures.PersonWithThings;
import org.vaadin.firitin.testdomain.Address;
import org.vaadin.firitin.testdomain.Group;

import java.time.LocalDateTime;

@Route
public class DtoDisplayComplexView extends VerticalLayout {

    public DtoDisplayComplexView() {
        add(new RichText().withMarkDown("""
                ## DTO Display, with custom formatting
                                
                You can also provide custom formatting for the fields:
                                
                    new DtoDisplay(personWithThings)
                       .withDefaultHeader()
                       .withPropertyPrinter(ctx -> {
                           if(ctx.getProperty().getName().equals("age")) {
                               return new RichText().withMarkDown("**" + ctx.getPropertyValue() + "**, which is a good age to be.");
                           }
                           return null;
                       });
                """));

        PersonWithThings personWithThings = getPersonWithThings();

        // Probably an app wide configured with DI in real apps
        var printer = new PrettyPrinter()
                .withPropertyHeaderPrinter(ctx -> {
                    // This customises the default header generation
                    String propertyName = ctx.getProperty().getName();
                    String deCamelCased = DtoDisplay.deCamelCased(propertyName);
                    Class<?> rawClass = ctx.getProperty().getPrimaryType().getRawClass();
                    return new RichText("%s:<br><i>(%s)</i>".formatted(deCamelCased, shortenPackageName(rawClass.getName())));
                })
                .withPropertyPrinter(ctx -> {
                    if (ctx.getProperty().getPrimaryType().getRawClass().equals(Boolean.class)) {
                        Boolean value = (Boolean) ctx.getPropertyValue();
                        if(value == null) {
                            return new Emphasis("undefined Boolean value");
                        }
                        return value ? "Yes" : "No";
                    }
                    return null;
                })
                .withPropertyPrinter(ctx -> {
                    if (ctx.getProperty().getName().equals("age")) {
                        return new RichText().withMarkDown("**" + ctx.getPropertyValue() + "**, which is a good age to be.");
                    }
                    return null;
                })
                .withPropertyPrinter(new PropertyPrinter() {
                    @Override
                    public Component printValue(ValueContext ctx) {
                        if (ctx.getProperty().getName().equals("lastName")) {
                            return new RichText().withMarkDown("**%s**".formatted(ctx.getPropertyValue()));
                        }
                        return null;
                    }

                    @Override
                    public Object getPropertyHeader(ValueContext ctx) {
                        // PropertyPrinters, have optional veto for the header they handle
                        return new RichText().withMarkDown("""
                                Surname aka last name:<br/>
                                *%s*
                                """.formatted(ctx.getProperty().getPrimaryType().getRawClass().getName()));
                    }
                });

        add(printer.printToVaadin(personWithThings));

    }

    private static PersonWithThings getPersonWithThings() {
        PersonWithThings personWithThings = new PersonWithThings();
        personWithThings.setSupervisor(personWithThings); // circular reference to show infinite recursion handling
        personWithThings.setFirstName("John");
        personWithThings.setLastName("Doe");
        personWithThings.setAge(42);
        personWithThings.setJoinTime(LocalDateTime.now());
        personWithThings.setIntegerToo(123);

        Address address = new Address();
        address.setStreet("Some street 123");
        address.setCity("Springfield");
        address.setZipCode(12345);
        address.setType(Address.AddressType.Home);
        personWithThings.getAddresses().add(address);

        Group group = new Group();
        group.setName("Admin");
        group.setIidee(1);
        personWithThings.getGroups().add(group);
        group = new Group();
        group.setName("User");
        group.setIidee(2);
        personWithThings.getGroups().add(group);

        personWithThings.setMainGadget(new PersonWithThings.Gadget("Smartphone", "A phone that is smart", false));
        personWithThings.setGadgets(new PersonWithThings.Gadget[]{
                new PersonWithThings.Gadget("Smartwatch", "A watch that is smart", false),
                new PersonWithThings.Gadget("Smartglasses", "Glasses that are smart", true)
        });

        personWithThings.setThings(new String[]{"Car", "House", "Boat"});
        return personWithThings;
    }

    public static String toPrettyJson(Object dto) {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String shortenPackageName(String fullName) {
        String[] parts = fullName.split("\\.");
        for(int i = 0; i < parts.length - 1; i++) {
            parts[i] = parts[i].substring(0, 1);
        }
        return String.join(".", parts);
    }


    @Test
    public void test() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        PersonWithThings personWithThings = new PersonWithThings();
        personWithThings.setAge(1);
        objectMapper.writeValueAsString(personWithThings);
    }

}
