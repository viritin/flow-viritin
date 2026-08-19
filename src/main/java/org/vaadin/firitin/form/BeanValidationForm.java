/*
 * Copyright 2024 Matti Tahvonen.
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
package org.vaadin.firitin.form;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import jakarta.validation.Configuration;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.MessageInterpolator;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.vaadin.firitin.components.button.DefaultButton;
import org.vaadin.firitin.components.button.DeleteButton;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.dialog.VDialog;
import org.vaadin.firitin.components.formlayout.VFormLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.util.VStyles;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * This is "the next version" of the AbstractForm class, that finally, since V7
 * era, works properly with e.g. cross field validation and validation groups.
 * <p>
 * This version uses FormBinder (published in Viritin 2.8) instead of the basic
 * Vaadin Binder. This might already be better, but most likely there are
 * regressions and edge case that may be incompatible with the old AbstractForm,
 * thus bringing in this with a different name for testing. Once tested enough
 * and if no big regressions, AbstractForm becomes this and possible a backwards
 * compatibility version is created for "core Binder version".
 * </p>
 * <p>
 * In the default configuration BeanValidationForm expects that fields are
 * lazily pushing their value change events to the server. This way it can
 * automatically adjust e.g. save/cancel buttons based on the state/validity.
 * Either use Viritin fields like VTextField or configure e.g. with
 * setValueChangeMode.
 * </p>
 *
 * @author mstahv
 * @deprecated handle with care, very little tested and should be considered
 * experimental at this point. API will most likely change, but feedback is more
 * than welcome.
 */
@Deprecated(forRemoval = false)
public abstract class BeanValidationForm<T> extends Composite<Div> {

    private final Class<T> entityType;
    private T entity;
    private SavedHandler<T> savedHandler;
    private SavedHandler<T> eagerSavedHandler;
    private ResetHandler<T> resetHandler;
    private DeleteHandler<T> deleteHandler;
    private String modalWindowTitle = "Edit entry";
    private String saveCaption = "Save";
    private String deleteCaption = "Delete";
    private String cancelCaption = "Cancel";
    private VDialog popup;
    private FormBinder<T> binder;
    private boolean hasChanges = false;
    private Button saveButton;
    private Button resetButton;
    private Button deleteButton;
    private Class<?>[] validationGroups;
    private boolean saveOnEnter = true;
    private Validator validator;
    private FormLayout formLayout;

    private HasComponents classLevelViolationsDisplay = new Div();

    /**
     * Constructor for the abstract form.
     *
     * @param entityType The class type used for data binding
     */
    public BeanValidationForm(Class<T> entityType) {
        this.entityType = entityType;
        getContent().setSizeFull();
        addAttachListener(e -> lazyInit());
    }

    /**
     * Sizes this form as a part of a page rather than as the page.
     * <p>
     * The composition root is full size by default, which suits a form that has
     * a view or a modal to itself. Embedded anywhere else the default is a trap
     * with two different faces: as a section of a page a full-height form
     * pushes everything below it off the screen, and inside a popover — which
     * sizes itself by its content — a full-height child is a child with no
     * height at all. This call gives the root full width and content-driven
     * height instead.
     *
     * @return the form, for further configuration
     */
    public BeanValidationForm<T> asSection() {
        getContent().setWidthFull();
        getContent().setHeight(null);
        return this;
    }

    /**
     * Controls whether the save button created by this form submits on ENTER.
     * <p>
     * On by default, and right for a form that has the view to itself. With two
     * bound forms in one view — or one popover — one keypress would perform two
     * saves, so the form that is not the main verdict of the page switches its
     * shortcut off. Affects the {@link DefaultButton} that
     * {@link #createSaveButton()} creates; a save button set explicitly with
     * {@link #setSaveButton(Button)} is managed by whoever created it.
     *
     * @param saveOnEnter true to submit this form on ENTER
     */
    public void setSaveOnEnter(boolean saveOnEnter) {
        this.saveOnEnter = saveOnEnter;
        if (saveButton instanceof DefaultButton db) {
            db.setEnterShortcutEnabled(saveOnEnter);
        }
    }

