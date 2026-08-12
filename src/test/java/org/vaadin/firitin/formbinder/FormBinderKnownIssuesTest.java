package org.vaadin.firitin.formbinder;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.vaadin.firitin.form.BeanValidationForm;
import org.vaadin.firitin.form.FormBinder;

/**
 * Cases found while building a real form with FormBinder and BeanValidationForm,
 * where the binder either fails in a way that does not name its cause, or leaves
 * work to the developer that it has all the information to do itself.
 *
 * <p>Every test here asserts the <b>desired</b> behaviour and is therefore
 * disabled: removing the {@code @Disabled} is what a fix looks like. The reason
 * on each says what happens today.
 *
 * <p>These are headless — no UI, no session — like the rest of
 * {@link FormBinderTest}.
 */
public class FormBinderKnownIssuesTest {

    // ------------------------------------------------------------------
    // Test domain
    // ------------------------------------------------------------------

    /**
     * The shape an editable row naturally has: an identifier that is part of the
     * value but has no business being on screen, and two fields that do.
     */
    public record CounterEdit(long id, @Size(max = 4) String comment,
                              @NotNull @Positive Double target) {
    }

    public record CommentAndTarget(@Size(max = 4) String comment,
                                   @NotNull @Positive Double target) {
    }

    public static class TwoFieldForm extends VerticalLayout {
        TextField comment = new TextField();
        NumberField target = new NumberField();

        public TwoFieldForm() {
            add(comment, target);
        }
    }

    public static class BaseForm extends VerticalLayout {
        TextField comment = new TextField();
    }

    /** A form that adds one field to a base class that already carries one. */
    public static class DerivedForm extends BaseForm {
        NumberField target = new NumberField();

        public DerivedForm() {
            add(comment, target);
        }
    }

    /** A BeanValidationForm that builds its own layout, as a non-trivial form does. */
    public static class CustomLayoutForm extends BeanValidationForm<CommentAndTarget> {
        TextField comment = new TextField();
        NumberField target = new NumberField();

        public CustomLayoutForm() {
            super(CommentAndTarget.class);
            setSavedHandler(value -> {
            });
        }

        @Override
        protected Component createContent() {
            // Deliberately without getClassLevelViolationsDisplay(): the point of
            // the test is that forgetting it is silent.
            return new VerticalLayout(comment, target, getSaveButton());
        }

        @Override
        protected List<Component> getFormComponents() {
            return List.of();
        }
    }

    // ------------------------------------------------------------------
    // 1. A null value
    // ------------------------------------------------------------------

    /**
     * {@code setValue(null)} is how a form is unbound, and
     * {@link BeanValidationForm#setEntity(null)} calls exactly this. Clearing the
     * editors is the obvious meaning; today the property loop asks Jackson for a
     * property of null.
     */
    @Test
    @Disabled("Today: NullPointerException from accessor.getValue(null) in setValue")
    public void aNullValueClearsTheEditors() {
        TwoFieldForm form = new TwoFieldForm();
        FormBinder<CommentAndTarget> binder = new FormBinder<>(CommentAndTarget.class, form);
        binder.setValue(new CommentAndTarget("hirvi", 40.0));

        Assertions.assertDoesNotThrow(() -> binder.setValue(null));

        Assertions.assertEquals("", form.comment.getValue());
        Assertions.assertNull(form.target.getValue());
    }

    /**
     * The same through the form API, which documents null as a value it takes:
     * "the currently edited entity or null if the form is currently unbound".
     */
    @Test
    @Disabled("Today: NullPointerException, via binder.setValue(null) in setEntity")
    public void anEntityCanBeSetToNullToUnbindTheForm() {
        CustomLayoutForm form = new CustomLayoutForm();
        form.setEntity(new CommentAndTarget("hirvi", 40.0));

        Assertions.assertDoesNotThrow(() -> form.setEntity(null));
        Assertions.assertFalse(form.isVisible());
    }

    // ------------------------------------------------------------------
    // 2. A record component with no editor
    // ------------------------------------------------------------------

    /**
     * A record is the natural DTO for an editable row, but only some of its
     * components are edited: an id, a creation time or a version has no field and
     * should not have one. The binder already holds the value that was set, so it
     * could carry those through instead of failing.
     *
     * <p>The alternative fix — refusing to bind such a type at all, with a message
     * naming the component — would at least be diagnosable. Today the developer
     * gets a NullPointerException from a line that mentions neither the record nor
     * the property, and the only way out is to split the record and carry the id
     * in a closure.
     */
    @Test
    @Disabled("Today: NullPointerException in constructRecord, 'hasValue is null'")
    public void aRecordComponentWithoutAnEditorKeepsTheValueItWasGiven() {
        TwoFieldForm form = new TwoFieldForm();
        FormBinder<CounterEdit> binder = new FormBinder<>(CounterEdit.class, form);
        binder.setValue(new CounterEdit(7, "hirvi", 40.0));

        form.comment.setValue("peura");

        CounterEdit edited = binder.getValue();
        Assertions.assertEquals("peura", edited.comment());
        Assertions.assertEquals(7, edited.id(), "the unedited component should survive");
    }

    // ------------------------------------------------------------------
    // 3. Fields declared in a superclass
    // ------------------------------------------------------------------

