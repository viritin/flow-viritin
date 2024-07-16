package org.vaadin.firitin.gridfiltering;

import java.util.List;

public class Person {
    private String fullName;
    private String email;
    private String profession;

    public Person(String fullName, String email, String profession) {
        this.fullName = fullName;
        this.email = email;
        this.profession = profession;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public static List<Person> getListOfPeople() {
        return List.of(
                new Person("John Doe", "john@doe.com", "gardener"),
                new Person("Jane Doe", "jane@doe.com", "teacher"),
                new Person("Alice Copland", "alice@compland.com", "developer"),
                new Person("Bob Smith", "bob@smith.com", "blacksmith")
        );
    }

}
