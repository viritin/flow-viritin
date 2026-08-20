package org.vaadin.firitin.appframework;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import in.virit.mopo.Mopo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Verifies that routes registered at runtime via
 * {@link com.vaadin.flow.router.RouteConfiguration#forSessionScope()} are
 * picked up by {@link MainLayout} and added to the side navigation menu.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DynamicRouteRegistrationIT {

    @Value("${local.server.port}")
    private int port;

    static Playwright playwright = Playwright.create();

    private Browser browser;
    private Page page;
    private Mopo mopo;

    @BeforeEach
    public void setup() {
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions());
        page = browser.newPage();
        // Generous timeout so the first test survives Vaadin's cold dev-mode
        // frontend bundle compilation in CI, where target/frontend is not cached.
        page.setDefaultTimeout(60_000);
        mopo = new Mopo(page);
    }

    @AfterEach
    public void closePlaywright() {
        page.close();
        browser.close();
    }

    @Test
    public void dynamicallyRegisteredViewAppearsInMenu() {
        page.navigate("http://localhost:" + port + "/my-app/dynamicregistration");
        try {
            mopo.waitForConnectionToSettle();
        } catch (Exception e) {
            System.out.println("Loading initial view didn't work, seems to be flaky for some reason in CI, this should be investigated. Trying again once...");
            //- waiting for locator("vaadin-connection-indicator[loading]")
            //9 × locator resolved to 1 element
            //        - unexpected value "1"
            page.reload(new Page.ReloadOptions().setTimeout(10_000));
            page.navigate("http://localhost:" + port + "/my-app/dynamicregistration");
            mopo.waitForConnectionToSettle();
        }

        Locator dynamicMenuItem = page.locator("vaadin-side-nav-item[path='my-app/dynamic']");
        assertThat(dynamicMenuItem).hasCount(0);

        registerButton().click();
        mopo.waitForConnectionToSettle();
        assertThat(dynamicMenuItem).hasCount(1);

        unregisterButton().click();
        mopo.waitForConnectionToSettle();
        assertThat(dynamicMenuItem).hasCount(0);
    }

    @Test
    public void registeredViewIsNavigable() {
        page.navigate("http://localhost:" + port + "/my-app/dynamicregistration");
        mopo.waitForConnectionToSettle();

        registerButton().click();
        mopo.waitForConnectionToSettle();

        page.locator("vaadin-side-nav-item[path='my-app/dynamic']").click();
        mopo.waitForConnectionToSettle();

        assertThat(page.locator("text=Dynamically registered view content.")).isVisible();
    }

    private Locator registerButton() {
        return page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Register DynamicView").setExact(true));
    }

    private Locator unregisterButton() {
        return page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Unregister DynamicView").setExact(true));
    }

}
