package org.vaadin.firitin;

import com.vaadin.flow.router.Route;
import in.virit.color.NamedColor;
import org.vaadin.firitin.components.messagelist.MarkdownMessage;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.util.VStyleUtil;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

@Route
public class MarkdownMessageProblemView extends VVerticalLayout {

    static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledFuture;

    public MarkdownMessageProblemView() {
        MarkdownMessage message = new MarkdownMessage("", LocalDateTime.now());
        // Fix weird styling when ChatGPT gives you code snippets
        VStyleUtil.inject("""
         vaadin-message {
            width:100%;
         }
         vaadin-message::part(content) {
             overflow:hidden;
         }
         code, pre {
             background-color: inherit;
         }
         pre {
            overflow: scroll;
            position: relative;
        }
         """);
        message.appendMarkdown("""
        # Appended markdown title
                                
         * List 1
         * List 2
        
        Some more text
                 
        Tässä koodia:

              This is  a long java code line that should be wrapped to multiple lines, but is not and breaks the layout because it becomes so wide it cannot fit on Sami's screen.

        Ja puhutko välillä suomeekin?
        
        Ja puhutko välillä suomeekin?Ja puhutko välillä suomeekin? Ja puhutko välillä suomeekin?Ja puhutko välillä suomeekin?Ja puhutko välillä suomeekin?Ja puhutko välillä suomeekin?Ja puhutko välillä suomeekin?Ja puhutko välillä suomeekin?Ja puhutko välillä suomeekin?Ja puhutko välillä suomeekin?Ja puhutko välillä suomeekin?Ja puhutko välillä suomeekin?Ja puhutko välillä suomeekin?Ja puhutko välillä suomeekin?Ja puhutko välillä suomeekin?Ja puhutko välillä suomeekin?

        | First Header  | Second Header |
        | ------------- | ------------- |
        | Content Cell  | Content Cell  |
        | Content Cell  | Content Cell  |

        """);
        message.setAvatarColor(MarkdownMessage.Color.AVATAR_PRESETS[1]);
        message.setAvatarColor(NamedColor.RED);
        add(message);

    }
}
