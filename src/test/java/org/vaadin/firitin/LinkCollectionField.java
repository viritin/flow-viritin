package org.vaadin.firitin;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.select.VSelect;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.form.AbstractForm;
import org.vaadin.firitin.testdomain.Link;

import java.util.ArrayList;
import java.util.List;

public class LinkCollectionField extends Composite<VerticalLayout>
        implements HasValue<HasValue.ValueChangeEvent<List<Link>>, List<Link>>, HasSize {

    class RowEditor extends AbstractForm<Link> {
        TextField text = new VTextField().withPlaceholder("Link text");
        TextField href = new VTextField().withPlaceholder("http://your.link/here");
        VSelect<Link.Target> target = new VSelect<Link.Target>().withPlaceholder("Target");

        public RowEditor() {
            super(Link.class);
            target.setItems(Link.Target.values());
        }

        @Override
        protected Component createContent() {
            return new VHorizontalLayout(text, href, target).alignAll(FlexComponent.Alignment.CENTER).withPadding(false);
        }
    }

    private List<Link> value;

    RowEditor next = new RowEditor();
    Button addEntry = new Button(VaadinIcon.PLUS.create());

    VerticalLayout existingValues = new VVerticalLayout()
            .withMargin(false)
            .withPadding(false)
            .withSpacing(false);

    public LinkCollectionField() {
        value = new ArrayList<>();

        getContent().setPadding(false);
        HorizontalLayout newRowForm = new VHorizontalLayout(
                next, addEntry).withDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        getContent().add(
                new H3("Links"),
                existingValues,
                new Span("New row"),
                newRowForm);

        next.setEntity(new Link());
        addEntry.addClickListener(e -> {
            Link newValue = next.getEntity();
            this.value.add(newValue);
            addRow(newValue);
            fireValueChange();
            next.setEntity(new Link());
            next.focusFirst();
        });

    }

    private void addRow(Link newValue) {
        VHorizontalLayout row = new VHorizontalLayout();
        RowEditor editor = new RowEditor();
        editor.getBinder().addValueChangeListener(e -> {
            // mutable object, just fire value change on next level
            fireValueChange();
        });
        VButton deleteButton = new VButton(VaadinIcon.TRASH.create(), e -> {
            value.remove(editor.getEntity());
            existingValues.remove(row);
            fireValueChange();
        });
        row.add(editor, deleteButton);
        editor.setEntity(newValue);
        existingValues.add(row);
    }

    private void fireValueChange() {
        fireEvent(new AbstractField.ComponentValueChangeEvent<LinkCollectionField, List<Link>>(this, this, null, true));
    }

    @Override
    public void setValue(List<Link> value) {
        this.value = value;
        existingValues.removeAll();
        value.forEach(this::addRow);
    }

    @Override
    public List<Link> getValue() {
        return value;
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<? super ValueChangeEvent<List<Link>>> listener) {
        @SuppressWarnings("rawtypes")
        ComponentEventListener componentListener = event -> {
            AbstractField.ComponentValueChangeEvent<LinkCollectionField, List<Link>> valueChangeEvent = (AbstractField.ComponentValueChangeEvent<LinkCollectionField, List<Link>>) event;
            listener.valueChanged(valueChangeEvent);
        };
        return addListener(AbstractField.ComponentValueChangeEvent.class,
                componentListener);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        getContent().setEnabled(false);
    }

    @Override
    public boolean isReadOnly() {
        return getContent().isEnabled();
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {

    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return false;
    }
}
