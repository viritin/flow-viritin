package org.vaadin.firitin.formbinder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.BiConsumer;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;

import org.junit.jupiter.api.Test;

/**
 * The record demo: a record with per-property constraints and a class level one,
 * bound with a plain {@link org.vaadin.firitin.form.FormBinder} and validated from
 * a listener the view writes itself.
 *
 * <p>The value it opens with — {@code Person("Jorma", 70, 69)} — breaks the class
 * level rule on purpose, so the first change is enough to show it.
 */
class FormBinderWithRecordViewTest {

    private static final String VIEW_PACKAGE = "org.vaadin.firitin.formbinder";

    private static final String CROSS_FIELD = "Big should be bigger that the small!";

    private static void inView(BiConsumer<BrowserlessUIContext, FormBinderWithRecordView> body) {
        try (BrowserlessApplicationContext app =
                     BrowserlessApplicationContext.create(VIEW_PACKAGE)) {
            BrowserlessUIContext ui = app.newUser().newWindow();
            body.accept(ui, ui.navigate(FormBinderWithRecordView.class));
        }
    }

    @Test
    void theRecordIsShownInTheFields() {
        inView((ui, view) -> {
            assertEquals("Jorma", ui.findTextField().withLabel("Name").component().getValue());
            assertEquals(70, ui.findIntegerField().withLabel("Small number").component().getValue());
            assertEquals(69, ui.findIntegerField().withLabel("Big number").component().getValue());
        });
    }

    /**
     * The rule spans two fields, so it belongs to neither. It is reported at form
     * level — for a plain binder, in the component the fields live in.
     */
    @Test
    void theCrossFieldRuleIsReportedWhenItIsBroken() {
        inView((ui, view) -> {
            // Any change is enough to run the view's validation listener.
            ui.findIntegerField().withLabel("Big number").setValue(60);

            assertTrue(containsText(view, CROSS_FIELD),
                    "69 was already smaller than 70, and 60 is smaller still");
        });
    }

    @Test
    void andGoesAwayWhenTheValuesAreMadeToAgree() {
        inView((ui, view) -> {
            ui.findIntegerField().withLabel("Big number").setValue(60);
            ui.findIntegerField().withLabel("Big number").setValue(71);

            assertFalse(containsText(view, CROSS_FIELD));
        });
    }

    /**
     * The record carries an {@code isValid()} method that is not a property. The
     * comment beside it says it "should not break binding" — this is that sentence
     * as a test: the values still go in and come back out.
     */
    @Test
    void aMethodThatIsNotAPropertyDoesNotDisturbTheBinding() {
        inView((ui, view) -> {
            ui.findTextField().withLabel("Name").setValue("Kalle");
            ui.findIntegerField().withLabel("Small number").setValue(1);
            ui.findIntegerField().withLabel("Big number").setValue(2);

            ui.findButton().withText("Show value").click();

            // The notification is the view's own way of showing what the binder holds.
            String shown = ui.findNotification().getText();
            assertTrue(shown.contains("Kalle"), "what was typed comes back: " + shown);
            assertTrue(shown.contains("small=1") && shown.contains("big=2"),
                    "and so do the numbers, in the components they belong to: " + shown);
        });
    }

    private static boolean containsText(Component root, String text) {
        String own = root.getElement().getText();
        if (own != null && own.contains(text)) {
            return true;
        }
        return ComponentUtil.getAllChildren(root).anyMatch(child -> containsText(child, text));
    }
}
