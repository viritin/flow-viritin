package org.vaadin.firitin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.messagelist.MarkdownMessage;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Route
public class MessageListView extends VVerticalLayout {

    static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledFuture;

    public MessageListView() {
        MarkdownMessage message = new MarkdownMessage("Hello!\n","John", LocalDateTime.now());
        message.setAvatarColor(MarkdownMessage.Color.AVATAR_PRESETS[1]);
        message.setAvatarColor(new MarkdownMessage.Color("red"));
        add(message);

        add(
                new Button("Append markdown", event -> {
                    message.appendMarkdown("### Appended \n Some more text\n");
                }),
                new Button("Append text", event -> {
                    message.appendText("<h3>Appended</h3><p>Some more text</p>");
                }),
                new Button("Append Markdown", event -> {
                    message.appendMarkdown("""
                            # Appended markdown title
                                                
                             * List 1
                             * List 2
                             
                             Some more text
                            """);
                }),

                new Button("Subscribe to streaming", event -> {
                    String md = """
                    # Appended markdown title
                    
                    So this is [an anchor](https://github.com/mstahv) to resource in the GitHub.
                    
                     * List 1 is pretty long one
                     * List 2
                    
                    Some more text...
                    """;
                    Random r = new Random(0);
                    AtomicInteger sent = new AtomicInteger();

                    scheduledFuture = executorService.scheduleAtFixedRate(() -> {
                        int charsToSend = r.nextInt(5, 10);
                        int start = sent.get();
                        int end = start + charsToSend;
                        if(start + charsToSend > md.length()) {
                            end = md.length();
                        }
                        String token = md.substring(start, end);

                        message.appendMarkdownAsync(token);

                        int currentlySent = sent.addAndGet(charsToSend);
                        if(currentlySent > md.length()) {
                            scheduledFuture.cancel(true);
                        }
                    }, 500, 500, TimeUnit.MILLISECONDS);

                }));

    }
}
