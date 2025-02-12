package org.vaadin.firitin.rad;

import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.HasHelper;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.function.SerializableConsumer;
import jakarta.validation.Configuration;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.MessageInterpolator;
import jakarta.validation.Path;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.firitin.components.button.DefaultButton;
import org.vaadin.firitin.components.button.DeleteButton;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.form.FormBinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A form that automatically creates fields for all properties of a bean. Not meant to created directly, but through
 * {@link AutoFormContext}.
 */
public class AutoForm<T> extends Composite<Div> implements ValueContext {

    public static final int SHORT_STRING_THRESHOLD = 50;

    private final FormBinder<T> formBinder;
    private final AutoFormContext autoFormContext;
    private final BasicBeanDescription beanDescription;
    private T value;

    private List<PropertyHeaderPrinter> headerPrinters = new ArrayList<>();
    private Validator validator;
    private Class<?>[] validationGroups;
    private boolean hasChanges;
    private SerializableConsumer<T> saveHandler;
    private SerializableConsumer<T> deleteHandler;
    private SerializableConsumer<T> resetHandler;
    private Dialog dialog;
    private Button saveButton = new DefaultButton("Save", e -> {
        saveHandler.accept(getValue());
        if (dialog != null) {
            dialog.close();
        }
        hasChanges = false;
        if(isAttached()) {
            adjustSaveButtonState();
        }
    }) {{
        setEnabled(false);
    }};
    private Button deleteButton = new DeleteButton(() -> {
        deleteHandler.accept(getValue());
        if (dialog != null) {
            dialog.close();
        }
    });
    private Button resetButton = new VButton("Reset", e -> {
        resetHandler.accept(getValue());
        if (dialog != null) {
            dialog.close();
        }
    });

    AutoForm(AutoFormContext autoFormContext, BasicBeanDescription beanDescription, T value) {
        this.autoFormContext = autoFormContext;

        this.beanDescription = beanDescription;
        this.formBinder = new FormBinder<T>(beanDescription);
        this.value = value;

        formBinder.addValueChangeListener(e -> {
            if (e.isFromClient()) {
                hasChanges = true;
                doBeanValidation();
                resetButton.setEnabled(true);
                adjustSaveButtonState();
            }
        });


    }

    private static boolean isLongString(Object object) {
        return object != null && object.toString().length() > SHORT_STRING_THRESHOLD;
    }

    static String toShortString(Object object) {
        String toString = object == null ? "" : object.toString();
        return toShortString(toString);
    }

    static String toShortString(String toString) {
        if (toString == null) {
            return "";
        }
        if (toString.length() > SHORT_STRING_THRESHOLD) {
            toString = toString.substring(0, 50) + "...";
            ;
        }
        return toString;
    }

    static String deCamelCased(String propertyName) {
        String deCamelCased = propertyName.replaceAll("([a-z])([A-Z]+)", "$1 $2");
        deCamelCased = StringUtils.capitalize(deCamelCased);
        return deCamelCased;
    }

