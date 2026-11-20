package org.vaadin.firitin.fields.searchselect.it;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.vaadin.firitin.VaadinPage;
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

    @RegisterExtension
    final VaadinPage vaadin = new VaadinPage(LargeListView.class);

    private Page page() {
        return vaadin.page();
    }

    private final AtomicInteger serverRequests = new AtomicInteger();

    @BeforeEach
    void open() {
        assertThat(input("backend")).isVisible();
        page().onRequest(request -> {
            if (request.url().contains("v-r=uidl")) {
                serverRequests.incrementAndGet();
            }
        });
    }

    @Test
    void fluent_typing_causes_a_single_query() {
        input("backend").click();
        input("backend").pressSequentially("pk1 juo 2026", new Locator.PressSequentiallyOptions().setDelay(80));

        assertThat(visibleItems()).hasText(new String[] {"Juoksu – (PK1) kevyt, season 2026"});
        assertThat(page().locator("#queries")).containsText("(latest: \"pk1 juo 2026\")");
        page().waitForTimeout(500);
        assertThat(serverRequests.get()).as("server requests").isEqualTo(1);
    }

    @Test
    void a_pause_while_typing_queries_the_server() {
        input("backend").click();
        input("backend").pressSequentially("juo");
        assertThat(page().locator("#queries")).containsText("(latest: \"juo\")");

        input("backend").pressSequentially(" 2026 pk1", new Locator.PressSequentiallyOptions().setDelay(80));

        assertThat(visibleItems()).hasText(new String[] {"Juoksu – (PK1) kevyt, season 2026"});
        assertThat(serverRequests.get()).as("server requests").isEqualTo(2);
    }

    @Test
    void refining_narrows_the_previous_results_at_once() {
        input("backend").click();
        input("backend").pressSequentially("juo");
        assertThat(page().locator("#queries")).containsText("(latest: \"juo\")");
        // From now on the server answers slowly: what is shown must come from the browser.
        page().route("**/*v-r=uidl*", route -> {
            page().waitForTimeout(3000);
            route.resume();
        });

        input("backend").pressSequentially(" season 1901");

        assertThat(visibleItems().first()).containsText("season 1901",
                new com.microsoft.playwright.assertions.LocatorAssertions.ContainsTextOptions().setTimeout(1000));
        assertThat(visibleItems().allTextContents()).allMatch(item -> item.toLowerCase().contains("juo") && item.endsWith("1901"));
    }

    @Test
    void an_item_found_on_the_server_can_be_chosen() {
        input("in-memory").click();
        input("in-memory").pressSequentially("hiihto vaellus 2100");
        assertThat(visibleItems()).hasText(new String[] {"Hiihto – Vaellus, season 2100"});

        input("in-memory").press("ArrowDown");
        input("in-memory").press("Enter");

        assertThat(page().locator("#selected")).hasText("Selected: Hiihto – Vaellus, season 2100");
        assertThat(input("in-memory")).hasValue("Hiihto – Vaellus, season 2100");
    }

    private Locator input(String id) {
        return page().locator("#" + id + " input");
    }

    private Locator visibleItems() {
        return page().locator("vaadin-combo-box-item:visible");
    }
}
