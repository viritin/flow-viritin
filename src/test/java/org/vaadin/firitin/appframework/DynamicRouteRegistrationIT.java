package org.vaadin.firitin.appframework;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.firitin.VaadinPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Verifies that routes registered at runtime via
 * {@link com.vaadin.flow.router.RouteConfiguration#forSessionScope()} are
 * picked up by {@link MainLayout} and added to the side navigation menu.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DynamicRouteRegistrationIT {

    @RegisterExtension
    final VaadinPage vaadin = new VaadinPage(DynamicRegistrationView.class);

    private Page page() {
        return vaadin.page();
    }

    @Test
    public void dynamicallyRegisteredViewAppearsInMenu() {
        Locator dynamicMenuItem = page().locator("vaadin-side-nav-item[path='my-app/dynamic']");
        assertThat(dynamicMenuItem).hasCount(0);

        ButtonElement.getByText(page(), "Register DynamicView").click();
        assertThat(dynamicMenuItem).hasCount(1);

        ButtonElement.getByText(page(), "Unregister DynamicView").click();
        assertThat(dynamicMenuItem).hasCount(0);
    }

    @Test
    public void registeredViewIsNavigable() {
        ButtonElement.getByText(page(), "Register DynamicView").click();

        page().locator("vaadin-side-nav-item[path='my-app/dynamic']").click();

        assertThat(page().locator("text=Dynamically registered view content.")).isVisible();
    }
}
