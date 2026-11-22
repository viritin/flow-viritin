package org.vaadin.firitin.appframework.menuaccess;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;
import org.junit.jupiter.api.Test;

/**
 * checkAccess applies to views in a group too, not only to top-level ones: a view
 * the user may not open must not be listed in a group (an admin view under
 * "Settings"), and a group without a view of its own is shown only when the user
 * may open one of its views.
 */
class MenuAccessTest {

    @Test
    void deniedViewsAreLeftOutOfGroupsAndAGroupWithNoneLeftIsHidden() {
        try (BrowserlessApplicationContext app = BrowserlessApplicationContext.create(
                MenuAccessTest.class.getPackageName())) {
            BrowserlessUIContext ui = app.newUser().newWindow();
            ui.navigate(Views.Home.class);
            MenuAccessLayout layout = ui.find(MenuAccessLayout.class).first();

            assertEquals(java.util.List.of("Home", "Settings", "Settings > Profile"),
                    layout.shownMenu());
        }
    }
}
