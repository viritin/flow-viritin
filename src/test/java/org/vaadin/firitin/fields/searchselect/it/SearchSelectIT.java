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
import org.vaadin.firitin.fields.searchselect.SearchSelect;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * The browser side of {@link SearchSelect}, on the demo view
 * with NettiHPK's training types.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SearchSelectIT {

    private static final Playwright playwright = Playwright.create();

    @Value("${local.server.port}")
    private int port;

    private Browser browser;
    private Page page;

    @BeforeEach
    void open() {
        // Headless so the tests also run on CI and servers; flip to false to watch locally.
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        page = browser.newPage();
        page.setDefaultTimeout(15000);
        page.navigate("http://localhost:" + port + "/searchselect");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        assertThat(input()).isVisible();
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
    void typing_filters_by_all_words_in_any_order_without_any_server_requests() {
        AtomicInteger serverRequests = new AtomicInteger();
        page.onRequest(request -> {
            if (request.url().contains("v-r=uidl")) {
                serverRequests.incrementAndGet();
            }
        });

        input().click();
        input().pressSequentially("pk juo", new Locator.PressSequentiallyOptions().setDelay(80));

        assertThat(visibleItems()).hasText(new String[] {
                "Juoksu – (PK) pitkä lenkki", "Juoksu – (PK1) kevyt", "Juoksu – (PK2) rauhallinen"});
        page.waitForTimeout(600); // a server-filtering combo box would have sent its requests by now
        assertThat(serverRequests.get()).as("server requests while typing").isZero();
    }

    @Test
    void choosing_with_the_keyboard_sends_the_item_to_the_server() {
        input().click();
        input().pressSequentially("hiihto vaellus");
        assertThat(visibleItems()).hasText(new String[] {"Hiihto – Vaellus"});

        input().press("ArrowDown");
        input().press("Enter");

        assertThat(page.locator("#selected")).hasText("Valittu: Hiihto – Vaellus (käyttäjä)");
        assertThat(input()).hasValue("Hiihto – Vaellus");
    }

    @Test
    void a_value_set_on_the_server_is_shown() {
        page.getByText("Valitse palvelimelta: Hiihto – Vaellus").click();

        assertThat(page.locator("#selected")).hasText("Valittu: Hiihto – Vaellus (palvelin)");
        assertThat(input()).hasValue("Hiihto – Vaellus");
    }

    @Test
    void accents_and_case_are_ignored() {
        input().click();
        input().pressSequentially("PAIVA SAIRAS");

        List<String> items = visibleItems().allTextContents();
        assertThat(items).isNotEmpty().allMatch(item -> item.startsWith("Päivä"));
    }

    private Locator input() {
        return page.locator("vaadin-combo-box input");
    }

    private Locator visibleItems() {
        return page.locator("vaadin-combo-box-item:visible");
    }
}
