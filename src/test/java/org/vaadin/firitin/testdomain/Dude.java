/*
 * Copyright 2014 Matti Tahvonen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vaadin.firitin.testdomain;

import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.ObjectUtils;

/**
 *
 */
@FieldMatch(first = "email", second = "emailConfirm")
public class Dude {
    private int id;

    @NotNull
    @Size(min = 3, max = 15)
    private String firstName;
    private String lastName;

    @NotNull
    private Integer age;

    private Address address = new Address();

    private String email;
    private String emailConfirm;

    private Dude supervisor;

    private List<Dude> subordinates = new ArrayList<Dude>();

    public Dude() {
    }

    public Dude(int id, String firstName, String lastName, int age) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public Dude(String name) {
        this.firstName = name;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailConfirm() {
        return emailConfirm;
    }

    public void setEmailConfirm(String emailConfirm) {
        this.emailConfirm = emailConfirm;
    }

    public Dude getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Dude supervisor) {
        this.supervisor = supervisor;
    }

    public List<Dude> getSubordinates() {
        return subordinates;
    }

    public void setSubordinates(List<Dude> subordinates) {
        this.subordinates = subordinates;
    }

    @Override
    public String toString() {
        return "Person [id=" + id + ", name=" + firstName + " " + lastName + ", age=" + age + ", address=" + address
                + "]";
    }

}
