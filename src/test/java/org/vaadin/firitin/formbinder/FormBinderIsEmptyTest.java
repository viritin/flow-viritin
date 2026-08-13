package org.vaadin.firitin.formbinder;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vaadin.firitin.form.FormBinder;

/**
 * What {@code isEmpty()} answers for a binder, and why it is not what the interface
 * describes.
 *
 * <p>{@link HasValue} defines it as the value being equal to the empty value:
 *
 * <pre>
 * default V getEmptyValue() { return null; }
 * default boolean isEmpty()  { return Objects.equals(getValue(), getEmptyValue()); }
 * </pre>
 *
 * <p>Following that would mean defining the empty value of a form as the object
 * built from every editor's own empty value. Two of the tests below are why it is
 * not that:
 *
 * <ul>
 * <li>that object <b>cannot always be built</b>. A record with a primitive component
 * has no empty form to construct — the editor can be empty, {@code int} cannot. So
 * {@code getEmptyValue()} would throw, and with it {@code clear()}, which the
 * interface defines as {@code setValue(getEmptyValue())}.
 * <li>for a mutable bean it <b>would not answer anything</b>. The empty value would
 * be a bean built for the comparison, and a bean is equal to nothing but itself
 * unless someone wrote it an equals — most are written with none.
 * </ul>
 *
 * <p>So the question goes to the editors instead, where "empty" already means
 * something exact for each of them. {@code getEmptyValue()} stays null, and the pair
 * is knowingly not the comparison the interface describes.
 *
 * <p>The cost is in the last test: this is about the form, not about the value. A
 * component nothing edits — an identifier carried over from the object that was set
 * — does not make the form non-empty.
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

    /** A record with a primitive component: the editor can be empty, {@code int} cannot. */
    public record Edit(String comment, int count) {
    }

    public static class EditForm extends VerticalLayout {
        TextField comment = new TextField();
        IntegerField count = new IntegerField();

        public EditForm() {
            add(comment, count);
        }
    }

    /** A bean with the equality most application beans have, which is none. */
    public static class Bean {
        private String comment;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    public static class BeanForm extends VerticalLayout {
        TextField comment = new TextField();

        public BeanForm() {
            add(comment);
        }
    }

    @Test
    public void theEmptyValueCannotAlwaysBeBuilt() {
        FormBinder<Edit> binder = new FormBinder<>(Edit.class, new EditForm());

        // Every editor is empty, and the record still cannot be constructed from them.
        Assertions.assertThrows(NullPointerException.class, binder::getValue,
                "a primitive component has no empty value to build");
    }

    @Test
    public void equalityWouldNotAnswerForAMutableBean() {
        FormBinder<Bean> binder = new FormBinder<>(Bean.class, new BeanForm());

        // What getEmptyValue() would have to hand over: a bean built for the comparison.
        Bean anEmptyOne = new Bean();

        Assertions.assertNotEquals(anEmptyOne, binder.getValue(),
                "same content, and still not equal: the bean has no equals of its own");
    }

    @Test
    public void isEmptyAsksTheEditors() {
        Form form = new Form();
        FormBinder<CommentAndTarget> binder = new FormBinder<>(CommentAndTarget.class, form);

        Assertions.assertNull(binder.getEmptyValue(),
                "the empty value stays null: there is no object to offer as one");

        // Nothing typed anywhere, even though a value can be constructed from it.
        Assertions.assertTrue(binder.isEmpty());
        Assertions.assertEquals(new CommentAndTarget("", null), binder.getValue());

        binder.setValue(new CommentAndTarget("hirvi", 40.0));
        Assertions.assertFalse(binder.isEmpty());

        binder.clear();
        Assertions.assertTrue(binder.isEmpty(), "which is what clearing a form means");
    }

    /** One empty editor is not enough; the form is empty when they all are. */
    @Test
    public void oneFilledEditorIsEnoughToMakeItNotEmpty() {
        Form form = new Form();
        FormBinder<CommentAndTarget> binder = new FormBinder<>(CommentAndTarget.class, form);

        form.target.setValue(40.0);

        Assertions.assertFalse(binder.isEmpty());
    }

    /**
     * The cost of asking the editors: a component nothing edits is not part of the
     * question. The form is empty because nothing has been typed into it, while the
     * value it would hand over still carries the identifier it was given.
     */
    @Test
    public void aComponentNothingEditsDoesNotCount() {
        BeanForm form = new BeanForm();
        FormBinder<WithIdentifier> binder = new FormBinder<>(WithIdentifier.class, form);
        binder.setValue(new WithIdentifier(7, ""));

        Assertions.assertTrue(binder.isEmpty(), "nothing has been typed into the form");
        Assertions.assertEquals(7, binder.getValue().id(), "and the value still knows its id");
    }

    public record WithIdentifier(long id, String comment) {
    }
}
