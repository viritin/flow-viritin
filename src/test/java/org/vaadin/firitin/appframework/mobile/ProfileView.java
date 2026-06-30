package org.vaadin.firitin.appframework.mobile;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.aura.Aura;
import org.vaadin.firitin.TestTheme;
import org.vaadin.firitin.appframework.MenuItem;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

@Route(value = "profile", layout = MobileDemoLayout.class)
@MenuItem(icon = VaadinIcon.USER)
@TestTheme(Aura.class)
public class ProfileView extends VVerticalLayout {
    public ProfileView() {
        // A basic form to see how regular input components sit in the mobile
        // layout (and how the scroll-away title / bottom bar behave with a form).
        TextField firstName = new TextField("First name");
        TextField lastName = new TextField("Last name");
        EmailField email = new EmailField("Email");
        DatePicker birthDate = new DatePicker("Date of birth");
        Checkbox newsletter = new Checkbox("Subscribe to newsletter");
        TextArea bigArea = new TextArea("Big area");

        bigArea.setSizeFull();

        FormLayout form = new FormLayout(firstName, lastName, email, birthDate, newsletter);
        form.setColspan(newsletter, 2);

        Button save = new Button("Save");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(form);
        addAndExpand(bigArea);
        add(save);
        // No manual bottom padding needed: MobileMainLayout reserves space for the
        // floating bottom bar (measured), so the Save button stays visible above it.
    }
}
