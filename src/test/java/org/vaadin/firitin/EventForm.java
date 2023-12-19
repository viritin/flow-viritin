package org.vaadin.firitin;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.fields.MultiDateTimeField;
import org.vaadin.firitin.fields.MultiDateTimeField2;
import org.vaadin.firitin.fields.MultiDateTimeField3;
import org.vaadin.firitin.form.AbstractForm;
import org.vaadin.firitin.testdomain.Event;
import org.vaadin.firitin.testdomain.Link;
import org.vaadin.firitin.util.style.Padding;
import org.vaadin.firitin.util.style.Padding.Side;
import org.vaadin.firitin.util.style.Padding.Size;

import java.time.LocalDateTime;

@Route
public class EventForm extends AbstractForm<Event> {

    private TextField name = new TextField();
    private MultiDateTimeField dates = new MultiDateTimeField();
    private LinkCollectionField links = new LinkCollectionField();
    private AttachementListField attachments = new AttachementListField();

    public EventForm() {
        super(Event.class);
        setDeleteHandler(this::handleDelete);
        setSavedHandler(this::handleSave);
        setResetHandler(this::handleReset);
        final Event event = new Event();
        event.getDates().add(LocalDateTime.now());

        event.getLinks().add(new Link("http://vaadin.com", "Vaadin"));

        setEntity(event);

        DatePicker datePicker = new DatePicker();

        getContent().add(datePicker);

    }

    private void handleSave(Event event) {
        Notification.show("Handle Save");
        System.err.println(event);
        BinderValidationStatus<Event> validate = getBinder().validate();
        if (validate.isOk()) {
            closePopup();
        }
    }

    private void handleDelete(Event person) {
        Notification.show("Handle Delete");
        closePopup();
    }

    private void handleReset(Event person) {
        Notification.show("Handle Reset");
    }

    @Override
    protected Component createContent() {
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);

        VerticalLayout formContents = new VerticalLayout();

        name.setLabel("Name");
        formContents.add(name, new Span("Dates:"), dates, links, attachments);

        content.add(formContents);

        HorizontalLayout toolbar = getToolbar();
        Padding.of(Side.HORIZONTAL, Size.SMALL).apply(toolbar);

        toolbar.add(new Button("Open another in a modal popup", e -> {
            new EventForm().openInModalPopup().setCloseOnOutsideClick(true);
        }));

        content.add(toolbar);

        Button focus = new Button("Focus", e -> {
            focusFirst();
        });
        toolbar.add(focus);


        return content;
    }

}
