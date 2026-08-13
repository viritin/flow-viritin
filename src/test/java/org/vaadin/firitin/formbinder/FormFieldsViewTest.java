package org.vaadin.firitin.formbinder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Set;
import java.util.function.Consumer;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;

import org.junit.jupiter.api.Test;
import org.vaadin.firitin.fields.CommaSeparatedStringField;
import org.vaadin.firitin.fields.DurationField;
import org.vaadin.firitin.fields.ElementCollectionField;
import org.vaadin.firitin.fields.EnumSelect;

/**
 * The generic fields, bound to a record by a form.
 *
 * <p>These tests stay on the binding rather than on the fields themselves: that the
 * record reaches every one of them and comes back, which is what makes the view a
 * demonstration of the binder rather than of any one field. How each field behaves
 * inside is its own business, and mostly a matter for a browser.
 */
class FormFieldsViewTest {

    private static final String VIEW_PACKAGE = "org.vaadin.firitin.formbinder";

    private static void inView(Consumer<BrowserlessUIContext> body) {
        try (BrowserlessApplicationContext app =
                     BrowserlessApplicationContext.create(VIEW_PACKAGE)) {
            BrowserlessUIContext ui = app.newUser().newWindow();
            ui.navigate(FormFieldsView.class);
            body.accept(ui);
        }
    }

    @Test
    void everyComponentOfTheRecordReachesItsField() {
        inView(ui -> {
            assertEquals(FormFieldsView.MyEnum.ONE,
                    ui.find(EnumSelect.class).first().getValue());
            assertEquals(Duration.ofHours(1).plusSeconds(30),
                    ui.find(DurationField.class).first().getValue());
            assertEquals(Set.of("one", "two", "three"),
                    ui.find(CommaSeparatedStringField.class).first().getValue());
            ElementCollectionField<?> children = ui.find(ElementCollectionField.class).first();
            assertTrue(((java.util.List<?>) children.getValue()).isEmpty(),
                    "the record was given an empty list");
        });
    }

    @Test
    void thereIsNothingToSaveBeforeAnythingChanges() {
        inView(ui -> assertFalse(ui.findButton().withText("Save").component().isEnabled()));
    }

    /**
     * Through the enum, which is the one field here that a browserless test can
     * drive the way a reader would.
     */
    @Test
    void aChangeMakesItSaveableAndIsHandedOver() {
        inView(ui -> {
            // As the reader picks it: by the label shown in the list.
            EnumSelect<FormFieldsView.MyEnum> myEnum = ui.find(EnumSelect.class).first();
            ui.use(myEnum).selectItem("THREE");

            assertTrue(ui.findButton().withText("Save").component().isEnabled());

            ui.findButton().withText("Save").click();

            String shown = ui.findNotification().getText();
            assertTrue(shown.startsWith("Saved:"), shown);
            assertTrue(shown.contains("THREE"), "with the change in it: " + shown);
            assertTrue(shown.contains("PT1H30S"), "and the parts nobody touched: " + shown);
        });
    }
}
