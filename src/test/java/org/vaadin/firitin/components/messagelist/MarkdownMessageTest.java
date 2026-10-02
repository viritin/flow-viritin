package org.vaadin.firitin.components.messagelist;

import com.vaadin.flow.component.UI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vaadin.firitin.util.style.LumoProps;

public class MarkdownMessageTest {

    /** https://github.com/viritin/flow-viritin/issues/104 */
    @Test
    public void nameWithNegativeHashCodeGetsAnAvatarColor() {
        Assertions.assertTrue("dialog".hashCode() < 0);
        // setMarkdown needs a UI to schedule the rendering on
        UI.setCurrent(new UI());
        try {
            Assertions.assertDoesNotThrow(() -> new MarkdownMessage("<code>Hello world</code>", "dialog"));
        } finally {
            UI.setCurrent(null);
        }
    }

    /** https://github.com/viritin/flow-viritin/issues/103 */
    @Test
    public void avatarColorCanBeACssVariable() {
        UI.setCurrent(new UI());
        try {
            var message = new MarkdownMessage("Hi", "bot");
            message.setAvatarColor("var(--lumo-primary-color)");
            Assertions.assertEquals("var(--lumo-primary-color)",
                    message.getElement().getStyle().get("--vaadin-avatar-user-color"));
            message.setAvatarColor(LumoProps.ERROR_COLOR);
            Assertions.assertEquals("var(--lumo-error-color)",
                    message.getElement().getStyle().get("--vaadin-avatar-user-color"));
        } finally {
            UI.setCurrent(null);
        }
    }
}
