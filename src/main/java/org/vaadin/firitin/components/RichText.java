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

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.ui.LoadMode;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author mstahv
 */

/**
 * XSS safe rich text label with either Markdown syntax or raw html (sanitized
 * with Jsoup).
 * <p>
 * By default jsoups Safelist.relaxed is used for sanitizing. This can be
 * overridden by returning custom safelist with getSafelist method.
 */
public class RichText extends Div {

    private static final long serialVersionUID = -6926829115110918731L;
    transient private Safelist safelist;
    private String richText;

    public RichText() {
        setWidth("100%");
    }

    public RichText(String content) {
        setWidth("100%");
        setRichText(content);
    }



    public RichText withMarkDown(String markdown) {
        markdownStrategy.toElement(markdown, this);
        return this;
    }


    public RichText withMarkDown(InputStream markdown) {
        try {
            // Note, this is now reading the whole markdown file into memory
            // previously it was read line by line. Probably a tiny bit less efficient.
            String mdString = IOUtils.toString(markdown, "UTF-8");
            markdownStrategy.toElement(mdString, this);
            return this;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public RichText withSafeHtml(String html) {
        return setRichText(html);
    }

    public RichText withSafeHtml(InputStream markdown) {
        try {
            return setRichText(IOUtils.toString(markdown, "UTF-8"));
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
         void toElement(String markdown, RichText component);
    }

    public static class MarkdownItStrategy implements MarkdownStrategy {

        @Override
        public void toElement(String markdown, RichText component) {
            ensureMarkdownIt();
            component.getElement().executeJs("""
                const md = window.markdownit(); 
                const input = $0;
                const html = md.render(input);
                this.innerHTML = html;
            """, markdown);
        }

        private void ensureMarkdownIt() {
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
        public void toElement(String markdown, RichText component) {
            String html = renderer.render(parser.parse(markdown));
            component.getElement().executeJs("this.innerHTML = $0", Jsoup.clean(html, component.getWhitelist()));
        }
    }

}