    /**
     * by default only save button get's enabled when form has any changes<br>
     * you can use this method in case the prefilled entity is already valid and
     * save should be possible to press without any changes<br>
     * if entity is not valid saveButton will stay disabled!
     *
     * @param entity the object to be edited by this form
     */
    public void setEntityWithEnabledSave(T entity) {
        setEntity(entity);
        setHasChanges(true);
        adjustSaveButtonState();
    }

    /**
     * @return true if bean has been changed since last setEntity call.
     */
    public boolean hasChanges() {
        return hasChanges;
    }

    protected void setHasChanges(boolean hasChanges) {
        this.hasChanges = hasChanges;
    }

    @Deprecated
    public boolean isValid() {
        return binder.isValid();
    }

    public ResetHandler<T> getResetHandler() {
        return resetHandler;
    }

    public void setResetHandler(ResetHandler<T> resetHandler) {
        this.resetHandler = resetHandler;
        getResetButton().setVisible(this.resetHandler != null);
    }

    public SavedHandler<T> getSavedHandler() {
        return savedHandler;
    }

    public void setSavedHandler(SavedHandler<T> savedHandler) {
        this.savedHandler = savedHandler;
        getSaveButton().setVisible(this.savedHandler != null);
    }

    public SavedHandler<T> getEagerSavedHandler() {
        return eagerSavedHandler;
    }

    /**
     * Saves as the reader types, without a save button.
     * <p>
     * For a form that is a row in a list or a panel of settings, where a save button
     * of its own would be one button too many. The handler is called after every
     * change the reader makes that leaves the form valid; a change that does not is
     * shown on the field and not saved, and what was stored stays as it was.
     * <p>
     * Shows no button and does not need {@link #setSavedHandler}, which is the
     * other, deliberate way to save. A form may have both, though it is worth asking
     * why.
     *
     * @param eagerSavedHandler called with the entity after each valid change, or
     *        null to stop
     */
    public void setEagerSavedHandler(SavedHandler<T> eagerSavedHandler) {
        this.eagerSavedHandler = eagerSavedHandler;
    }

    public DeleteHandler<T> getDeleteHandler() {
        return deleteHandler;
    }

    public void setDeleteHandler(DeleteHandler<T> deleteHandler) {
        this.deleteHandler = deleteHandler;
        getDeleteButton().setVisible(this.deleteHandler != null);
    }

    public String getSaveCaption() {
        return saveCaption;
    }

    public void setSaveCaption(String saveCaption) {
        this.saveCaption = saveCaption;
        if (saveButton != null) {
            getSaveButton().setText(getSaveCaption());
        }
    }

    public String getModalWindowTitle() {
        return modalWindowTitle;
    }

    public void setModalWindowTitle(String modalWindowTitle) {
        this.modalWindowTitle = modalWindowTitle;
    }

    public String getDeleteCaption() {
        return deleteCaption;
    }

    public void setDeleteCaption(String deleteCaption) {
        this.deleteCaption = deleteCaption;
        if (deleteButton != null) {
            getDeleteButton().setText(getDeleteCaption());
        }
    }

    public String getCancelCaption() {
        return cancelCaption;
    }

    public void setCancelCaption(String cancelCaption) {
        this.cancelCaption = cancelCaption;
        if (resetButton != null) {
            getResetButton().setText(getCancelCaption());
        }
    }

    public FormBinder<T> getBinder() {
        if (binder == null) {
            lazyInit();
        }
        return binder;
    }

    protected void lazyInit() {
        if (!getContent().getChildren().findAny().isPresent()) {
            getContent().add(createContent());
            ensureClassLevelViolationsAreVisible();
            bind();
        }
    }

