package org.vaadin.firitin.formbinder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.fields.CommaSeparatedStringField;
import org.vaadin.firitin.fields.DurationField;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.fields.ElementCollectionField;
import org.vaadin.firitin.fields.EnumSelect;
import org.vaadin.firitin.form.BeanValidationForm;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Route
@MenuItem(icon = VaadinIcon.EDIT)
public class FormFieldsView extends BeanValidationForm<FormFieldsView.MyRecord> {

    public enum MyEnum {
        ONE, TWO, THREE
    }

    // TODO fix element collection field with records
    public record Element(String name, Integer value, MyEnum myEnum) {
    }

    public record MyRecord(MyEnum myEnum, Duration duration, List<Element> children, Set<String> strings) {
    }

    private DurationField duration = new DurationField("Input duration here");
    private EnumSelect<MyEnum> myEnum = new EnumSelect<>(MyEnum.class)
            .withLabel("Select enum value");
    private ElementCollectionField<Element> children = new ElementCollectionField<>(Element.class);
    private CommaSeparatedStringField strings = new CommaSeparatedStringField("Strings separated with commas");

    public FormFieldsView() {
        super(MyRecord.class);
        setEntity(new MyRecord(MyEnum.ONE, Duration.ofHours(1).plusSeconds(30), new ArrayList<>(), Set.of("one", "two", "three")));
        setSavedHandler(mydata -> {
            Notification.show("Saved: " + mydata);
        });
    }

    @Override
    protected List<Component> getFormComponents() {
        return Arrays.asList(duration, myEnum, children, strings);
    }

    @Override
    protected Component createContent() {
        VerticalLayout layout = (VerticalLayout) super.createContent();
        layout.addComponentAsFirst(new RichText().withMarkDown("""
                        # Generic form fields from the Viritin add-on
                                        
                        This view shows some of the generic form fields and their usage. 
                        The form is implemented BeanValidationForm from Viritin add-on,
                        but the fields themselves are compatible with the basic Binder from 
                        Vaadin core as well.
                        """));
        return layout;
    }
}
