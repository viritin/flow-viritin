package org.vaadin.firitin.util.clipboard;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.function.SerializableConsumer;
import org.vaadin.firitin.components.button.VButton;

/**
 * A helper button to read text from the clipboard.
 * <p>
 *     As opposed to the current implementation of the {@link org.vaadin.firitin.util.clipboard.Clipboard}
 *     class, which could be used to implement this functionality as well, this class also supports Safari.
 * </p>
 *
 * @deprecated Vaadin 25.2 ships a native clipboard API,
 *             {@code com.vaadin.flow.component.clipboard.Clipboard}. Instead of a
 *             ready-made button, arm any {@code ClickNotifier} with
 *             {@code Clipboard.onClick(button).readText(consumer, errorConsumer)}.
 *             It also offers {@code Clipboard.onPaste(...)} for server-side Ctrl+V
 *             handling.
 */
@Deprecated(since = "3.6", forRemoval = true)
public class ReadFromClipboardButton extends VButton {

    private final SerializableConsumer<String> handler;

    public ReadFromClipboardButton(SerializableConsumer<String> textHandler) {
        super("Read from clipboard");
        this.handler = textHandler;

        getElement().executeJs("""
            this.addEventListener('click', () => {
                 navigator.clipboard.readText().then(value => {
                    this.$server.clipboard(value);
                });
            });
        """);
    }

    @ClientCallable
    private void clipboard(String value) {
        handler.accept(value);
    }
}

