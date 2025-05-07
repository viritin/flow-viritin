package org.vaadin.firitin.util;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.PendingJavaScriptResult;

import java.net.URI;

/**
 * A simple wrapper for the Web Share API.
 *
 * See also: https://developer.mozilla.org/en-US/docs/Web/API/Web_Share_API
 */
public class Share {

    public record ShareData(String title, String text, String url) {
    }

    /**
     * Share a link using the Web Share API.
     *
     * @param title The title of the document being shared. May be ignored by the target.
     * @param text Arbitrary text that forms the body of the message being shared.
     * @param url A URL string referring to a resource being shared.
     */
    public static PendingJavaScriptResult share(String title, String text, URI url) {
        return share(new ShareData(title, text, url.toString()));
    }

    /**
     * Share a link using the Web Share API.
     *
     * @param title The title of the document being shared. May be ignored by the target.
     * @param text Arbitrary text that forms the body of the message being shared.
     * @param url A URL string referring to a resource being shared.
     */
    public static PendingJavaScriptResult share(String title, String text, String url) {
        return share(new ShareData(title, text, url));
    }

    public static PendingJavaScriptResult share(ShareData data) {
            return UI.getCurrent().getPage().executeJs("""
                const data = {
                    title: $0,
                    text: $1,
                    url: $2
                };
                debugger;
                return navigator.share(data);
            """, data.title, data.text, data.url);
    }

}
