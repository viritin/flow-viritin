/*
 * Copyright 2017 Matti Tahvonen.
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
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
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
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.util.VStyles;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * This is "the next version" of the AbstractForm class, that finally,
 * since V7 era, works properly with e.g. cross field validation &
 * validation groups.
 * <p>
 * This version uses FormBinder (published in Viritin 2.8)
 * instead of the basic Vaadin Binder. This might already be
 * better, but most likely there are regressions and edge case
 * that may be incompatible with the old AbstractForm, thus
 * bringing in this with a different name for testing. Once
 * tested enough and if no big regressions, AbstractForm becomes
 * this and possible a backwards compatibility version is created
 * for "core Binder version".
 *
 * @author mstahv
 * @deprecated handle with care, very little tested and should be considered experimental at this point. API will most likely change, but feedback is more than welcome.
 */
@Deprecated(forRemoval = false)
public abstract class BeanValidationForm<T> extends Composite<Div> {

    private final Class<T> entityType;
    private T entity;
    private SavedHandler<T> savedHandler;
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
    private Class<?>[] validationGroups = new Class[0];
    private Validator validator;

    private Div classLevelViolationsDisplay = new Div();

    /**
     * Constructor for the abstract form.
     *
     * @param entityType The class type used for data binding
     */
    public BeanValidationForm(Class<T> entityType) {
        this.entityType = entityType;
        addAttachListener(e -> lazyInit());
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
        if(binder == null) {
            lazyInit();
        }
        return binder;
    }

    protected void lazyInit() {
        if (!getContent().getChildren().findAny().isPresent()) {
            getContent().add(createContent());
            bind();
        }
    }

    /**
     * By default just does simple name based binding. Override this method to
     * customize the binding.
     */
    protected void bind() {
        binder = new FormBinder<>(entityType, this);
        binder.setClassLevelViolationDisplay(classLevelViolationsDisplay);
        binder.addValueChangeListener(e -> {
            if(e.isFromClient()) {
                hasChanges = true;
                // TODO this is old status change listener, figure out what is really needed
                Set<ConstraintViolation<T>> constraintViolations = doBeanValidation(e.getValue());
                binder.setConstraintViolations(constraintViolations);
                adjustResetButtonState();
                adjustSaveButtonState();
            }
        });
    }

    public void setValidationGroups(Class<?>... groups) {
        this.validationGroups = groups;
    }

    public Class<?>[] getValidationGroups() {
        return validationGroups;
    }


    protected  <T> Set<ConstraintViolation<T>> doBeanValidation(T object) {
        return getValidator().validate(object, getValidationGroups());
    }


    protected Validator getValidator() {
        if(validator == null) {
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
        getFormComponents().forEach(f -> layout.add(f));
        layout.add(classLevelViolationsDisplay);
        layout.add(getToolbar());
        return layout;
    }

    /**
     * Return the list of field components added to the form body by default.
     * Use a dummy implementation if your override createContent() method
     * where you can fully customise how the content of the form is built.
     *
     * @return the fields displayed in the form created by createContent()
     * method.
     */
    protected abstract List<Component> getFormComponents();

    /**
     * Adjust save button state. Override if you for example want to have
     * Save button always enabled, even if the Binder has not tracked any
     * changes yet.
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
        return new DefaultButton(getSaveCaption()).withVisible(false);
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
        } else {
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
        for (Iterator<Component> iter = compositionRoot.getChildren().iterator(); iter.hasNext(); ) {
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
     * A handler called when the built-in reset/cancel button of the form is called.
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
