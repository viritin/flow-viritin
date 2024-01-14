package org.vaadin.firitin.components.combobox;

import java.util.Locale;

/**
 * A select component for {@link java.util.Locale}.
 * <p>
 * The caption for each locale is the name of the locale in its own language
 * e.g. "Deutsch" for german and "Dansk" for danish.
 *
 * @author Daniel Nordhoff-Vergien
 */
public class LocaleSelect extends VComboBox<Locale> {
    public LocaleSelect() {
        setItemLabelGenerator(Locale::getDisplayName);
    }
}