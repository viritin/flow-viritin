package org.vaadin.firitin.util.clipboard;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.streams.ElementRequestHandler;
import org.vaadin.firitin.components.button.VButton;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * A helper button to copy certain text to the clipboard. Works asynchronously so that
 * the text can be generated when the button is clicked or provided statically.
 * As opposed to the current implementation of the {@link org.vaadin.firitin.util.clipboard.Clipboard}
 * class, which could be used to implement this functionality as well, this class also supports Safari.
 * <p>
 *     Currently only basic text is supported, not HTML or other formats (would be fairly easy
 *     to add though).
 * </p>
 *
 * @deprecated Vaadin 25.2 ships a native clipboard API,
 *             {@code com.vaadin.flow.component.clipboard.Clipboard}. Instead of a
 *             ready-made button, arm any {@code ClickNotifier} with
 *             {@code Clipboard.onClick(button).writeText(value, onSuccess, onError)}.
 *             Note the action must be armed once at construction (not from inside an
 *             {@code addClickListener}), so the privileged write runs within the
 *             browser's user-gesture window.
 */
@Deprecated(since = "3.6", forRemoval = true)
public class CopyToClipboardButton extends VButton {

    public CopyToClipboardButton(String string) {
        this(() -> string);
    }

    public CopyToClipboardButton(SerializableSupplier<String> textSupplier) {
        super("Copy to clipboard");
        getElement().setAttribute("txtUrl", (ElementRequestHandler) (request, response, session, owner) -> {
            response.setContentType("text/html; charset=utf-8");
            try {
                response.getWriter().write(textSupplier.get());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        getElement().executeJs("""
                this.addEventListener('click', () => {
                    const item = new ClipboardItem({
                        "text/plain": fetch(this.getAttribute("txtUrl")).then(response => response.text())
                    });
                    navigator.clipboard.write([item]);
                });
            """);
    }
}
