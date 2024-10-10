package org.vaadin.firitin.rad;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.rad.datastructures.PersonWithThings;
import org.vaadin.firitin.testdomain.Address;
import org.vaadin.firitin.testdomain.Group;

import java.time.LocalDateTime;
import java.util.List;

@Route
public class PrettyPrinterView extends VerticalLayout {

    public record PersonRecord(String firstName, String lastName, int age, List<PhoneNumber> phoneNumbers) {
    }

    public record PhoneNumber(String name, String number) {
    }


    public PrettyPrinterView() {
        PersonRecord person = new PersonRecord(
                "John", "Doe", 42,
                List.of(new PhoneNumber("Home", "1234567890"),
                        new PhoneNumber("Work", "12345666")
                )
        );

        add(new RichText().withMarkDown("""
                ## The old hack,
                
                ... pre-formatted pretty printed json serialization, that I want to get rid of:
                
                    new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dto);
                """));

        add(new Pre(toPrettyJson(person)));


        add(new H1("DTO Display, basic usage"));


        add(PrettyPrinter.toVaadin(person));


        add(new RichText().withMarkDown("""
                ## DTO Display, with custom formatting
                
                You can also provide custom formatting for the fields:
                
                        DtoDisplay dtoDisplay = new DtoDisplay(person)
                                .withFormatter("age", age -> age + " years old")
                                .withFormatter("phoneNumbers", phoneNumbers -> {
                                    StringBuilder sb = new StringBuilder();
                                    phoneNumbers.forEach(pn -> sb.append(pn.name()).append(": ").append(pn.number()).append("\n"));
                                    return sb.toString();
                                });
                """));

        PersonWithThings personWithThings = new PersonWithThings();
        personWithThings.setFirstName("John");
        personWithThings.setLastName("Doe");
        personWithThings.setAge(42);
        personWithThings.setJoinTime(LocalDateTime.now());

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

        personWithThings.setThings(new String[]{"Car", "House", "Boat"});

        personWithThings.setMainGadget(new PersonWithThings.Gadget("Smartphone", "A phone that is smart", false));
        personWithThings.setGadgets(new PersonWithThings.Gadget[]{
                new PersonWithThings.Gadget("Smartwatch", "A watch that is smart", false),
                new PersonWithThings.Gadget("Smartglasses", "Glasses that are smart", true)
        });

        add(PrettyPrinter.toVaadin(personWithThings));

    }

    public static String toPrettyJson(Object dto) {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }



}
