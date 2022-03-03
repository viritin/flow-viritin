package org.vaadin.firitin.fields;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.combobox.ComboBox;
import java.util.Arrays;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentFocusable;
import org.vaadin.firitin.fluency.ui.FluentHasSize;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;
import org.vaadin.firitin.fluency.ui.FluentHasValidation;
import org.vaadin.firitin.fluency.ui.FluentHasValueAndElement;
import org.vaadin.firitin.fluency.ui.internal.FluentHasAutofocus;
import org.vaadin.firitin.fluency.ui.internal.FluentHasLabel;

public class EnumSelect<T> extends ComboBox<T> implements FluentHasSize<EnumSelect<T>>,
    FluentHasValidation<EnumSelect<T>>, FluentFocusable<ComboBox<T>, EnumSelect<T>>,
    FluentComponent<EnumSelect<T>>, FluentHasLabel<EnumSelect<T>>, FluentHasAutofocus<EnumSelect<T>>,
    FluentHasStyle<EnumSelect<T>>, FluentHasValueAndElement<EnumSelect<T>, AbstractField.ComponentValueChangeEvent<ComboBox<T>, T>, T> {

  public EnumSelect(Class<T> enumClass) {
    this(null, enumClass);
  }

  public EnumSelect(String caption, Class<T> enumClass) {
    super(caption, Arrays.asList(enumClass.getEnumConstants()));
  }

}
