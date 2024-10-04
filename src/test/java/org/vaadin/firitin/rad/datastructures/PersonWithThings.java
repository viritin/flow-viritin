package org.vaadin.firitin.rad.datastructures;

import org.vaadin.firitin.testdomain.Person;

public class PersonWithThings extends Person {

    public record Gadget(String name, String description, boolean broken) {
    }


    private Gadget mainGadget;

    private Gadget[] gadgets;

    private String[] things;

    private Person supervisor;

    public boolean cool;

    private Boolean coolToo;

    private int integer;
    private Integer integerToo;

    public String[] getThings() {
        return things;
    }

    public void setThings(String[] things) {
        this.things = things;
    }

    public Gadget getMainGadget() {
        return mainGadget;
    }

    public void setMainGadget(Gadget mainGadget) {
        this.mainGadget = mainGadget;
    }

    public Gadget[] getGadgets() {
        return gadgets;
    }

    public void setGadgets(Gadget[] gadgets) {
        this.gadgets = gadgets;
    }

    public Person getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Person supervisor) {
        this.supervisor = supervisor;
    }


    public boolean isCool() {
        return cool;
    }

    public void setCool(boolean cool) {
        this.cool = cool;
    }

    public Boolean getCoolToo() {
        return coolToo;
    }

    public void setCoolToo(Boolean coolToo) {
        this.coolToo = coolToo;
    }

    public int getInteger() {
        return integer;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }

    public Integer getIntegerToo() {
        return integerToo;
    }

    public void setIntegerToo(Integer integerToo) {
        this.integerToo = integerToo;
    }
}
