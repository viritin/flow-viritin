package com.niafikra.dimension.commons.ui.field;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.combobox.ComboBox;
import org.vaadin.firitin.components.combobox.VComboBox;
import org.vaadin.firitin.fluency.ui.*;
import org.vaadin.firitin.fluency.ui.internal.FluentHasAutofocus;
import org.vaadin.firitin.fluency.ui.internal.FluentHasLabel;

import java.util.Arrays;

public class EnumSelect<T extends Enum> extends VComboBox<T> implements FluentHasSize<VComboBox<T>>, FluentHasValidation<VComboBox<T>>, FluentFocusable<ComboBox<T>, VComboBox<T>>, FluentComponent<VComboBox<T>>, FluentHasLabel<VComboBox<T>>, FluentHasAutofocus<VComboBox<T>>, FluentHasStyle<VComboBox<T>>, FluentHasValueAndElement<VComboBox<T>, AbstractField.ComponentValueChangeEvent<ComboBox<T>, T>, T>, FluentHasHelper<VComboBox<T>> {

    public EnumSelect(Class<T> enumClass) {
        this(null, enumClass);
    }

    public EnumSelect(String caption, Class<T> enumClass) {
        super(caption, Arrays.asList(enumClass.getEnumConstants()));
    }

}
