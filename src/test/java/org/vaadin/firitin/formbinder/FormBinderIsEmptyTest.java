package org.vaadin.firitin.formbinder;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vaadin.firitin.form.FormBinder;

/**
 * What {@code isEmpty()} answers for a binder, pinned down so the question can be
 * looked at rather than reasoned about.
 *
 * <p>{@link HasValue} defines it, and neither half of the definition fits:
 *
 * <pre>
 * default V getEmptyValue() { return null; }
 * default boolean isEmpty()  { return Objects.equals(getValue(), getEmptyValue()); }
 * </pre>
 *
 * <p>{@link FormBinder#getValue()} never returns null — with nothing bound it
 * constructs a value from the editors, which is what makes a binder usable for
 * creating a new object as well as for editing one. So the comparison is always
 * against null, and <b>isEmpty() is false in every state</b>: before anything is
 * set, after a value is set, and after {@code clear()}. That is what the assertions
 * below record.
 *
 * <p>Two ways out, and they mean different things:
 *
 * <ul>
 * <li><b>empty = nothing bound.</b> {@code getValue()} would have to return null
 * when no value has been set, which would take the "build a new object from empty
 * fields" use away — {@code FormBinderTest.testRecordBasics} depends on it.
 * <li><b>empty = every editor is empty.</b> An override that asks the editors
 * instead of comparing values. {@code clear()} would then make it true, which is
 * what a reader of the interface would expect, and no existing behaviour changes.
 * </ul>
 *
 * <p>Neither is urgent: nothing in this library calls it, and a binder cannot be a
 * field of another form, so no Vaadin code reaches it either. It is an inconsistency
 * in what the class advertises rather than one anybody has run into — the
 * application this came from never called it.
 */
public class FormBinderIsEmptyTest {

    public record CommentAndTarget(String comment, Double target) {
    }

    public static class Form extends VerticalLayout {
        TextField comment = new TextField();
        NumberField target = new NumberField();

        public Form() {
            add(comment, target);
        }
    }

    @Test
    public void isEmptyIsFalseInEveryState() {
        Form form = new Form();
        FormBinder<CommentAndTarget> binder = new FormBinder<>(CommentAndTarget.class, form);

        Assertions.assertNull(binder.getEmptyValue(),
                "the empty value of a binder is null, as HasValue defaults it");

        // Nothing set: the editors are empty, but a value is constructed from them.
        Assertions.assertFalse(binder.isEmpty());
        Assertions.assertEquals(new CommentAndTarget("", null), binder.getValue());

        binder.setValue(new CommentAndTarget("hirvi", 40.0));
        Assertions.assertFalse(binder.isEmpty());

        binder.clear();
        Assertions.assertFalse(binder.isEmpty(),
                "even here, where a reader of the interface would expect true");
        Assertions.assertEquals(new CommentAndTarget("", null), binder.getValue(),
                "because clearing empties the editors, and getValue() reads the editors");
    }
}
