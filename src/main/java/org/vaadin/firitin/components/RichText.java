/*
 * Copyright 2019 Viritin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.firitin.components;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.ui.LoadMode;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;

/**
 * XSS safe rich text label with either Markdown syntax or raw html (sanitized
 * with Jsoup). In case markdown format is used, the default is to use dynamcally
 * loaded markdown-it JS library in the browser (and omitting Jsoup sanitizing),
 * which at least claims to be secure by default.
 * <p>
 * By default jsoups Safelist.relaxed is used for sanitizing. This can be
 * overridden by returning custom safelist with getSafelist method.
 * </p>
 * <p>
 * For a more secure approach with markdown formatting, add
 * "com.vladsch.flexmark:flexmark" dependency to your project and the component
 * starts to use server side rendering and Jsoup sanitizing instead. You can also
 * customize behaviour by implementing custom {@link MarkdownStrategy} (and e.g.
 * use a bundled markdown-it instead of the dynamically loaded one).
 * </p>
 * <p>
 *     Note, that for basic Markdown usage, the Markdown component from Vaadin core
 *     is now available since Vaadin 24.8. This component though provides more efficiency
 *     (does not save content to the server memory), easier to use append method (no need
 *     for UI.access dance) and more flexibility (uses the excellent Flexmark library for
 *     server side rendering if available, else falls back to the client side markdown-it)
 * </p>
 * <p>
 * As the content is by default only sent to the browser, the browser discards
 * it if the component is detached (moving the component within the same round
 * trip and toggling visibility are fine). To support re-attaching without
 * consuming server memory, the component keeps a {@link WeakReference} to the
 * given content string and sends it again on re-attach. This always works
 * with string literals and constants, and with content otherwise kept in
 * memory by the application. Content built dynamically (e.g. read from a file)
 * is typically garbage collected soon, and then an
 * {@link IllegalStateException} is thrown on re-attach. The same happens if
 * markdown has been appended (the full content only exists in the browser) or
 * the session has been deserialized. If you need to re-attach reliably, set the
 * content again before that, or use {@link #setRichTextAndSaveReference(String)}.
 * </p>
 */
public class RichText extends Div {

    private static final long serialVersionUID = -6926829115110918731L;
    transient private Safelist safelist;
    private String richText;
    private UI ui;
    // Content was sent to the browser without keeping it on the server
    private boolean contentOnlyInBrowser;
    // Incremented when content is (re)set, to detect content set while detached
    private int contentVersion;
    private boolean contentLost;
    // The given content (not a copy of it) for re-sending on re-attach. Weak,
    // so it doesn't consume memory unless the app keeps it anyway (literals)
    private transient WeakReference<String> contentRef;
    private boolean contentIsMarkdown;

    public RichText() {
        setWidth("100%");
    }

    public RichText(String content) {
        setWidth("100%");
        setRichText(content);
    }



    public RichText withMarkDown(String markdown) {
        sendOnlyToBrowser(markdown, true);
        return this;
    }

