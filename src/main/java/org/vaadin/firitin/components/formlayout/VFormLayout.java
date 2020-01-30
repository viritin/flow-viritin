package org.vaadin.firitin.components.formlayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import org.vaadin.firitin.fluency.ui.*;

import java.util.List;

public class VFormLayout extends FormLayout implements FluentThemableLayout<VFormLayout>,
        FluentHasStyle<VFormLayout>, FluentHasSize<VFormLayout>, FluentHasComponents<VFormLayout> {

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
     * @param label     the label for component
     * @param colspan   the amount of columns this component should consume
     */
    public void addFormItem(Component component, String label, int colspan) {
        FormItem formItem = addFormItem(component, label);
        formItem.getElement().setAttribute("colspan", "" + colspan);
    }

    public VFormLayout withFormItem(Component component, String label, int colspan) {
        addFormItem(component, label, colspan);
        return this;
    }

    public VFormLayout withResponsiveSteps(ResponsiveStep... steps) {
        setResponsiveSteps(steps);
        return this;
    }

    public VFormLayout withResponsiveSteps(List<ResponsiveStep> steps) {
        setResponsiveSteps(steps);
        return this;
    }

    /**
     * shorthand for two col configuration
     *
     * @param position       TOP or ASIDE
     * @param minWidthTwoCol good value 21em
     * @return itself for fluent writing
     */
    public VFormLayout withResponsiveStepsTwoCols(ResponsiveStep.LabelsPosition position, String minWidthTwoCol) {
        return withResponsiveSteps(new ResponsiveStep("0", 1, position),
                new ResponsiveStep(minWidthTwoCol, 2, position));
    }

    /**
     * shorthand for three col configuration
     *
     * @param position         TOP or ASIDE
     * @param minWidthTwoCol   good value 21em
     * @param minWidthThreeCol good value 14em
     * @return itself for fluent writing
     */
    public VFormLayout withResponsiveStepsThreeCols(ResponsiveStep.LabelsPosition position, String minWidthTwoCol, String minWidthThreeCol) {
        return withResponsiveSteps(new ResponsiveStep("0", 1, position),
                new ResponsiveStep(minWidthTwoCol, 2, position),
                new ResponsiveStep(minWidthThreeCol, 3, position));
    }
}
