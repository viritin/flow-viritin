package org.vaadin.firitin.fields.searchselect.it;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.vaadin.firitin.VaadinPage;
import org.vaadin.firitin.fields.searchselect.SearchSelect;
import org.vaadin.firitin.fields.searchselect.TrainingTypesView;

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

    @RegisterExtension
    final VaadinPage vaadin = new VaadinPage(TrainingTypesView.class);

    private Page page() {
        return vaadin.page();
    }

    @BeforeEach
    void open() {
        assertThat(input()).isVisible();
    }

    @Test
    void typing_filters_by_all_words_in_any_order_without_any_server_requests() {
        AtomicInteger serverRequests = new AtomicInteger();
        page().onRequest(request -> {
            if (request.url().contains("v-r=uidl")) {
                serverRequests.incrementAndGet();
            }
        });

        input().click();
        input().pressSequentially("pk juo", new Locator.PressSequentiallyOptions().setDelay(80));

        assertThat(visibleItems()).hasText(new String[] {
                "Juoksu – (PK) pitkä lenkki", "Juoksu – (PK1) kevyt", "Juoksu – (PK2) rauhallinen"});
        page().waitForTimeout(600); // a server-filtering combo box would have sent its requests by now
        assertThat(serverRequests.get()).as("server requests while typing").isZero();
    }

    @Test
    void choosing_with_the_keyboard_sends_the_item_to_the_server() {
        input().click();
        input().pressSequentially("hiihto vaellus");
        assertThat(visibleItems()).hasText(new String[] {"Hiihto – Vaellus"});

        input().press("ArrowDown");
        input().press("Enter");

        assertThat(page().locator("#selected")).hasText("Selected: Hiihto – Vaellus (user)");
        assertThat(input()).hasValue("Hiihto – Vaellus");
    }

    @Test
    void a_value_set_on_the_server_is_shown() {
        page().getByText("Select on server: Hiihto – Vaellus").click();

        assertThat(page().locator("#selected")).hasText("Selected: Hiihto – Vaellus (server)");
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
        return page().locator("vaadin-combo-box input");
    }

    private Locator visibleItems() {
        return page().locator("vaadin-combo-box-item:visible");
    }
}
