package org.vaadin.firitin.formbinder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Consumer;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;

import org.junit.jupiter.api.Test;

/**
 * The plain binder demo, checked for the things it demonstrates.
 *
 * <p>Two of them were only ever visible to a reader of the screen: that a
 * constraint reaches the field that can enforce it, and that a constraint belonging
 * to a validation group does not reach anything until that group is active — which
 * the view states in a comment beside the field and nothing checked.
 *
 * <p>Browserless, in the style of {@code LocalizedFieldTest}: a real view in a real
 * routing context, no browser and no frontend build.
 */
class FormBinderViewTest {

    private static final String VIEW_PACKAGE = "org.vaadin.firitin.formbinder";

    private static void inView(Consumer<BrowserlessUIContext> body) {
        try (BrowserlessApplicationContext app =
                     BrowserlessApplicationContext.create(VIEW_PACKAGE)) {
            BrowserlessUIContext ui = app.newUser().newWindow();
            ui.navigate(FormBinderView.class);
            body.accept(ui);
        }
    }

    /**
     * {@code @NotNull @Min(0) Integer small} on the bean, and nothing about limits in
     * the form: the field carries both because the binder passed them on.
     */
    @Test
    void aConstraintReachesTheFieldThatCanEnforceIt() {
        inView(ui -> {
            var small = ui.findIntegerField().withLabel("Small number").component();

            assertEquals(0, small.getMin(), "@Min(0) belongs on the field");
            assertTrue(small.isRequiredIndicatorVisible(), "@NotNull too, as it always did");
        });
    }

    /**
     * The other half of the same rule, and the one the view already documents:
     * {@code text} is {@code @NotEmpty(groups = ExtraConstraints.class)}, so it is
     * not required until that group is asked for. A limit cannot be un-set per
     * group, which is why constraints with groups are left to the server side
     * entirely.
     */
    @Test
    void aConstraintBelongingToAGroupReachesNothingUntilTheGroupIsActive() {
        inView(ui -> {
            assertTrue(ui.findTextField().withLabel("Name").component().isRequiredIndicatorVisible(),
                    "@NotEmpty, in the default group");
            assertFalse(ui.findTextField().withLabel("Text").component().isRequiredIndicatorVisible(),
                    "@NotEmpty(groups = ExtraConstraints.class), which is not active here");
        });
    }
}
