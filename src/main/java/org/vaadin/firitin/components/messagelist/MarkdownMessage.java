package org.vaadin.firitin.components.messagelist;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.auth.AnnotatedViewAccessChecker;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A wrapper for vaadin-message web component, that supports Markdown
 * formatting and appending content dynamically to the element. Typical
 * usecase: LLM chatbots slowly giving you the answer.
 */
@Tag("vaadin-message")
public class MarkdownMessage extends Component {
    public record Color(String cssColorCode){

        // "Stolen" from https://github.com/vaadin/web-components/blob/1875686236814dcc065a0e067c87adb80153ce60/packages/vaadin-lumo-styles/user-colors.js#L12
        public static Color[] AVATAR_PRESETS = new Color[] {
                new Color("#df0b92"),
                new Color("#650acc"),
                new Color("#097faa"),
                new Color("#ad6200"),
                new Color("#bf16f3"),
                new Color("#084391"),
                new Color("#078836")
        };
    };

    private static HtmlRenderer renderer;
    private static Parser parser;
    private UI ui;

    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.appendMarkdown(markdown);
    }

    private String markdown;
    private String previousHtml;

    private boolean autoScroll = true;

    private Element content = new Element("div");
    private Element scrollHelper = new Element("div");

    /**
     * Constructs a new MarkdownMessages with all the bells and whistles,
     * without initial content. Add that later with {@link #appendMarkdownAsync(String)}.
     *
     * @param name the name of the user
     * @param timestamp time of the message
     * @param color the color used for user avatar
     */
    public MarkdownMessage(String name, LocalDateTime timestamp, Color color) {
        if(color != null) {
            setAvatarColor(color);
        }
        getElement().setProperty("userName", name);
        getElement().setProperty("time", timestamp.format(DateTimeFormatter.ofPattern("YYYY-MM-DD hh:mm")));
        getElement().appendChild(content, scrollHelper);
        content.getStyle().setWhiteSpace(Style.WhiteSpace.NORMAL);
    }

    /**
     * Constructs a new MarkdownMessages, without initial content.
     * Add that later with {@link #appendMarkdownAsync(String)}.
     *
     * @param name the name of the user
     */
    public MarkdownMessage(String name) {
        this(name, LocalDateTime.now(), Color.AVATAR_PRESETS[0]);
    }

    /**
     * Constructs a new MarkdownMessages, without initial content.
     * Add that later with {@link #appendMarkdownAsync(String)}.
     *
     * @param name the name of the user
     * @param timestamp the timestamp of the message
     */
    public MarkdownMessage(String name, LocalDateTime timestamp) {
        this(name, timestamp,Color.AVATAR_PRESETS[0]);
    }

    /**
     * Constructs a new MarkdownMessages, without initial content.
     * Add that later with {@link #appendMarkdownAsync(String)}.
     *
     * @param name the name of the user
     * @param avatarColor the avatar color
     */
    public MarkdownMessage(String name, Color avatarColor) {
        this(name, LocalDateTime.now(), avatarColor);
    }

    /**
     * Constructs a new MarkdownMessages, with initial content.
     * You can add more text later with {@link #appendMarkdownAsync(String)}.
     *
     *
     * @param markdown the initial content as markdown formatter text
     * @param name the name of the user
     * @param avatarColor the color of the avatar
     */
    public MarkdownMessage(String markdown, String name, Color avatarColor) {
        this(name, LocalDateTime.now(), avatarColor);
        appendMarkdown(markdown);
    }

    /**
     * Constructs a new MarkdownMessages, with initial content.
     * You can add more text later with {@link #appendMarkdownAsync(String)}.
     *
     *
     * @param markdown the initial content as markdown formatter text
     * @param name the name of the user
     */
    public MarkdownMessage(String markdown, String name) {
        this(name, LocalDateTime.now(), Color.AVATAR_PRESETS[name.hashCode()%Color.AVATAR_PRESETS.length]);
        appendMarkdown(markdown);
    }

    /**
     * Constructs a new MarkdownMessages, with initial content.
     * You can add more text later with {@link #appendMarkdownAsync(String)}.
     *
     * @param markdown the initial content as markdown formatted text
     * @param name the name of the user
     * @param timestamp the timestamp of the message
     */
    public MarkdownMessage(String markdown, String name, LocalDateTime timestamp) {
        this(name, timestamp, null);
        String html = getMdRenderer().render(getMdParser().parse(markdown));
        appendHtml(html);
        this.markdown = markdown;
        this.previousHtml = html;
    }

    public void setAvatarColor(Color color) {
        getElement().getStyle().set("--vaadin-avatar-user-color", color.cssColorCode);
        // remove the once set by constructor && ensure the flag making it use
        getElement().executeJs("$0.querySelector('vaadin-avatar').style.setProperty('--vaadin-avatar-user-color', null);$0.querySelector('vaadin-avatar').setAttribute('has-color-index', true);");
    }

    public void setUserColorIndex(int index) {
        getElement().setProperty("userColorIndex", index);
    }

    private void appendHtml(String html) {
        getElement().executeJs("""
                if(this.curHtml) {
                    this.curHtml = this.curHtml + $0;
                } else {
                    this.curHtml = $0;
                }
                $1.innerHTML = this.curHtml;
                """, html, content);
    }
    private void appendHtml(String html, int replaceFrom) {
        getElement().executeJs("""
                this.curHtml = this.curHtml.substring(0, $2) + $0; 
                $1.innerHTML = this.curHtml;
                """, html, content, replaceFrom);
    }

    protected HtmlRenderer getMdRenderer() {
        if (renderer == null) {
            renderer = HtmlRenderer.builder().build();
        }
        return renderer;
    }

    protected Parser getMdParser() {
        if (parser == null) {
            MutableDataSet options = new MutableDataSet();
            parser = Parser.builder(options).build();
        }
        return parser;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        this.ui = attachEvent.getUI();
    }

    public UI getUi() {
        if(ui == null) {
            // fallback, but not 100% thread safe, thanks to all involved :-)
            ui = ui.getUI().orElseGet(() -> UI.getCurrent());
        }
        return ui;
    }

    /**
     * Directly adds markdown formatted text to message part.
     * Note, that this method should not be called from any other but
     * Vaadin UI thread. Consider using the {@link #appendMarkdownAsync(String)}
     * version in case the new text is coming from a background thread.
     *
     * @param markdownSnippet the new markdown formatted text snippet
     */
    public void appendMarkdown(String markdownSnippet) {
        appendMarkdown(markdownSnippet, false);
    }

    /**
     * Adds markdown formatted text to message part. This method takes care
     * of synchronization with {@link UI#access(Command)}, so it is safe to call
     * this directly from a background thread.
     * 
     * @param markdownSnippet the new markdown formatted text snippet
     */
    public void appendMarkdownAsync(String markdownSnippet) {
        appendMarkdown(markdownSnippet, true);
    }

    protected void appendMarkdown(String markdownSnippet, boolean uiAccess) {
        if(markdown == null) {
            markdown = markdownSnippet;
        } else {
            markdown += markdownSnippet;
        }
        String html = getMdRenderer().render(getMdParser().parse(markdown));
        Command c;
        if(previousHtml == null) {
            c = () -> appendHtml(html);
        } else {
            String commonPrefix = StringUtils.getCommonPrefix(html, previousHtml);
            int startOfNew = commonPrefix.length();
            String newPart = html.substring(startOfNew);
            c  = () -> {
                appendHtml(newPart, startOfNew);
                doAutoScroll();
            };
        }
        previousHtml = html;
        if(uiAccess) {
            getUi().access(c);
        } else {
            c.execute();
        }

    }

    public boolean isAutoScroll() {
        return autoScroll;
    }

    public void setAutoScroll(boolean autoScroll) {
        this.autoScroll = autoScroll;
    }

    protected void doAutoScroll() {
        if(autoScroll) {
            scrollHelper.executeJs("""
                if(this.scrollIntoViewIfNeeded) {
                    this.scrollIntoViewIfNeeded();
                } else {
                    // FF
                    this.scrollIntoView();
                }
            """);
        }
    }

}
