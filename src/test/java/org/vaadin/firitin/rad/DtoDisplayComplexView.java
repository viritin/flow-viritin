package org.vaadin.firitin.rad;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
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

        add(new DtoDisplay(personWithThings)
                .withDefaultHeader()
                .withPropertyPrinter(ctx -> {
                    if(ctx.getProperty().getName().equals("age")) {
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
                    public String getPropertyHeader(ValueContext ctx) {
                        return "Surname aka last name:";
                    }
                })
        );

    }

    public static String toPrettyJson(Object dto) {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void test() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        PersonWithThings personWithThings = new PersonWithThings();
        personWithThings.setAge(1);
        objectMapper.writeValueAsString(personWithThings);
    }

}
