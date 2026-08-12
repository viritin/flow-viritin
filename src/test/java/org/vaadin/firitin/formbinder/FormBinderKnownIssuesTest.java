package org.vaadin.firitin.formbinder;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vaadin.firitin.form.BeanValidationForm;
import org.vaadin.firitin.form.FormBinder;

/**
 * Cases found while building a real form with FormBinder and BeanValidationForm,
 * where the binder either fails in a way that does not name its cause, or leaves
 * work to the developer that it has all the information to do itself.
 *
 * <p>They were written as the list of what was wrong: each asserted the desired
 * behaviour and was disabled with what happened at the time, and removing that
 * annotation was what a fix looked like. The list is empty now, so they are simply
 * tests — of a null value, of inherited fields, of a record component nothing
 * edits, of where a class level violation goes, of what a form does with the entity
 * it is given, and of which constraints a field can enforce for itself.
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

    /** Bounds that a field can state exactly as the constraint does. */
    public record Bounded(@Min(1) @Max(9) Double amount,
                          @DecimalMin("1.5") Integer count) {
    }

    public static class BoundedForm extends VerticalLayout {
        NumberField amount = new NumberField();
        IntegerField count = new IntegerField();
    }

    /** A form that already limits a field more tightly than the annotation does. */
    public static class PreConfiguredForm extends VerticalLayout {
        TextField comment = new TextField();
        NumberField target = new NumberField();

        public PreConfiguredForm() {
            comment.setMaxLength(2);
        }
    }

    /** Not public on purpose: an application's own DTOs rarely are. */
    record HiddenEdit(long id, @Size(max = 4) String comment,
                      @NotNull @Positive Double target) {
    }

    /** The same shape as a mutable bean, which the binder writes into in place. */
    public static class CommentAndTargetBean {
        @Size(max = 4)
        private String comment;
        @NotNull
        @Positive
        private Double target;

        public CommentAndTargetBean() {
        }

        public CommentAndTargetBean(String comment, Double target) {
            this.comment = comment;
            this.target = target;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public Double getTarget() {
            return target;
        }

        public void setTarget(Double target) {
            this.target = target;
        }
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
     *
     * <p>Null is the binder's empty value — there is no other candidate for a
     * type it does not construct — so {@code clear()}, which {@code HasValue}
     * defines as {@code setValue(getEmptyValue())}, has to mean the same thing.
     * Each editor is cleared to <em>its own</em> empty value, which is why the
     * text field ends up at "" and the number field at null.
     */
    @Test
    public void aNullValueClearsTheEditors() {
        TwoFieldForm form = new TwoFieldForm();
        FormBinder<CommentAndTarget> binder = new FormBinder<>(CommentAndTarget.class, form);
        binder.setValue(new CommentAndTarget("hirvi", 40.0));

        Assertions.assertDoesNotThrow(() -> binder.setValue(null));

        Assertions.assertEquals("", form.comment.getValue());
        Assertions.assertNull(form.target.getValue());

        // The inherited shorthand takes the same route and must survive it.
        binder.setValue(new CommentAndTarget("hirvi", 40.0));
        Assertions.assertDoesNotThrow(binder::clear);
        Assertions.assertEquals("", form.comment.getValue());
    }

    /**
     * The same for a mutable bean, where clearing the editors fires value change
     * events that the binder writes back into the bean — except that there is no
     * longer a bean to write into. Observing server originated changes is not the
     * default, but it is offered, and it is what a test does.
     */
    @Test
    public void aMutableBeanFormCanBeEmptiedWhileServerChangesAreObserved() {
        TwoFieldForm form = new TwoFieldForm();
        FormBinder<CommentAndTargetBean> binder =
                new FormBinder<>(CommentAndTargetBean.class, form);
        binder.setIgnoreServerOriginatedChanges(false);
        binder.setValue(new CommentAndTargetBean("hirvi", 40.0));

        Assertions.assertDoesNotThrow(() -> binder.setValue(null));

        Assertions.assertEquals("", form.comment.getValue());
        // With nothing bound, the binder builds a new bean from the empty editors.
        Assertions.assertNotNull(binder.getValue());
    }

    /**
     * The same through the form API, which documents null as a value it takes:
     * "the currently edited entity or null if the form is currently unbound".
     */
    @Test
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
    public void aRecordComponentWithoutAnEditorKeepsTheValueItWasGiven() {
        TwoFieldForm form = new TwoFieldForm();
        FormBinder<CounterEdit> binder = new FormBinder<>(CounterEdit.class, form);
        binder.setValue(new CounterEdit(7, "hirvi", 40.0));

        form.comment.setValue("peura");

        CounterEdit edited = binder.getValue();
        Assertions.assertEquals("peura", edited.comment());
        Assertions.assertEquals(7, edited.id(), "the unedited component should survive");
    }

    /**
     * The same for a record that is not public, which is what a DTO nested in an
     * application's own form class usually is. Reading it takes the same access fix
     * that a bound property gets — found by running a real application against this,
     * where every such record is package private.
     */
    @Test
    public void anUnboundComponentOfAPackagePrivateRecordIsReadable() {
        TwoFieldForm form = new TwoFieldForm();
        FormBinder<HiddenEdit> binder = new FormBinder<>(HiddenEdit.class, form);
        binder.setValue(new HiddenEdit(7, "hirvi", 40.0));

        form.comment.setValue("peura");

        Assertions.assertEquals(7, binder.getValue().id());
    }

    /**
     * The case that cannot be carried over: no editor, nothing set to read from,
     * and a component that cannot be null. The value is genuinely unavailable, so
     * this has to fail — but it should say which component and why.
     */
    @Test
    public void anUnboundPrimitiveComponentWithNothingSetIsDiagnosable() {
        TwoFieldForm form = new TwoFieldForm();
        FormBinder<CounterEdit> binder = new FormBinder<>(CounterEdit.class, form);

        IllegalStateException failure =
                Assertions.assertThrows(IllegalStateException.class, binder::getValue);

        Assertions.assertTrue(failure.getMessage().contains("id"),
                "the component should be named: " + failure.getMessage());
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
    public void constraintsAreAppliedToTheFieldsThatCanEnforceThem() {
        TwoFieldForm form = new TwoFieldForm();
        new FormBinder<>(CommentAndTarget.class, form);

        // This part already worked.
        Assertions.assertTrue(form.target.isRequiredIndicatorVisible(),
                "@NotNull should make the field required");

        Assertions.assertEquals(4, form.comment.getMaxLength(), "@Size(max = 4)");
    }

    /*
       A field's minimum is inclusive and @Positive is not, and there is no next
       double after zero to use instead. A field that allowed zero while the
       constraint refused it would be a worse lie than no limit at all, so the
       server side keeps that one to itself.
    */
    @Test
    public void aStrictBoundIsNotHandedToTheField() {
        TwoFieldForm form = new TwoFieldForm();
        new FormBinder<>(CommentAndTarget.class, form);

        Assertions.assertFalse(Double.isFinite(form.target.getMin()),
                "@Positive is strict; an inclusive minimum cannot say the same thing");
    }

    @Test
    public void inclusiveBoundsReachTheField() {
        BoundedForm form = new BoundedForm();
        new FormBinder<>(Bounded.class, form);

        Assertions.assertEquals(1.0, form.amount.getMin(), "@Min(1)");
        Assertions.assertEquals(9.0, form.amount.getMax(), "@Max(9)");
        Assertions.assertEquals(2, form.count.getMin(), "@DecimalMin(\"1.5\") rounded towards valid");
    }

    /**
     * The developer knew something the annotation does not — a target that has to be
     * at least one degree-day, say, where the constraint only says "positive". The
     * limit they set stays, and the constraint is still checked where it always was.
     */
    @Test
    public void aLimitTheDeveloperSetIsKept() {
        PreConfiguredForm form = new PreConfiguredForm();
        new FormBinder<>(CommentAndTarget.class, form);

        Assertions.assertEquals(2, form.comment.getMaxLength());
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
    public void aValueChangeModeSetByTheDeveloperIsRespected() {
        TwoFieldForm form = new TwoFieldForm();
        form.comment.setValueChangeMode(ValueChangeMode.EAGER);

        new FormBinder<>(CommentAndTarget.class, form);

        Assertions.assertEquals(ValueChangeMode.EAGER, form.comment.getValueChangeMode());
        // The default is still lazy, which is what keeps validation off every keystroke.
        Assertions.assertEquals(ValueChangeMode.LAZY, form.target.getValueChangeMode(),
                "a field the developer did not configure should still become lazy");
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
