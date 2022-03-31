package org.vaadin.firitin.components.datetimepicker;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.server.VaadinSession;
import org.vaadin.firitin.fluency.ui.*;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class VDateTimePicker extends DateTimePicker implements FluentHasSize<VDateTimePicker>, FluentHasValidation<VDateTimePicker>,
        FluentHasStyle<VDateTimePicker>, FluentFocusable<DateTimePicker, VDateTimePicker>, FluentComponent<VDateTimePicker>,
        FluentThemableLayout<VDateTimePicker>, FluentHasValueAndElement<VDateTimePicker, AbstractField.ComponentValueChangeEvent<DateTimePicker, LocalDateTime>, LocalDateTime>,
        FluentHasHelper<VDateTimePicker> {

    public VDateTimePicker() {
        super();
        initI18n();
    }

    public VDateTimePicker(String label) {
        super(label);
        initI18n();
    }

    public VDateTimePicker(String label, LocalDateTime initialDateTime) {
        super(label, initialDateTime);
        initI18n();
    }

    public VDateTimePicker(LocalDateTime initialDateTime) {
        super(initialDateTime);
        initI18n();
    }

    public VDateTimePicker(ValueChangeListener<ComponentValueChangeEvent<DateTimePicker, LocalDateTime>> listener) {
        super(listener);
        initI18n();
    }

    public VDateTimePicker(String label, ValueChangeListener<ComponentValueChangeEvent<DateTimePicker, LocalDateTime>> listener) {
        super(label, listener);
        initI18n();
    }

    public VDateTimePicker(LocalDateTime initialDateTime, ValueChangeListener<ComponentValueChangeEvent<DateTimePicker, LocalDateTime>> listener) {
        super(initialDateTime, listener);
        initI18n();
    }

    public VDateTimePicker(String label, LocalDateTime initialDateTime, ValueChangeListener<ComponentValueChangeEvent<DateTimePicker, LocalDateTime>> listener) {
        super(label, initialDateTime, listener);
        initI18n();
    }

    public VDateTimePicker(LocalDateTime initialDateTime, Locale locale) {
        super(initialDateTime, locale);
    }

    @Override
    public void setLocale(Locale locale) {
        super.setLocale(locale);
        initI18n();
    }

    /**
     * Makes a "proper" initialization based on set locale.
     */
    protected void initI18n() {
        Locale l = getLocale();
        if(l != null) {
            DatePicker.DatePickerI18n i18n = createI18nFromLocale(l);
            setDatePickerI18n(i18n);
        }

    }

    private static HashMap<Locale, DatePicker.DatePickerI18n> i18nCache = new HashMap<>();

    public static synchronized DatePicker.DatePickerI18n createI18nFromLocale(Locale locale) {
        return i18nCache.computeIfAbsent(locale, l -> {
            DatePicker.DatePickerI18n i18n = new DatePicker.DatePickerI18n();

            i18n.setFirstDayOfWeek(WeekFields.of(locale).getFirstDayOfWeek().getValue() % 7);

            i18n.setMonthNames(Stream.of(Month.values()).map(m -> {
                        return m.getDisplayName(TextStyle.FULL_STANDALONE, locale);
                    })
                    .collect(toList()));

            List<String> weekdays = Stream.of(DayOfWeek.values())
                    .map(d -> d.getDisplayName(TextStyle.FULL_STANDALONE, locale)).collect(toList());
            Collections.rotate(weekdays, 1);
            i18n.setWeekdays(weekdays);

            List<String> weekdaysShort = Stream.of(DayOfWeek.values())
                    .map(d -> d.getDisplayName(TextStyle.SHORT_STANDALONE, locale)).collect(toList());
            Collections.rotate(weekdaysShort, 1);
            i18n.setWeekdaysShort(weekdaysShort);
            return i18n;
        });
    }

    public VDateTimePicker withDatePlaceholder(String placeholder) {
        setDatePlaceholder(placeholder);
        return this;
    }

    public VDateTimePicker withTimePlaceholder(String placeholder) {
        setTimePlaceholder(placeholder);
        return this;
    }

    public VDateTimePicker withStep(Duration step) {
        setStep(step);
        return this;
    }

    public VDateTimePicker withWeekNumbersVisible(boolean weekNumbersVisible) {
        setWeekNumbersVisible(weekNumbersVisible);
        return this;
    }

    public VDateTimePicker withLocale(Locale locale) {
        setLocale(locale);
        return this;
    }

    public VDateTimePicker withMin(LocalDateTime min) {
        setMin(min);
        return this;
    }

    public VDateTimePicker withMax(LocalDateTime max) {
        setMax(max);
        return this;
    }

    public VDateTimePicker withDatePickerI18n(DatePicker.DatePickerI18n i18n) {
        setDatePickerI18n(i18n);
        return this;
    }

    public VDateTimePicker withRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        setRequiredIndicatorVisible(requiredIndicatorVisible);
        return this;
    }

}
