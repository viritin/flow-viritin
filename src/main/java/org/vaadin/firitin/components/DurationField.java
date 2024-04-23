package org.vaadin.firitin.components;

import com.vaadin.flow.component.HasPlaceholder;
import com.vaadin.flow.component.customfield.CustomField;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.vaadin.firitin.components.textfield.VTextField;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * A field for entering a duration of JDK type {@link Duration}.
 * <p>
 *     The field supports inputting the duration the following formats:
 * </p>
 * <ul>
 *     <li>hh:mm:ss</li>
 *     <li>hhmmss</li>
 *     <li>hh:mm</li>
 *     <li>hhmm</li>
 *     <li>hh</li>
 *     <li>ISO-8601 format: PnDTnHnMn.nS</li>
 * </ul>
 */
public class DurationField extends CustomField<Duration> implements HasPlaceholder {
    VTextField durationInput = new VTextField()
            .withPlaceholder("hh:mm:ss");

    /**
     * Creates a new field with empty value (null).
     */
    public DurationField() {
        this(null);
    }

    /**
     * Creates a new field with a given label.
     *
     * @param label the text to set as the label
     */
    public DurationField(String label) {
        setLabel(label);
        durationInput.setWidth("8em");
        durationInput.addBlurListener(e -> {
            // Normalize formatting on blur
            String str = durationInput.getValue();
            if (!durationInput.isInvalid()) {
                Duration duration = parseInput(durationInput.getValue());
                durationInput.setValue(formatToPresentation(duration));
            }
        });
        add(durationInput);
        setTooltipText("""
            Default formatting for duration: hh:mm:ss.
            For quick input, if no separator typed in, until 100, interpreted an hour.
            Then until 10000, hhmm. Also supports ISO-8601 style format for "human compilers": PnDTnHnMn.nS
        """);
    }

    protected String formatToPresentation(Duration d) {
        return defaultFormatToPresentation(d);
    }

    public static final String defaultFormatToPresentation(Duration d) {
        if (d == null) {
            return "";
        }
        long secs = d.get(ChronoUnit.SECONDS);
        boolean hasSecs = (secs % 60) != 0;
        if (hasSecs) {
            return DurationFormatUtils.formatDuration(secs * 1000, "H:mm:ss", true);
        } else {
            return DurationFormatUtils.formatDuration(secs * 1000, "H:mm", true);
        }
    }

    protected Duration parseInput(String value) {
        return parseInputDefault(value);
    }

    static final Duration parseInputDefault(String value) {
        try {
            // First try with ISO-8601: PnDTnHnMn.nS
            return Duration.parse(value);
        } catch (DateTimeParseException e) {
            if (value.contains(":")) {
                value = value.replaceFirst(":", "H");
                if (value.contains(":")) {
                    value = value.replaceFirst(":", "M") + "S";
                } else {
                    value += "M";
                }
                value = "PT" + value;
                try {
                    return Duration.parse(value);
                } catch (DateTimeParseException exception) {
                    return null;
                }
            } else {
                boolean numbersOnly = value.matches("[0-9]+");
                if (numbersOnly) {
                    try {
                        if (value.length() < 3) {
                            int hours = Integer.parseInt(value);
                            return Duration.ofHours(hours);
                        } else {
                            int firstMinute = value.length() - 2;
                            int minutes = Integer.parseInt(value.substring(firstMinute));
                            int hours = Integer.parseInt(value.substring(0, firstMinute));
                            return Duration.ofMinutes(minutes + hours * 60);
                        }
                    } catch (NumberFormatException numberFormatException) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected Duration generateModelValue() {
        String value = durationInput.getValue();
        Duration d = parseInput(value);
        durationInput.setInvalid(d == null && !value.isEmpty());
        return d;
    }

    @Override
    protected void setPresentationValue(Duration newPresentationValue) {
        String presentation = formatToPresentation(newPresentationValue);
        durationInput.setValue(presentation);
    }

    @Override
    public void setInvalid(boolean invalid) {
        super.setInvalid(invalid);
        // proxy also to the underlaying TextField (red background if invalid)
        durationInput.setInvalid(invalid);
    }

    // HasPlaceholder implementation, proxy to underlying TextField
    public void setPlaceholder(String placeholder) {
        durationInput.setPlaceholder(placeholder);
    }

    @Override
    public String getPlaceholder() {
        return durationInput.getPlaceholder();
    }
}
