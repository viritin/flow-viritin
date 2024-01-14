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

import java.io.Serializable;
import java.util.Iterator;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.vaadin.firitin.components.button.DefaultButton;
import org.vaadin.firitin.components.button.DeleteButton;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.dialog.VDialog;
import org.vaadin.firitin.util.VStyles;

/**
 * A basic form class to avoid a ton of boilerplate code. Provided things like
 * data binding, save and cancel buttons and controls their state based on the
 * input in the form and deals with some of the quirks of Binder.
 * <p>
 * For binding this form uses naming conventions, so for each field of your
 * edited entity, it searches similarly named Vaadin component from your class
 * extending AbstractForm.
 * <p>
 * By default, BeanValidationBinder is used, so validators defined in your DTO
 * or entity class are taken into account. Note that Vaadin don't currently
 * support class level validators so that must be handled separately. Binding is
 * always non-buffered variant, create a clone of your DTO if you mind that it
 * might get changed even if user don't press the save button.
 *
 * @author mstahv
 */
public abstract class AbstractForm<T> extends Composite<Div> {

    private boolean settingBean;

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

    private T entity;
    private SavedHandler<T> savedHandler;
    private ResetHandler<T> resetHandler;
    private DeleteHandler<T> deleteHandler;
    private String modalWindowTitle = "Edit entry";
    private String saveCaption = "Save";
    private String deleteCaption = "Delete";
    private String cancelCaption = "Cancel";
    private VDialog popup;
    private Binder<T> binder;
    private boolean hasChanges = false;

    /**
     * Constructor for the abstract form.
     * 
     * @param entityType The class type used for data binding
     */
    public AbstractForm(Class<T> entityType) {
        addAttachListener(e -> lazyInit());
        binder = new BeanValidationBinder<>(entityType);
        binder.addValueChangeListener(e -> {
            // binder.hasChanges is not really usefull so track it manually
            if (!settingBean) {
                hasChanges = true;
            }
        });
        binder.addStatusChangeListener(e -> {
            // TODO optimize this
            // TODO see if explicitly calling writeBean would write also invalid
            // values -> would make functionality more logical and easier for
            // users to do validation and error reporting

            // Eh, value change listener is called after status change listener, so
            // ensure flag is on...
            if (!settingBean) {
                hasChanges = true;
            }
            adjustResetButtonState();
            adjustSaveButtonState();
        });
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
        this.settingBean = true;
        lazyInit();
        if (entity != null) {
            binder.setBean(entity);
            hasChanges = false;
            setVisible(true);
        } else {
            binder.setBean(null);
            hasChanges = false;
            setVisible(false);
        }
        settingBean = false;
        adjustSaveButtonState();
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

    public boolean isValid() {
        return binder.isValid();
    }

    public void setSavedHandler(SavedHandler<T> savedHandler) {
        this.savedHandler = savedHandler;
        getSaveButton().setVisible(this.savedHandler != null);
    }

    public void setResetHandler(ResetHandler<T> resetHandler) {
        this.resetHandler = resetHandler;
        getResetButton().setVisible(this.resetHandler != null);
    }

    public void setDeleteHandler(DeleteHandler<T> deleteHandler) {
        this.deleteHandler = deleteHandler;
        getDeleteButton().setVisible(this.deleteHandler != null);
    }

    public ResetHandler<T> getResetHandler() {
        return resetHandler;
    }

    public SavedHandler<T> getSavedHandler() {
        return savedHandler;
    }

    public DeleteHandler<T> getDeleteHandler() {
        return deleteHandler;
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

    public Binder<T> getBinder() {
        return binder;
    }

    public void setBinder(Binder<T> binder) {
        this.binder = binder;
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
        binder.bindInstanceFields(this);
    }

    /**
     * This method should return the actual content of the form, including
     * possible toolbar.
     *
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
     *
     */
    protected abstract Component createContent();

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

    protected Button createSaveButton() {
        return new DefaultButton(getSaveCaption()).withVisible(false);
    }

    private Button saveButton;

    protected boolean isBound() {
        return binder != null && binder.getBean() != null;
    }

    protected Button createResetButton() {
        return new VButton(getCancelCaption()).withVisible(false);
    }

    private Button resetButton;

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

    private Button deleteButton;

    public void setDeleteButton(final Button deleteButton) {
        this.deleteButton = deleteButton;
        deleteButton.addClickListener(this::delete);
    }

    public Button getDeleteButton() {
        if (deleteButton == null) {
            setDeleteButton(createDeleteButton());
        }
        return deleteButton;
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

    public void setSaveButton(Button button) {
        this.saveButton = button;
        saveButton.addClickListener(this::save);
    }

    /**
     * @return the currently edited entity or null if the form is currently
     * unbound
     */
    public T getEntity() {
        return entity;
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
     *
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

}
