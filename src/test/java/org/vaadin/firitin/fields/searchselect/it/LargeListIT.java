package org.vaadin.firitin.fields.searchselect.it;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.vaadin.firitin.fields.searchselect.LargeListView;
import org.vaadin.firitin.fields.searchselect.SearchSelect;

import java.util.concurrent.atomic.AtomicInteger;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Server-side filtering of {@link SearchSelect} for large
 * lists, on {@link LargeListView} (~19 000 items).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LargeListIT {

    private static final Playwright playwright = Playwright.create();

    @Value("${local.server.port}")
    private int port;

    private Browser browser;
    private Page page;
    private final AtomicInteger serverRequests = new AtomicInteger();

    @BeforeEach
    void open() {
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        page = browser.newPage();
        page.setDefaultTimeout(15000);
        page.navigate("http://localhost:" + port + "/searchselect-large");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertThat(input("backend")).isVisible();
        page.onRequest(request -> {
            if (request.url().contains("v-r=uidl")) {
                serverRequests.incrementAndGet();
            }
        });
    }

    @AfterEach
    void close() {
        browser.close();
    }

    @AfterAll
    static void closePlaywright() {
        playwright.close();
    }

    @Test
    void fluent_typing_causes_a_single_query() {
        input("backend").click();
        input("backend").pressSequentially("pk1 juo 2026", new Locator.PressSequentiallyOptions().setDelay(80));

        assertThat(visibleItems()).hasText(new String[] {"Juoksu – (PK1) kevyt, kausi 2026"});
        assertThat(page.locator("#queries")).containsText("(viimeisin: \"pk1 juo 2026\")");
        page.waitForTimeout(500);
        assertThat(serverRequests.get()).as("server requests").isEqualTo(1);
    }

    @Test
    void a_pause_while_typing_queries_the_server() {
        input("backend").click();
        input("backend").pressSequentially("juo");
        assertThat(page.locator("#queries")).containsText("(viimeisin: \"juo\")");

        input("backend").pressSequentially(" 2026 pk1", new Locator.PressSequentiallyOptions().setDelay(80));

        assertThat(visibleItems()).hasText(new String[] {"Juoksu – (PK1) kevyt, kausi 2026"});
        assertThat(serverRequests.get()).as("server requests").isEqualTo(2);
    }

    @Test
    void refining_narrows_the_previous_results_at_once() {
        input("backend").click();
        input("backend").pressSequentially("juo");
        assertThat(page.locator("#queries")).containsText("(viimeisin: \"juo\")");
        // From now on the server answers slowly: what is shown must come from the browser.
        page.route("**/*v-r=uidl*", route -> {
            page.waitForTimeout(3000);
            route.resume();
        });

        input("backend").pressSequentially(" kausi 1901");

        assertThat(visibleItems().first()).containsText("kausi 1901",
                new com.microsoft.playwright.assertions.LocatorAssertions.ContainsTextOptions().setTimeout(1000));
        assertThat(visibleItems().allTextContents()).allMatch(item -> item.toLowerCase().contains("juo") && item.endsWith("1901"));
    }

    @Test
    void an_item_found_on_the_server_can_be_chosen() {
        input("in-memory").click();
        input("in-memory").pressSequentially("hiihto vaellus 2100");
        assertThat(visibleItems()).hasText(new String[] {"Hiihto – Vaellus, kausi 2100"});

        input("in-memory").press("ArrowDown");
        input("in-memory").press("Enter");

        assertThat(page.locator("#selected")).hasText("Valittu: Hiihto – Vaellus, kausi 2100");
        assertThat(input("in-memory")).hasValue("Hiihto – Vaellus, kausi 2100");
    }

    private Locator input(String id) {
        return page.locator("#" + id + " input");
    }

    private Locator visibleItems() {
        return page.locator("vaadin-combo-box-item:visible");
    }
}