    private void buildTable() {
        FormLayout formLayout = new FormLayout();
        beanDescription.findProperties().forEach(p -> {
            if (autoFormContext.getHiddenProperties().contains(p.getName())) {
                return;
            }
            new PropertyContextImpl(this, p);
            PropertyContext propertyContext = getPropertyContext(p);

            Object value = null;
            for (PropertyPrinter propertyPrinter : autoFormContext.getPropertyPrinters()) {
                value = propertyPrinter.printValue(propertyContext);
                if (value != null) {
                    if (HasValue.class.isAssignableFrom(value.getClass())) {
                        // Data binding with the form binder
                        formBinder.bindProperty(p, (HasValue) value);
                    }

                    if (autoFormContext.isAnnotateTypes()) {
                        if (HasHelper.class.isAssignableFrom(value.getClass())) {
                            ((HasHelper) value).setHelperText(p.getPrimaryType().getRawClass().toString());
                        } else {
                            HasElement element = (HasElement) value;
                            // As a fallback, set to the title attribute
                            element.getElement().setAttribute("title", p.getPrimaryType().getRawClass().toString());
                        }
                    }

                    // PropertyPrinter can override header if it wants, otherwise use the first one that returns or
                    // default header
                    Object propertyHeader = propertyPrinter.getPropertyHeader(propertyContext);
                    if (propertyHeader == null) {
                        propertyHeader = headerPrinters.stream().map(headerPrinter -> headerPrinter.printHeader(propertyContext))
                                .filter(h -> h != null).findFirst().orElse(PropertyHeaderPrinter.defaultHeader(propertyContext));
                    }
                    if (propertyHeader instanceof Component c) {
                        throw new RuntimeException("WTF!?");
                    } else {
                        if (value != null && value instanceof HasLabel hl) {
                            hl.setLabel(propertyHeader.toString());
                        } else {
                            // TODO wrap the content somehow!?

                        }
                    }
                    break;
                }
            }

            if (value != null) {
                if (value instanceof Component c) {
                    formLayout.add(c);
                } else {
                    formLayout.add(value.toString());
                }
            } else {
                throw new RuntimeException("No printer found for " + p.getName());
                /*

                TableHeaderCell tableHeaderCell = tableRow.addHeaderCell();
                tableHeaderCell.setText(p.getName());
                Object value1 = p.getGetter().getValue(context.value());
                tableRow.addCells((value1 == null ? "null" : value1.toString()) + " (no printer found)");

                 */
            }

        });

        getContent().add(formLayout);
        if (value != null) {
            formBinder.setValue(value);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        getContent().removeAll();
        buildTable();
        Div display = new Div();
        display.setClassName("bean-validation-display");
        getContent().add(display);
        formBinder.setClassLevelViolationDisplay(display);
    }

    public AutoForm withPropertyHeaderPrinter(PropertyHeaderPrinter printer) {
        headerPrinters.add(0, printer);
        return this;
    }

    public Component getFormBody() {
        return this;
    }

    public T getValue() {
        return formBinder.getValue();
    }

    public FormBinder<T> getBinder() {
        return formBinder;
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    public ValueContext parent() {
        return null;
    }

    @Override
    public BasicBeanDescription beanDescription() {
        return beanDescription;
    }

    @Override
    public Locale getLocale() {
        return getFormBody().getUI().get().getLocale();
    }

    @Override
    public PrettyPrinter getPrettyPrinter() {
        return null;
    }

    @Override
    public PropertyContext getPropertyContext(BeanPropertyDefinition property) {
        return new PropertyContextImpl(this, property);
    }

    @Override
    public int getLevel() {
        return 0;
    }

    public void setSaveHandler(SerializableConsumer<T> saveHandler) {
        this.saveHandler = saveHandler;
    }

    public void setDeleteHandler(SerializableConsumer<T> deleteHandler) {
        this.deleteHandler = deleteHandler;
    }

    public void setResetHandler(SerializableConsumer<T> resetHandler) {
        this.resetHandler = resetHandler;
    }

    public Component getActions() {
        if (resetHandler == null) {
            resetButton.setVisible(false);
        }
        if (deleteHandler == null) {
            deleteButton.setVisible(false);
        }
        if (saveHandler == null) {
            saveButton.setVisible(false);
        }

        return new HorizontalLayout(saveButton, deleteButton, resetButton);
    }

    @Deprecated(forRemoval = true)
    public AutoForm<T> withBeanValidation() {
        return this;
    }

    /**
     * Adjust save button state. Override if you for example want to have Save
     * button always enabled, even if the Binder has not tracked any changes
     * yet.
     */
    protected void adjustSaveButtonState() {
        if (value != null) {
            boolean valid = formBinder.isValid();
            saveButton.setEnabled(hasChanges && valid);
        }
    }


    protected void doBeanValidation() {
        if (autoFormContext.isDefaultBeanValidation()) {
            try {
                T object = (T) getBinder().getValue();
                Class<?>[] groups = getValidationGroups();
                Set<ConstraintViolation<T>> constraintViolations;
                if (groups != null) {
                    constraintViolations = getValidator().validate(object, groups);
                } else {
                    constraintViolations = getValidator().validate(object);
                }
                // TODO figure out if this is a good default, strict developer would use groups!!
                // clear violations for which there is a field but not a UI field (e.g. id for new JPA entity)
                constraintViolations.removeIf(v -> {
                    try {
                        Path propertyPath = v.getPropertyPath();
                        String propertyName = propertyPath.toString();
                        List<String> boundProperties = getBinder().getBoundProperties();
                        if (!boundProperties.contains(propertyName)) {
                            return true;
                        }
                    } catch (Exception e) {
                        // ignore
                        Logger.getLogger(AutoForm.class.getName()).log(Level.FINE, "Ignoring constraint violation", e);
                    }

                    return false;
                });

                formBinder.setConstraintViolations(constraintViolations);
            } catch (Throwable e) {
                // TODO catch error if BeanValidation is not on the classpath and log + ignore
                new RuntimeException(e);
            }
        }
    }

    protected Validator getValidator() {
        if (validator == null) {
            Locale locale = getLocale();
            Configuration<?> configuration = Validation.byDefaultProvider().configure();
            MessageInterpolator defaultMessageInterpolator = configuration.getDefaultMessageInterpolator();
            ValidatorFactory factory = configuration
                    .messageInterpolator(new MessageInterpolator() {
                        @Override
                        public String interpolate(String messageTemplate, Context context) {
                            // Override the locale to come from the form (~ UI), instead of JVM default
                            return defaultMessageInterpolator.interpolate(messageTemplate, context, locale);
                        }

                        @Override
                        public String interpolate(String messageTemplate, Context context, Locale locale) {
                            return defaultMessageInterpolator.interpolate(messageTemplate, context, locale);
                        }
                    }).buildValidatorFactory();
            validator = factory.getValidator();
        }
        return validator;
    }

    public Class<?>[] getValidationGroups() {
        return validationGroups;
    }

    public void setValidationGroups(Class<?>... groups) {
        // TODO figure out if this is the right place
        this.validationGroups = groups;
    }


    public Dialog openInDialog() {
        dialog = new Dialog();
        dialog.setHeaderTitle("Edit " + beanDescription.getBeanClass().getSimpleName());
        dialog.add(this);
        dialog.getFooter().add(getActions());
        dialog.open();
        return dialog;
    }

    AutoFormContext getAutoFormContext() {
        return autoFormContext;
    }
}
