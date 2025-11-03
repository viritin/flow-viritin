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
 */
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

    public record ShareData(String title, String text, String url) {
    }

}
