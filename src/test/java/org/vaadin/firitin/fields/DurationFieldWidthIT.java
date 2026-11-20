package org.vaadin.firitin.fields;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.BoundingBox;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.vaadin.firitin.VaadinPage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DurationFieldWidthIT {

    @RegisterExtension
    final VaadinPage vaadin = new VaadinPage(DurationFieldWidthView.class);

    private Page page() {
        return vaadin.page();
    }

    private BoundingBox host(String id) {
        return page().locator("#" + id).boundingBox();
    }

    private BoundingBox input(String id) {
        return page().locator("#" + id + " vaadin-text-field").boundingBox();
    }

    private double emPx() {
        return ((Number) page().evaluate(
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
