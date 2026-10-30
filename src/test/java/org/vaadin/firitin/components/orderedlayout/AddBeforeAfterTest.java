package org.vaadin.firitin.components.orderedlayout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Span;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AddBeforeAfterTest {

    private final Span a = new Span("a");
    private final Span b = new Span("b");
    private final Span c = new Span("c");
    private final Span x = new Span("x");
    private final Span y = new Span("y");
    private final VVerticalLayout layout = new VVerticalLayout(a, b, c);

    private BrowserlessUIContext ui;

    @BeforeEach
    void setUp() {
        ui = BrowserlessUIContext.forComponent(layout);
    }

    @AfterEach
    void tearDown() {
        ui.close();
    }

    @Test
    void addBefore() {
        layout.addBefore(b, x, y);
        assertEquals(List.of(a, x, y, b, c), children());
    }

    @Test
    void addBeforeFirst() {
        layout.addBefore(a, x);
        assertEquals(List.of(x, a, b, c), children());
    }

    @Test
    void addAfter() {
        layout.addAfter(b, x, y);
        assertEquals(List.of(a, b, x, y, c), children());
    }

    @Test
    void addAfterLast() {
        layout.addAfter(c, x);
        assertEquals(List.of(a, b, c, x), children());
    }

    @Test
    void moveExistingChildForward() {
        layout.addAfter(c, a);
        assertEquals(List.of(b, c, a), children());
    }

    @Test
    void moveExistingChildBackward() {
        layout.addBefore(a, c);
        assertEquals(List.of(c, a, b), children());
    }

    @Test
    void moveExistingChildBeforeLaterSibling() {
        layout.addBefore(c, a);
        assertEquals(List.of(b, a, c), children());
    }

    @Test
    void moveFromOtherLayout() {
        VHorizontalLayout other = new VHorizontalLayout(x);
        layout.addAfter(a, x);
        assertEquals(List.of(a, x, b, c), children());
        assertEquals(0, other.getComponentCount());
    }

    @Test
    void chaining() {
        layout.addBefore(a, x).addAfter(c, y);
        assertEquals(List.of(x, a, b, c, y), children());
    }

    @Test
    void referenceNotAChild() {
        assertThrows(IllegalArgumentException.class, () -> layout.addAfter(x, y));
        assertEquals(List.of(a, b, c), children());
    }

    @Test
    void referenceAmongAdded() {
        assertThrows(IllegalArgumentException.class, () -> layout.addAfter(b, x, b));
        assertEquals(List.of(a, b, c), children());
    }

    private List<Component> children() {
        return layout.getChildren().toList();
    }
}
