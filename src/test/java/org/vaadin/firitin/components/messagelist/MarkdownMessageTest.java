package org.vaadin.firitin.components.messagelist;

import com.vaadin.flow.component.UI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
}