    /**
     * The binder reflects over {@code getClass().getDeclaredFields()}, so a field
     * inherited from a base form is invisible to it. Nothing says so: the property
     * is simply never bound, and for a record the failure surfaces later as the
     * NullPointerException above.
     */
    @Test
    @Disabled("Today: only [target] is bound; the inherited comment field is not seen")
    public void fieldsDeclaredInASuperclassAreBoundToo() {
        DerivedForm form = new DerivedForm();
        FormBinder<CommentAndTarget> binder = new FormBinder<>(CommentAndTarget.class, form);

        Assertions.assertEquals(List.of("comment", "target"),
                binder.getBoundProperties().stream().sorted().toList());
    }

    // ------------------------------------------------------------------
    // 4. Constraints that the field could enforce itself
    // ------------------------------------------------------------------

    /**
     * {@code @NotNull} already reaches the widget as a required indicator, and that
     * one mapping does real work: a required NumberField refuses to be emptied, so
     * the constraint is enforced by the browser rather than reported after the
     * fact.
     *
     * <p>The same is available for the rest of them. Today a developer writes
     * {@code comment.setMaxLength(64)} next to {@code @Size(max = 64)} and
     * {@code target.setMin(1)} next to {@code @Positive} — the binder has both
     * pieces and could do it.
     */
    @Test
    @Disabled("Today: only @NotNull is applied, as the required indicator")
    public void constraintsAreAppliedToTheFieldsThatCanEnforceThem() {
        TwoFieldForm form = new TwoFieldForm();
        new FormBinder<>(CommentAndTarget.class, form);

        // This part already works today.
        Assertions.assertTrue(form.target.isRequiredIndicatorVisible(),
                "@NotNull should make the field required");

        Assertions.assertEquals(4, form.comment.getMaxLength(), "@Size(max = 4)");
        Assertions.assertTrue(form.target.getMin() > 0, "@Positive");
    }

    // ------------------------------------------------------------------
    // 5. Save enabled for a value that was never validated
    // ------------------------------------------------------------------

    /**
     * Validation only runs from the binder's value change listener, and only for
     * changes that came from the client. An entity that is invalid to begin with is
     * therefore never judged, and {@code setEntityWithEnabledSave} — whose whole
     * purpose is "this one is already valid, allow saving without changes" — hands
     * the reader a Save button for a value the form would refuse a moment later.
     *
     * <p>Validating in {@code setEntity} would fix it without becoming noisy:
     * {@code ignoreRequiredConstraintForField} already keeps untouched required
     * fields from being marked, so only the button state would change.
     */
    @Test
    @Disabled("Today: save button is enabled and isValid() is true for an invalid entity")
    public void anInvalidEntityDoesNotEnableSaving() {
        CustomLayoutForm form = new CustomLayoutForm();

        // "liian pitka" is over @Size(max = 4)
        form.setEntityWithEnabledSave(new CommentAndTarget("liian pitka", 40.0));

        Assertions.assertFalse(form.getSaveButton().isEnabled(),
                "an entity that fails validation should not be offered for saving");
    }

    // ------------------------------------------------------------------
    // 6. Class level violations with nowhere to go
    // ------------------------------------------------------------------

    /**
     * Class level constraints are the reason to reach for this binder rather than
     * the core one, and they are reported into a component the form owns. Override
     * {@code createContent()} — which any form with a layout of its own does — and
     * forget to add that component, and every class level violation is rendered
     * into a detached Div. Nothing is logged and nothing appears.
     *
     * <p>Attaching it automatically when it has no parent, or saying so loudly,
     * would both do. The check is cheap: the display is the form's own field.
     */
    @Test
    @Disabled("Today: the message goes into a Div with no parent, and is never seen")
    public void aClassLevelViolationIsShownEvenIfTheLayoutForgotTheDisplay() {
        CustomLayoutForm form = new CustomLayoutForm();
        form.setEntity(new CommentAndTarget("ok", 40.0));

        form.getBinder().setRawConstraintViolations(Map.of("", "The values do not add up"));

        Assertions.assertTrue(containsText(form, "The values do not add up"),
                "a violation belonging to no field still has to be readable");
    }

    // ------------------------------------------------------------------
    // 7. A value change mode the developer chose
    // ------------------------------------------------------------------

    /**
     * {@code configureEditor} sets {@link ValueChangeMode#LAZY} on every editor
     * that has one. BeanValidationForm's own javadoc tells the developer to
     * "configure e.g. with setValueChangeMode" — and then the binder overwrites
     * whatever they configured.
     *
     * <p>EAGER is the one that matters in practice: a form that reacts as the
     * reader types is a deliberate choice, and it silently becomes lazy.
     */
    @Test
    @Disabled("Today: the mode set before binding is overwritten with LAZY")
    public void aValueChangeModeSetByTheDeveloperIsRespected() {
        TwoFieldForm form = new TwoFieldForm();
        form.comment.setValueChangeMode(ValueChangeMode.EAGER);

        new FormBinder<>(CommentAndTarget.class, form);

        Assertions.assertEquals(ValueChangeMode.EAGER, form.comment.getValueChangeMode());
    }

    // ------------------------------------------------------------------

    private static boolean containsText(Component root, String text) {
        if (root.getElement().getText() != null && root.getElement().getText().contains(text)) {
            return true;
        }
        return allChildren(root).anyMatch(child -> containsText(child, text));
    }

    private static Stream<Component> allChildren(Component component) {
        return ComponentUtil.getAllChildren(component);
    }
}
