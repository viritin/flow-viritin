package org.vaadin.firitin.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.vaadin.firitin.fluency.ui.FluentFlexComponent;
import org.vaadin.firitin.fluency.ui.FluentHasComponents;
import org.vaadin.firitin.fluency.ui.FluentThemableLayout;

public class VFormLayout extends FormLayout implements FluentThemableLayout<VFormLayout>,
        FluentFlexComponent<VFormLayout>, FluentHasComponents<VFormLayout> {

    public VFormLayout() {
        super();
    }

    public VFormLayout(Component... children) {
        super(children);
    }

    /**
     * Adds component with given label and colspan value. By default FormLayout has two columns, so if
     * you want full width component, give 2 as a last parameter.
     *
     * @param component the component
     * @param label the label for component
     * @param colspan the amount of columns this component should consume
     */
    public void addFormItem(Component component, String label, int colspan) {
        FormItem formItem = addFormItem(component, label);
        formItem.getElement().setAttribute("colspan", "" + colspan);
    }
}
