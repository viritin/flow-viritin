package org.vaadin.firitin.formbinder;

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
 * Two forms embedded in one page, neither of them being the page.
 *
 * <p>Taken from a real application: a sensor's settings panel holds a bound form
 * for the settings themselves and, under it, a second small form that starts a new
 * counter. Three things had to be true for that to work, and each has a name now:
 *
 * <ul>
 * <li>{@link BeanValidationForm#asSection() asSection()} — the form takes the width
 * it is given and only the height it needs, instead of the full-size default that
 * suits a form with a view to itself.
 * <li>{@code createContent()} is overridden and {@code getFormComponents()} simply
 * left alone: it is not abstract any more, so a form that lays itself out no longer
 * declares an empty list to say so.
 * <li>{@link BeanValidationForm#setSaveOnEnter(boolean) setSaveOnEnter(false)} on
 * the second form — with two save buttons on one page, ENTER belongs to the one
 * that is the page's verdict, and one keypress must not perform two saves.
 * </ul>
 */
@Route
public class EmbeddedFormsView extends VerticalLayout {

    public record Settings(@Size(max = 20) String name, boolean alerts) {
    }

    public record NewCounter(@Size(max = 8) String comment,
                             @NotNull @Positive Double target) {
    }

    private final Pre storedDisplay = new Pre();

    public EmbeddedFormsView() {
        add(new RichText().withMarkDown("""
                # Forms as parts of a page

                The settings form below saves on ENTER, as a lone form should.
                The counter starter under it does **not** — it said
                `setSaveOnEnter(false)`, so the one keypress cannot perform two
                saves. Both forms are sections of this page rather than the page:
                `asSection()` is why this text and the log at the bottom are not
                pushed off the screen.
                """));

        add(new SettingsForm(new Settings("Sauna", true), this::show));
        add(new StartCounterForm(this::show));
        add(storedDisplay);
    }

    private void show(Object saved) {
        storedDisplay.setText(storedDisplay.getText() + saved + "\n");
    }

    /** The page's main form: the default save button, and with it ENTER. */
    public static class SettingsForm extends BeanValidationForm<Settings> {

        TextField name = new TextField("Name");

        public SettingsForm(Settings settings, Consumer<Settings> onSave) {
            super(Settings.class);
            asSection();
            setSavedHandler(onSave::accept);
            setEntityWithEnabledSave(settings);
        }

        /*
           Laid out by hand — and that is the whole of it. No empty
           getFormComponents() under this method any more: a form that builds its
           own content only has to say what the content is.
        */
        @Override
        protected Component createContent() {
            return new VerticalLayout(name, getSaveButton());
        }
    }

    /** The second form on the page, which therefore keeps its hands off ENTER. */
    public static class StartCounterForm extends BeanValidationForm<NewCounter> {

        TextField comment = new TextField();
        NumberField target = new NumberField();

        public StartCounterForm(Consumer<NewCounter> onStart) {
            super(NewCounter.class);
            asSection();
            setSaveOnEnter(false);
            setSaveCaption("Start");
            comment.setPlaceholder("What is hanging");
            setSavedHandler(started -> {
                onStart.accept(started);
                setEntityWithEnabledSave(new NewCounter(null, 40.0));
            });
            setEntityWithEnabledSave(new NewCounter(null, 40.0));
        }

        @Override
        protected Component createContent() {
            HorizontalLayout row = new HorizontalLayout(comment, target, getSaveButton());
            row.setPadding(false);
            return row;
        }
    }
}
