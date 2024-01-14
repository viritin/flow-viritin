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

/**
 * A ComboBox that is pre-filled with all the values of an enum.
 *
 * @param <T> the enum type
 */
public class EnumSelect<T> extends ComboBox<T> implements FluentHasSize<EnumSelect<T>>,
    FluentHasValidation<EnumSelect<T>>, FluentFocusable<ComboBox<T>, EnumSelect<T>>,
    FluentComponent<EnumSelect<T>>, FluentHasLabel<EnumSelect<T>>, FluentHasAutofocus<EnumSelect<T>>,
    FluentHasStyle<EnumSelect<T>>, FluentHasValueAndElement<EnumSelect<T>, AbstractField.ComponentValueChangeEvent<ComboBox<T>, T>, T> {

  /**
   * Creates a new instance of the field.
   *
   * @param enumClass the class of the enum
   */
  public EnumSelect(Class<T> enumClass) {
    this(null, enumClass);
  }

  /**
   * Creates a new instance of the field.
   *
   * @param caption the caption to use
   * @param enumClass the class of the enum
   */
  public EnumSelect(String caption, Class<T> enumClass) {
    super(caption, Arrays.asList(enumClass.getEnumConstants()));
  }

}
