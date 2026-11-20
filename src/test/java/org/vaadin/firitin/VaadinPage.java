package org.vaadin.firitin;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.vaadin.addons.dramafinder.AutoWait;
import org.vaadin.addons.dramafinder.RouteNavigation;

/**
 * JUnit extension for the Playwright based integration tests: opens the given
 * view of the {@code @SpringBootTest} app in a fresh browser context before each
 * test, waits for Vaadin to settle and closes the context afterwards. Use with
 * DramaFinder's elements and {@link AutoWait}, without a base class:
 *
 * <pre>
 * &#64;SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
 * class MyViewIT {
 *     &#64;RegisterExtension
 *     final VaadinPage vaadin = new VaadinPage(MyView.class);
 *
 *     &#64;Test
 *     void saves() {
 *         ButtonElement.getByText(vaadin.page(), "Save").click();
 *     }
 * }
 * </pre>
 */
public class VaadinPage implements BeforeEachCallback, AfterEachCallback {

    // One browser for the whole test run, closed when the JVM exits
    private static Browser browser;

    private final Class<?> view;
    private Page page;

    public VaadinPage(Class<?> view) {
        this.view = view;
    }

    /**
     * @return the page of the currently running test
     */
    public Page page() {
        return page;
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        String port = SpringExtension.getApplicationContext(context).getEnvironment()
                .getProperty("local.server.port");
        BrowserContext browserContext = browser().newContext();
        // Generous timeout for opening the first view: in CI, Vaadin's dev mode
        // compiles the frontend bundle on the first request (target/frontend is
        // not cached).
        browserContext.setDefaultTimeout(120_000);
        page = browserContext.newPage();
        page.navigate("http://localhost:" + port + "/" + RouteNavigation.pathOf(view));
        AutoWait.waitForVaadinIdle(page);
        page.setDefaultTimeout(15_000);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        page.context().close();
        page = null;
    }

    private static synchronized Browser browser() {
        if (browser == null) {
            Playwright playwright = Playwright.create();
            browser = playwright.chromium().launch();
            Runtime.getRuntime().addShutdownHook(new Thread(playwright::close));
        }
        return browser;
    }
}
