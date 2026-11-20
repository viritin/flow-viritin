package org.vaadin.firitin.components.datepicker;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.vaadin.addons.dramafinder.AutoWait;
import org.vaadin.firitin.VaadinPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Verifies that VDatePicker, VTimePicker and VDateTimePicker fire value change
 * events while the user is still typing (like VTextField with LAZY value change
 * mode), not only when the field loses focus.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LazyDateValueChangeIT {

    @RegisterExtension
    final VaadinPage vaadin = new VaadinPage(LazyDateValueChangeView.class);

    private Page page() {
        return vaadin.page();
    }

    @Test
    public void textFieldReference() {
        typeWithoutBlur("#text input", "foo");
        assertValueWithoutBlur("#text", "#text-value", "foo");
    }

    @Test
    public void datePickerFiresValueChangeWhileTyping() {
        typeWithoutBlur("#date input", "14.11.2026");
        assertValueWithoutBlur("#date", "#date-value", "2026-11-14");
    }

    @Test
    public void datePickerClearedWhileTyping() {
        typeWithoutBlur("#date input", "14.11.2026");
        assertValueWithoutBlur("#date", "#date-value", "2026-11-14");
        page().locator("#date input").fill("");
        assertValueWithoutBlur("#date", "#date-value", "null");
    }

    @Test
    public void datePickerDoesNotCommitIncompleteInput() {
        // "14.11.20" would parse as year 2020 and be reformatted, so it
        // must not be committed (and rewritten) while the user is typing
        typeWithoutBlur("#date input", "14.11.20");
        page().waitForTimeout(1000);
        assertThat(page().locator("#date-value")).hasText("-");
        assertThat(page().locator("#date input")).hasValue("14.11.20");
    }

    @Test
    public void datePickerDoesNotFireAgainOnBlur() {
        typeWithoutBlur("#date input", "14.11.2026");
        assertValueWithoutBlur("#date", "#date-value", "2026-11-14");
        page().locator("#date input").press("Tab");
        AutoWait.waitForVaadinIdle(page());
        page().waitForTimeout(500);
        assertThat(page().locator("#date-count")).hasText("1");
        assertThat(page().locator("#date input")).hasValue("14.11.2026");
    }

    @Test
    public void onChangeModeKeepsWebComponentDefaults() {
        typeWithoutBlur("#date-onchange input", "14.11.2026");
        page().waitForTimeout(1000);
        assertThat(page().locator("#date-onchange-value")).hasText("-");
        page().locator("#date-onchange input").press("Enter");
        assertThat(page().locator("#date-onchange-value")).hasText("2026-11-14");
    }

    @Test
    public void timePickerFiresValueChangeWhileTyping() {
        typeWithoutBlur("#time input", "10:30");
        assertValueWithoutBlur("#time", "#time-value", "10:30");
    }

    @Test
    public void dateTimePickerFiresValueChangeWhileTyping() {
        typeWithoutBlur("#datetime vaadin-date-picker input", "14.11.2026");
        typeWithoutBlur("#datetime vaadin-time-picker input", "10.30");
        assertValueWithoutBlur("#datetime vaadin-time-picker", "#datetime-value", "2026-11-14T10:30");
    }

    private void typeWithoutBlur(String inputSelector, String text) {
        Locator input = page().locator(inputSelector);
        input.click();
        input.pressSequentially(text, new Locator.PressSequentiallyOptions().setDelay(30));
    }

    private void assertValueWithoutBlur(String fieldSelector, String valueSelector, String expected) {
        assertThat(page().locator(valueSelector)).hasText(expected,
                new com.microsoft.playwright.assertions.LocatorAssertions.HasTextOptions().setTimeout(5_000));
        // the value must have arrived without the user leaving the field
        assertThat(page().locator(fieldSelector)).hasAttribute("focused", "");
        assertThat(page().locator("#save")).isEnabled();
    }
}
