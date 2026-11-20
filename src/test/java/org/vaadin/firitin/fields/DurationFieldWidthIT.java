package org.vaadin.firitin.fields;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.BoundingBox;
import in.virit.mopo.Mopo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DurationFieldWidthIT {

    @Value("${local.server.port}")
    private int port;

    static Playwright playwright = Playwright.create();

    private Browser browser;
    private Page page;

    @BeforeEach
    public void setup() {
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions());
        page = browser.newPage();
        page.setDefaultTimeout(60_000);
        page.navigate("http://localhost:" + port + "/durationfieldwidth");
        new Mopo(page).waitForConnectionToSettle();
    }

    @AfterEach
    public void closePlaywright() {
        page.close();
        browser.close();
    }

    private BoundingBox host(String id) {
        return page.locator("#" + id).boundingBox();
    }

    private BoundingBox input(String id) {
        return page.locator("#" + id + " vaadin-text-field").boundingBox();
    }

    private double emPx() {
        return ((Number) page.evaluate(
                "() => parseFloat(getComputedStyle(document.querySelector('#default vaadin-text-field')).fontSize)"))
                .doubleValue();
    }

    @Test
    public void defaultWidthUnchanged() {
        assertEquals(8 * emPx(), input("default").width, 1);
    }

    @Test
    public void explicitWidthIsRespected() {
        BoundingBox host = host("narrow");
        BoundingBox input = input("narrow");
        assertEquals(6.5 * emPx(), host.width, 1);
        assertTrue(input.x + input.width <= host.x + host.width + 0.5,
                "inner input overflows the field: " + input.width + " > " + host.width);
        assertTrue(input.x + input.width <= host("amount").x,
                "inner input overlaps the next component");
    }

    @Test
    public void widthFullFillsParent() {
        assertEquals(400, host("full").width, 1);
        assertEquals(400, input("full").width, 1);
    }

    @Test
    public void undefinedWidthRestoresDefault() {
        assertEquals(8 * emPx(), input("reset").width, 1);
    }
}
