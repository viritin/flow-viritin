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
 * <p>The interface expects the two to agree — "override {@link #getEmptyValue()} if
 * the empty value is not null" — so the tidy answer would be to define the empty
 * value of a form as the object built from every editor's own empty value. For a
 * record that even works for free, since records compare by component. The two tests
 * below are why it is not that simple:
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
 * <p>Which leaves the definition that needs neither construction nor equality:
 * <b>empty = every bound editor is empty</b>, asked of the editors, where "empty" is
 * already defined properly for each of them. That would make {@code isEmpty()} true
 * after {@code clear()} and change nothing else — at the price of no longer being
 * the comparison the interface describes, and of being about the editors rather than
 * about the parts of the value nothing edits.</p>
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