    /**
     * Puts the class level violation display into the form if the content did not
     * take it.
     * <p>
     * The default {@link #createContent()} adds it, but any form that lays itself
     * out is free to forget — and then every violation that belongs to no single
     * field is rendered into a component with no parent, where nobody sees it.
     * Since class level constraints are much of the reason to use this form at all,
     * losing them silently is the worst of the options; appending the display is
     * the least surprising of the rest.
     */
    private void ensureClassLevelViolationsAreVisible() {
        Component display = getClassLevelViolationsDisplay();
        if (display.getParent().isEmpty()) {
            getContent().add(display);
        }
    }

    protected void bind() {
        binder = new FormBinder<>(entityType, this);
        if (validationGroups != null) {
            binder.setValidationGroups(validationGroups);
        }
        binder.setClassLevelViolationDisplay(classLevelViolationsDisplay);
        binder.addValueChangeListener(e -> {
            if (e.isFromClient()) {
                hasChanges = true;
                // TODO this is old status change listener, figure out what is really needed
                Set<ConstraintViolation<T>> constraintViolations = doBeanValidation(e.getValue());
                binder.setConstraintViolations(constraintViolations);
                adjustResetButtonState();
                adjustSaveButtonState();
            }
        });
        /*
           Added after the validating listener above, which is what lets this one ask
           whether the change just made left the form usable. Without that filter it
           would also fire while the form is being filled programmatically, and save
           whatever half-typed value passed through.
        */
        binder.addValidValueChangeListener(e -> {
            if (eagerSavedHandler != null) {
                eagerSavedHandler.onSave(getEntity());
                // Saved, so there is nothing unsaved left to report.
                hasChanges = false;
                adjustSaveButtonState();
            }
        });
    }

    public Class<?>[] getValidationGroups() {
        return validationGroups;
    }

    public void setValidationGroups(Class<?>... groups) {
        this.validationGroups = groups;
        if (binder != null) {
            binder.setValidationGroups(groups);
        }
    }

    protected <T> Set<ConstraintViolation<T>> doBeanValidation(T object) {
        Class<?>[] groups = getValidationGroups();
        if (groups != null) {
            return getValidator().validate(object, groups);
        } else {
            return getValidator().validate(object);
        }
    }

