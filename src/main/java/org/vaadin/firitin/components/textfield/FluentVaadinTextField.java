package org.vaadin.firitin.components.textfield;

import com.vaadin.flow.component.AbstractField;
import org.vaadin.firitin.fluency.ui.*;
import org.vaadin.firitin.fluency.ui.internal.FluentHasAutofocus;
import org.vaadin.firitin.fluency.ui.internal.FluentHasLabel;

public interface FluentVaadinTextField<S extends FluentVaadinTextField<S, F, T>, F extends AbstractField<F, T>, T> extends
        FluentHasValueAndElement<S, AbstractField.ComponentValueChangeEvent<F, T>, T>,
        FluentHasSize<S>,
        FluentHasValidation<S>,
        FluentFocusable<F, S>,
        FluentHasLabel<S>,
        FluentHasAutofocus<S>,
        FluentHasStyle<S>,
        FluentHasPrefixAndSuffix<S>,
        FluentKeyNotifier<S>,
        FluentInputNotifier<S>,
        FluentHasAutocapitalize<S>,
        FluentHasAutocomplete<S>,
        FluentHasAutocorrect<S>,
        FluentCompositionNotifier<S>,
        FluentHasTheme<S> {
}
