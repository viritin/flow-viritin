package org.vaadin.firitin.components;

import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.firitin.fluency.ui.*;

public class VTextField extends TextField
        implements FluentHasSize<VTextField>, FluentHasValidation<VTextField>,
        FluentHasStyle<VTextField>, FluentFocusable<TextField, VTextField>, FluentAttachNotifier<VTextField>, FluentDetachNotifier<VTextField> // from the parent classes
    // TODO Fluent implementation of HasValueChangeMode, HasPrefixAndSuffix, InputNotifier, KeyNotifier, CompositionNotifier, HasAutocomplete, HasAutocapitalize, HasAutocorrect
{
}
