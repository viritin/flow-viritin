package org.vaadin.firitin.util;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.page.PendingJavaScriptResult;

import java.net.URI;

/**
 * A simple wrapper for the Web Share API. Allows sharing text and URLs quickly via OS
 * native share dialog, if the browser supports the Web Share (~ all but FF).
 * <p>
 * On Firefox (which does not support the Web Share API),
 * it falls back to copying the text and URL to the clipboard and showing a notification on the
 * screen.
 * </p>
 * <p>
 * See also: https://developer.mozilla.org/en-US/docs/Web/API/Web_Share_API
 *
 * @deprecated Vaadin 25.2 ships a native Web Share API,
 *             {@code com.vaadin.flow.component.webshare.WebShare}. Arm sharing with
 *             {@code WebShare.onClick(button).share(content, onSuccess, onError)} (once
 *             at construction, within the user-gesture window) and build the payload
 *             with {@code ShareContent.create().title(..).text(..).url(..)}; feature
 *             detection is via {@code WebShare.supportSignal()}.
 *             <p>
 *             <strong>Migration caveat:</strong> this Viritin {@code Share} has a
 *             built-in fallback for browsers without the API (notably Firefox desktop),
 *             configurable via {@link #setFirefoxFallbackNotification(Runnable)}. The
 *             native {@code WebShare} has <em>no</em> built-in fallback — you must
 *             handle the {@code UNSUPPORTED} case yourself via
 *             {@code WebShare.supportSignal()} so the fallback behavior is not silently
 *             lost.
 */
@Deprecated(since = "3.6", forRemoval = true)
public class Share {

    private static Runnable firefoxFallbackNotification = () -> {
        Notification.show("Web Share API is not supported, but URL copied to clipboard.");
    };

    /**
     * Sets the action executed when the Web Share API is not supported (e.g. on Firefox). By default,
     * it shows a notification that the URL has been copied to clipboard. Use e.g. to show a custom
     * message.
     *
     * @param firefoxFallbackNotification the action to execute when the Web Share API is not supported.
     */
    public static void setFirefoxFallbackNotification(Runnable firefoxFallbackNotification) {
        Share.firefoxFallbackNotification = firefoxFallbackNotification;
    }

    /**
     * Share a link using the Web Share API.
     *
     * @param title The title of the document being shared. May be ignored by the target.
     * @param text  Arbitrary text that forms the body of the message being shared.
     * @param url   A URL string referring to a resource being shared.
     */
    public static PendingJavaScriptResult share(String title, String text, URI url) {
        return share(new ShareData(title, text, url.toString()));
    }

    /**
     * Share a link using the Web Share API.
     *
     * @param title The title of the document being shared. May be ignored by the target.
     * @param text  Arbitrary text that forms the body of the message being shared.
     * @param url   A URL string referring to a resource being shared.
     */
    public static PendingJavaScriptResult share(String title, String text, String url) {
        return share(new ShareData(title, text, url));
    }

    public static PendingJavaScriptResult share(ShareData data) {
        PendingJavaScriptResult pendingJavaScriptResult = UI.getCurrent().getPage().executeJs("""
                    const data = {
                        title: $0,
                        text: $1,
                        url: $2
                    };
                    if (!navigator.share) {
                        // FF is what it is, but it does support cliboard API these days.
                        // Fall back to copying the URL to clipboard in the same way as MacOS/iOS does when sharing
                        // e.g. to email clients
                        return navigator.clipboard.writeText(data.text + " " + data.url)
                            .then(() => {
                                console.warn("Web Share API is not supported, but URL copied to clipboard.");
                                return Promise.resolve("CLIPBOARD_COPY_SUCCESS");
                            })
                            .catch(err => {
                                console.error("Failed to copy URL to clipboard:", err);
                                return Promise.reject("CLIPBOARD_COPY_FAILED: " + err);
                            });
                    } else {
                        return navigator.share(data);
                    }
                """, data.title, data.text, data.url);

        pendingJavaScriptResult.then(json -> {
            String string = json.asText();
            if ("CLIPBOARD_COPY_SUCCESS".equals(string)) {
                firefoxFallbackNotification.run();
                return;
            }
        }, error -> {
            // NOOP, most likely the user cancelled the share dialog, user can use the return value
            // to do something, but most likely no need to do anything.
        });

        return pendingJavaScriptResult;

    }

    /**
     * @deprecated Part of the deprecated {@link Share} helper. Use
     *             {@code com.vaadin.flow.component.webshare.ShareContent} from Vaadin
     *             25.2 instead.
     */
    @Deprecated(since = "3.6", forRemoval = true)
    public record ShareData(String title, String text, String url) {
    }

}