    /**
     * The validator this form checks its entity with.
     * <p>
     * Built here from the default provider, with the locale taken from the UI rather
     * than from the JVM. That is enough while the constraints are self-contained.
     * <p>
     * It stops being enough as soon as a {@code ConstraintValidator} needs something
     * from the application — a repository to ask whether an identifier is still free,
     * a registry of what exists. A validator built from the default provider is
     * instantiated by reflection, so its injection points stay null and the check
     * fails with {@code HV000028} instead of answering. Message templates are read
     * from {@code ValidationMessages.properties} for the same reason, rather than
     * from an application's own message source.
     * <p>
     * In a Spring application both are already solved by the container's validator,
     * and giving it to the form is two lines:
     *
     * <pre>
     * public PersonForm(jakarta.validation.Validator validator) {
     *     super(Person.class);
     *     this.validator = validator;
     * }
     *
     * &#64;Override
     * protected Validator getValidator() {
     *     return validator;
     * }
     * </pre>
     *
     * There is deliberately no Spring aware version of this class: one overridable
     * method costs less than a second artifact to keep in step. See
     * {@code SpringManagedValidatorTest} in the test sources for the whole example,
     * including what the failure looks like without it.
     *
     * @return the validator, built on first use
     */
    protected Validator getValidator() {
        if (validator == null) {
            Configuration<?> configuration = Validation.byDefaultProvider().configure();
            MessageInterpolator defaultMessageInterpolator = configuration.getDefaultMessageInterpolator();
            ValidatorFactory factory = configuration
                    .messageInterpolator(new MessageInterpolator() {
                        @Override
                        public String interpolate(String messageTemplate, Context context) {
                            // Override the locale to come from the form (~ UI), instead of JVM default
                            return defaultMessageInterpolator.interpolate(messageTemplate, context, getLocale());
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

    /**
     * This method should return the actual content of the form, including
     * possible toolbar.
     * <p>
     * Use setEntity(T entity) to fill in the data. Am example implementation
     * could look like this:
     *
     * <pre>
     * <code>
     * public class PersonForm extends AbstractForm&lt;Person&gt; {
     *
     *     private TextField firstName = new MTextField(&quot;First Name&quot;);
     *     private TextField lastName = new MTextField(&quot;Last Name&quot;);
     *
     *    {@literal @}Override
     *     protected Component createContent() {
     *         return new MVerticalLayout(
     *                 new FormLayout(
     *                         firstName,
     *                         lastName
     *                 ),
     *                 getToolbar()
     *         );
     *     }
     * }
     * </code>
     * </pre>
     *
     * @return the content of the form
     */
    protected Component createContent() {
        VVerticalLayout layout = new VVerticalLayout();
        HasComponents formLayout = getFormLayout();
        getFormComponents().forEach(f -> formLayout.add(f));
        layout.add((Component) formLayout);
        layout.add(getClassLevelViolationsDisplay());
        layout.add(getToolbar());
        return layout;
    }

    protected FormLayout getFormLayout() {
        if(formLayout == null) {
            formLayout = new VFormLayout();
        }
        return formLayout;
    }

    public Component getClassLevelViolationsDisplay() {
        return (Component) classLevelViolationsDisplay;
    }

    public void setClassLevelViolationsDisplay(HasComponents classLevelViolationsDisplay) {
        this.classLevelViolationsDisplay = classLevelViolationsDisplay;
    }

    /**
     * Return the list of field components the default {@link #createContent()}
     * stacks into the form body, in order.
     * <p>
     * Only consulted by that default. A form that overrides
     * {@link #createContent()} to lay itself out never causes this to be called
     * — which is why it is no longer abstract: forcing every such form to
     * declare an empty list said nothing except that the form declines a
     * default it does not use.
     *
     * @return the fields displayed by the default createContent(), empty by
     * default
     */
    protected List<Component> getFormComponents() {
        return List.of();
    }

    /**
     * Adjust save button state. Override if you for example want to have Save
     * button always enabled, even if the Binder has not tracked any changes
     * yet.
     */
    protected void adjustSaveButtonState() {
        if (isBound()) {
            boolean valid = isValid();
            getSaveButton().setEnabled(hasChanges() && valid);
        }
    }

    public Button getSaveButton() {
        if (saveButton == null) {
            setSaveButton(createSaveButton());
        }
        return saveButton;
    }

    public void setSaveButton(Button button) {
        this.saveButton = button;
        saveButton.addClickListener(this::save);
    }

    protected Button createSaveButton() {
        DefaultButton button = new DefaultButton(getSaveCaption());
        button.setVisible(false);
        button.setEnterShortcutEnabled(saveOnEnter);
        return button;
    }

    protected boolean isBound() {
        return binder != null && binder.getValue() != null;
    }

    protected Button createResetButton() {
        return new VButton(getCancelCaption()).withVisible(false);
    }

    public Button getResetButton() {
        if (resetButton == null) {
            setResetButton(createResetButton());
        }
        return resetButton;
    }

    public void setResetButton(Button resetButton) {
        this.resetButton = resetButton;
        this.resetButton.addClickListener(this::reset);
    }

    protected Button createDeleteButton() {
        return new DeleteButton();
    }

    public Button getDeleteButton() {
        if (deleteButton == null) {
            setDeleteButton(createDeleteButton());
            deleteButton.setVisible(deleteHandler != null);
        }
        return deleteButton;
    }

    public void setDeleteButton(final Button deleteButton) {
        this.deleteButton = deleteButton;
        deleteButton.addClickListener(this::delete);
    }

    /**
     * Adjusts the reset button state. Override if you for example wish to keep
     * reset/cancel button enabled even if there is nothing to reset.
     */
    protected void adjustResetButtonState() {
        // due to issues, currently always enabled...
        getResetButton().setEnabled(true);
        /*
        if (getPopup() != null && getPopup().getParent().isPresent()) {
            // Assume cancel button in a form opened to a popup also closes
            // it, allows closing via cancel button by default
            return;
        }
        if (isBound()) {
            boolean modified = hasChanges();
            getResetButton().setEnabled(modified || getPopup() != null);
        } */
    }

    /**
     * @return the currently edited entity or null if the form is currently
     * unbound
     */
    public T getEntity() {
        if(binder.isImmutable()) {
            return binder.getValue();
        }
        return entity;
    }

    /**
     * Sets the object to be edited by this form. This method binds all fields
     * from this form to given objects.
     * <p>
     * If your form needs to manually configure something based on the state of
     * the edited object, you can override this method to do that either before
     * the object is bound to fields or to do something after the bean binding.
     *
     * @param entity the object to be edited by this form
     */
    public void setEntity(T entity) {
        this.entity = entity;
        lazyInit();
        if (entity != null) {
            binder.setValue(entity);
            hasChanges = false;
            setVisible(true);
            /*
               Judge what was just handed in, rather than waiting for the reader to
               touch a field. Without this the form calls itself valid until the
               first change, which is how setEntityWithEnabledSave could offer a
               Save button for an entity that fails validation.

               This does not turn a fresh, empty entity red: the binder does not
               report a missing value for a field the reader has not touched, which
               is what the required indicator is for.
            */
            binder.setConstraintViolations(doBeanValidation(entity));
        } else {
            // Clears the editors and any violations left from the previous entity.
            binder.setValue(null);
            hasChanges = false;
            setVisible(false);
        }
        adjustSaveButtonState();
    }

    protected void save(ClickEvent<Button> e) {
        savedHandler.onSave(getEntity());
        hasChanges = false;
        adjustSaveButtonState();
        adjustResetButtonState();
    }

    protected void reset(ClickEvent<Button> e) {
        resetHandler.onReset(getEntity());
        hasChanges = false;
        adjustSaveButtonState();
        adjustResetButtonState();
    }

    protected void delete(ClickEvent<Button> e) {
        deleteHandler.onDelete(getEntity());
        hasChanges = false;
    }

    /**
     * @return A default toolbar containing save/cancel/delete buttons
     */
    public HorizontalLayout getToolbar() {
        return new HorizontalLayout(getSaveButton(), getResetButton(), getDeleteButton());
    }

    public VDialog openInModalPopup() {
        popup = new VDialog();
        VStyles.applyDialogNoPaddingStyle(popup);
        popup.add(this);
        focusFirst();
        popup.open();
        return popup;
    }

    /**
     * Focuses the first field found from the form. It often improves UX to call
     * this method, or focus another field, when you assign a bean for editing.
     */
    public void focusFirst() {
        findFieldAndFocus(getContent());
    }

    private boolean findFieldAndFocus(Component compositionRoot) {
        for (Iterator<Component> iter = compositionRoot.getChildren().iterator(); iter.hasNext();) {
            Component component = iter.next();

            if (component instanceof Focusable<?>) {
                if (!isReadOnly(component)) {
                    ((Focusable) component).focus();
                    return true;
                }
            }
            if (component.getChildren().count() > 0) {
                if (findFieldAndFocus(component)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isReadOnly(Component component) {
        if (component instanceof HasValue) {
            return ((HasValue) component).isReadOnly();
        }
        return false;
    }

    /**
     * @return the last Popup into which the Form was opened with
     * #openInModalPopup method or null if the form hasn't been use in window
     */
    public Dialog getPopup() {
        return popup;
    }

    public void closePopup() {
        if (getPopup() != null) {
            getPopup().close();
        }
    }

    /**
     * A handler called when the built-in save button of the form is called.
     *
     * @param <T> the entity being edited
     */
    public interface SavedHandler<T> extends Serializable {

        void onSave(T entity);
    }

    /**
     * A handler called when the built-in reset/cancel button of the form is
     * called.
     *
     * @param <T> the entity being edited
     */
    public interface ResetHandler<T> extends Serializable {

        void onReset(T entity);
    }

    /**
     * A handler called when the built-in delete button of the form is called.
     *
     * @param <T> the entity being edited
     */
    public interface DeleteHandler<T> extends Serializable {

        void onDelete(T entity);
    }

}
