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
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.ui.LoadMode;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.io.IOException;
import java.io.InputStream;
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
 * </p>>
 */
public class RichText extends Div {

    private static final long serialVersionUID = -6926829115110918731L;
    transient private Safelist safelist;
    private String richText;
    private UI ui;

    public RichText() {
        setWidth("100%");
    }

    public RichText(String content) {
        setWidth("100%");
        setRichText(content);
    }



    public RichText withMarkDown(String markdown) {
        markdownStrategy.setMarkdown(markdown, this);
        return this;
    }

    public RichText withMarkDown(InputStream markdown) {
        try {
            // Note, this is now reading the whole markdown file into memory
            // previously it was read line by line. Probably a tiny bit less efficient.
            String mdString = new String(markdown.readAllBytes(), StandardCharsets.UTF_8);
            markdownStrategy.setMarkdown(mdString, this);
            return this;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public RichText appendMarkDown(String markdownFragment) {
        markdownStrategy.appendMarkdown(markdownFragment, this);
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
        ui.access(() -> {
            markdownStrategy.appendMarkdown(markdownFragment, this);
        });
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
        getElement().executeJs("this.innerHTML = $0", Jsoup.clean(text, getWhitelist()));
        return this;
    }

    public RichText setRichTextAndSaveReference(String text) {
        this.richText = text;
        getElement().setProperty("innerHTML", Jsoup.clean(richText, getWhitelist()));
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
        // Use flexmark-java if available, otherwise fallback to MarkdownIt in browser
        try {
            Class.forName("com.vladsch.flexmark.parser.Parser");
            markdownStrategy = new FlexMarkJavaStrategy();
        } catch (ClassNotFoundException e) {
            markdownStrategy = new MarkdownItStrategy();
        }
    }

    static class FlexMarkJavaStrategy implements MarkdownStrategy {

        private Parser parser;
        private HtmlRenderer renderer;

        public FlexMarkJavaStrategy() {
            parser = Parser.builder().build();
            renderer = HtmlRenderer.builder().build();
        }

        @Override
        public void setMarkdown(String markdown, RichText component) {
            String html = renderer.render(parser.parse(markdown));
            component.getElement().executeJs("this.innerHTML = $0", Jsoup.clean(html, component.getWhitelist()));
        }

        @Override
        public void appendMarkdown(String markdownFragment, RichText component) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        ui = attachEvent.getUI();
    }
}
