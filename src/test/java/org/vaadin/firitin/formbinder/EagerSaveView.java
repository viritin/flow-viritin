package org.vaadin.firitin.formbinder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.form.BeanValidationForm;

/**
 * Forms that save as the reader types, without a save button of their own.
 *
 * <p>Taken from a real application: a list of running counters, each a row in a
 * settings popover that already had one save button. A second one per row would
 * have been a puzzle, so the rows save on change — but only a change that leaves
 * the row usable, which is the part that is easy to get wrong by hand.
 *
 * <p>{@code setEagerSavedHandler} is the whole of it. The record's {@code id} has
 * no field and needs none; it comes back with the value, so the store knows which
 * row it was given.
 */
@Route
public class EagerSaveView extends VerticalLayout {

    public record Counter(long id,
                          @Size(max = 8) String comment,
                          @NotNull @Positive Double target) {
    }

    private final List<Counter> stored = new ArrayList<>(List.of(
            new Counter(1, "hirvi", 40.0),
            new Counter(2, "kauris", 30.0)));

    private final Pre storedDisplay = new Pre();

    public EagerSaveView() {
        add(new RichText().withMarkDown("""
                # Saving without a save button

                Each row saves as you type, and only when what you typed leaves it
                valid. A comment over eight characters, or a target that is not a
                positive number, is shown on the field and **not** stored — the store
                below keeps what it had.
                """));

        stored.forEach(counter -> add(new CounterRow(counter, this::save)));
        add(storedDisplay);
        showStored();
    }

    private void save(Counter counter) {
        stored.replaceAll(existing -> existing.id() == counter.id() ? counter : existing);
        showStored();
    }

    private void showStored() {
        storedDisplay.setText(stored.stream().map(Counter::toString)
                .reduce((a, b) -> a + "\n" + b).orElse(""));
    }

    /** One row. No toolbar, no save button, no listener of its own. */
    public static class CounterRow extends BeanValidationForm<Counter> {

        TextField comment = new TextField();
        NumberField target = new NumberField();

        public CounterRow(Counter counter, Consumer<Counter> onSave) {
            super(Counter.class);
            comment.setPlaceholder("What is hanging");
            target.setSuffixComponent(new com.vaadin.flow.component.html.Span("°Cd"));
            setEagerSavedHandler(onSave::accept);
            setEntity(counter);
        }

        @Override
        protected Component createContent() {
            HorizontalLayout row = new HorizontalLayout(comment, target);
            row.setPadding(false);
            return row;
        }
    }
}