    public RichText withMarkDown(InputStream markdown) {
        try {
            // Note, this is now reading the whole markdown file into memory
            // previously it was read line by line. Probably a tiny bit less efficient.
            String mdString = new String(markdown.readAllBytes(), StandardCharsets.UTF_8);
            return withMarkDown(mdString);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public RichText appendMarkDown(String markdownFragment) {
        markdownStrategy.appendMarkdown(markdownFragment, this);
        contentRef = null;
        return this;
    }

    /**
     * A shorthand to append markdown snippet to the content from a non-UI thread.
     * This is "the AI chatbot API".
     *
     * @param markdownFragment new markdown fragment to append
     * @return the component for further configuration
     */
    public RichText appendMarkDownAsync(String markdownFragment) {
        assert ui != null;
        ui.access(() -> appendMarkDown(markdownFragment));
        return this;
    }

    public RichText withSafeHtml(String html) {
        return setRichText(html);
    }

    public RichText withSafeHtml(InputStream markdown) {
        try {
            return setRichText(new String(markdown.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException ex) {
            throw new RuntimeException("Input stream coulnd't be read!", ex);
        }
    }

    /**
     * Only replaces all new line characters with &lt;br /&gt;, but no Markdown
     * processing.
     *
     * @param text the text value to be displayed
     * @return the object itself for further configuration
     */
    public RichText withNewLines(String text) {
        return setRichText(text.replaceAll("(\\r|\\n|\\r\\n)+", "<br />"));
    }

    /**
     * @return the safelist
     * @deprecated use getSafelist instead
     */
    @Deprecated
    public Safelist getWhitelist() {
        return getSafelist();
    }

    public Safelist getSafelist() {
        if (safelist == null) {
            return Safelist.relaxed();
        }
        return safelist;
    }

    /**
     * @param whitelist the whitelist used for sanitizing the rich text content
     * @return the object itself for further configuration
     * @deprecated Whitelist is not serializable. Override getWhitelist instead
     * if you need to support serialiazation
     */
    @Deprecated
    public RichText setSafelist(Safelist whitelist) {
        this.safelist = whitelist;
        return this;
    }

    /**
     * Return the richt text set using setRichTextAndSaveReference method.
     * Normally reference to the content is not saved to save server memory.
     *
     * @return the rich text set to this content, if available
     * @deprecated might return null if text not set with setRichTextAndSaveReference method.
     */
    @Override
    @Deprecated
    public String getText() {
        return richText;
    }

    public RichText setRichText(String text) {
        sendOnlyToBrowser(text, false);
        return this;
    }

    public RichText setRichTextAndSaveReference(String text) {
        this.richText = text;
        getElement().setProperty("innerHTML", Jsoup.clean(richText, getWhitelist()));
        contentOnlyInBrowser = false;
        contentVersion++;
        contentLost = false;
        contentRef = null;
        return this;
    }

    public RichText withMarkDownResource(String resourceName) {
        return withMarkDown(getClass().getResourceAsStream(resourceName));
    }

    public RichText withSafeHtmlResource(String resourceName) {
        return withSafeHtml(getClass().getResourceAsStream(resourceName));
    }

    public RichText withContent(String content) {
        return setRichText(content);
    }

    public interface MarkdownStrategy {
         void setMarkdown(String markdown, RichText component);
         void appendMarkdown(String markdownFragment, RichText component);
    }

    public static class MarkdownItStrategy implements MarkdownStrategy {

        @Override
        public void setMarkdown(String markdown, RichText component) {
            ensureMarkdownIt();
            component.getElement().executeJs("""
                const md = window.markdownit(); 
                const input = $0;
                this.markdown = input;// save for appending
                const html = md.render(input);
                this.innerHTML = html;
            """, markdown);
        }

        @Override
        public void appendMarkdown(String markdownFragment, RichText component) {
            ensureMarkdownIt();
            component.getElement().executeJs("""
                const md = window.markdownit();
                const input = $0;
                if(this.markdown) {
                    this.markdown = this.markdown + input;
                } else {
                    this.markdown = input;
                }
                const html = md.render(this.markdown);
                this.innerHTML = html;
            """, markdownFragment);
        }

        public static void ensureMarkdownIt() {
            UI ui = UI.getCurrent();
            if (ui == null) {
                throw new IllegalStateException("UI is not available");
            }
            final String jsloadedflag = MarkdownItStrategy.class.getName()+"_jsloaded";
            Object flag = ComponentUtil.getData(ui, jsloadedflag);
            if(flag == null) {
                ui.getPage().addJavaScript("https://cdn.jsdelivr.net/npm/markdown-it@14.1.0/dist/markdown-it.min.js", LoadMode.EAGER);
                ComponentUtil.setData(ui, jsloadedflag, true);
                ui.addDetachListener(e -> {
                    ComponentUtil.setData(ui, jsloadedflag, null);
                    e.unregisterListener();
                });
            }
        }
    }

    public static MarkdownStrategy markdownStrategy;

    static {
        markdownStrategy = new MarkdownItStrategy();
    }

    private void sendOnlyToBrowser(String content, boolean markdown) {
        send(content, markdown);
        contentRef = new WeakReference<>(content);
        contentIsMarkdown = markdown;
        contentOnlyInBrowser = true;
        // Pending JS is executed on attach, so the content is there again
        contentVersion++;
        contentLost = false;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        ui = attachEvent.getUI();
        if (contentLost) {
            String content = contentRef == null ? null : contentRef.get();
            if (content == null) {
                throw new IllegalStateException("""
                        RichText was re-attached, but its content is gone: the \
                        content was only sent to the browser (to save server \
                        memory) and the browser discarded it when the component \
                        was detached. The weak reference kept for re-attaching \
                        has been garbage collected (content not kept in memory \
                        by the application, unlike e.g. string literals), or \
                        markdown was appended (the full content only exists in \
                        the browser). Set the \
                        content again before re-attaching, use \
                        setRichTextAndSaveReference(String) to keep the content \
                        on the server, or hide the component with \
                        setVisible(false) instead of removing it.""");
            }
            send(content, contentIsMarkdown);
            contentLost = false;
        }
    }

    private void send(String content, boolean markdown) {
        if (markdown) {
            markdownStrategy.setMarkdown(content, this);
        } else {
            // Sanitized here, the cleaned copy is not referenced by anything
            getElement().executeJs("this.innerHTML = $0", Jsoup.clean(content, getWhitelist()));
        }
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        if (contentOnlyInBrowser) {
            // The element survives in the browser if re-attached within the
            // same round trip (e.g. moved to another place), so check only
            // when the response is written. The UI as the context, as
            // callbacks for a detached component would not be executed.
            UI detachedFrom = detachEvent.getUI();
            int version = contentVersion;
            detachedFrom.beforeClientResponse(detachedFrom, ctx -> {
                if (!isAttached() && contentVersion == version) {
                    contentLost = true;
                }
            });
        }
    }
}
