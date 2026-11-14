package org.vaadin.firitin.components.internal;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.io.Serializable;
import java.util.Objects;

/**
 * Internal helper that brings {@link ValueChangeMode} support to date and time
 * pickers (vaadin-date-picker, vaadin-time-picker and vaadin-date-time-picker
 * that contains both).
 * <p>
 * The web components only commit their value when the user presses enter or
 * leaves the field. With e.g. {@link ValueChangeMode#LAZY}, this helper commits
 * the typed text while the user is still typing, but only if the text is
 * "complete": it parses with the current i18n settings and formats back to
 * exactly the same text. Thus partial input, like "1.2.20" while typing a year,
 * is never re-formatted under the user's cursor, but committed on blur/enter as
 * before. Clearing the text commits an empty value.
 * <p>
 * {@link ValueChangeMode#ON_CHANGE} and {@link ValueChangeMode#ON_BLUR} keep the
 * default behaviour of the web components.
 */
public class PickerValueChangeModeSupport implements Serializable {

    private static final String JS = """
            const host = this;
            host.__viritinValueChangeTimeout = $0;
            if (host.__viritinValueChangeMode) {
                return;
            }
            host.__viritinValueChangeMode = true;
            const pad = (n, len = 2) => String(n).padStart(len, '0');
            const commit = (picker) => {
                const text = picker.inputElement ? picker.inputElement.value : '';
                const i18n = picker.__effectiveI18n || picker.i18n || {};
                let value = '';
                if (text) {
                    if (picker.localName === 'vaadin-date-picker') {
                        const d = i18n.parseDate && i18n.parseDate(text);
                        if (!d || !i18n.formatDate || i18n.formatDate(d) !== text) {
                            return;
                        }
                        value = pad(d.year, 4) + '-' + pad(d.month + 1) + '-' + pad(d.day);
                    } else {
                        const t = i18n.parseTime && i18n.parseTime(text);
                        if (!t || !i18n.formatTime || i18n.formatTime(t) !== text) {
                            return;
                        }
                        value = pad(t.hours) + ':' + pad(t.minutes || 0);
                        if (t.seconds || t.milliseconds) {
                            value += ':' + pad(t.seconds || 0);
                            if (t.milliseconds) {
                                value += '.' + pad(t.milliseconds, 3);
                            }
                        }
                    }
                }
                if ((picker.value || '') === value) {
                    return;
                }
                picker.value = value;
                // Same event the web component fires when the user commits the value
                // (enter/blur): syncs the input text to the server and lets the
                // vaadin-date-time-picker update its value.
                picker.dispatchEvent(new CustomEvent('change', {bubbles: true}));
            };
            const timers = new WeakMap();
            host.addEventListener('input', e => {
                const picker = e.target.closest && e.target.closest('vaadin-date-picker, vaadin-time-picker');
                const timeout = host.__viritinValueChangeTimeout;
                if (!picker || timeout < 0) {
                    return;
                }
                clearTimeout(timers.get(picker));
                timers.set(picker, setTimeout(() => commit(picker), timeout));
            });
            """;

    private final Component component;
    private ValueChangeMode valueChangeMode = ValueChangeMode.LAZY;
    private int valueChangeTimeout = HasValueChangeMode.DEFAULT_CHANGE_TIMEOUT;

    public PickerValueChangeModeSupport(Component component) {
        this.component = component;
        component.addAttachListener(e -> update());
    }

    public ValueChangeMode getValueChangeMode() {
        return valueChangeMode;
    }

    public void setValueChangeMode(ValueChangeMode valueChangeMode) {
        this.valueChangeMode = Objects.requireNonNull(valueChangeMode);
        update();
    }

    public int getValueChangeTimeout() {
        return valueChangeTimeout;
    }

    public void setValueChangeTimeout(int valueChangeTimeout) {
        if (valueChangeTimeout < 0) {
            throw new IllegalArgumentException("Timeout must not be negative");
        }
        this.valueChangeTimeout = valueChangeTimeout;
        update();
    }

    private int clientTimeout() {
        return switch (valueChangeMode) {
            case EAGER -> 0;
            case LAZY, TIMEOUT -> valueChangeTimeout;
            case ON_CHANGE, ON_BLUR -> -1;
        };
    }

    private void update() {
        if (component.isAttached()) {
            component.getElement().executeJs(JS, clientTimeout());
        }
    }
}
