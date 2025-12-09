/*
 * Copyright 2022 Viritin.
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
package org.vaadin.firitin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.ui.LoadMode;
import org.vaadin.firitin.components.RichText;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;

import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.vaadin.firitin.MessageListView.executorService;

/**
 * @author mstahv
 */
@Route
public class RichTextExample extends VVerticalLayout {

    private ScheduledFuture<?> scheduledFuture;

    public RichTextExample() {

        add(new H1("RichText examples"));


        add(new RichText().withMarkDown("""
                # RichText using Markdown syntax
                
                *Hello* [Viritin](https://vaadin.com/) users.
                
                This is another paragraph with multiple
                lines belonging to same paragraph.
                
                Table syntax is not supported out of the box (not "standard markdown"), if
                using the flexmark parser on the server (used if on classpath). Can be configured separately
                and also supported by default as using markdown-it on the browser.
                
                | First Header  | Second Header |
                | ------------- | ------------- |
                | Content Cell  | Content Cell  |
                | Content Cell  | Content Cell  |

                """));

        add(new RichText().withSafeHtml("""
                <h1>RichText using HTML syntax</h1>
                
                <p><i>Hello</i> <a href="https://vaadin.com/">Viritin</a> users.</p>
                """));

        // even though adding a large content, RichText don't consume memory
        // in the user session
        add(new RichText(generateRandomAlphabetic(100000)));

        // for this you could call getText and get meaningful return value,
        // but this consumes 100kB of (uncompressed) memory, while users
        // session is active
        add(new RichText().setRichTextAndSaveReference(generateRandomAlphabetic(100000)));


        add(new Button("Try with JS MarkdownIT (default without flexmark dependency)", e -> {
            // classloader global configuration
            RichText.markdownStrategy = new RichText.MarkdownItStrategy();

            add(new RichText().withMarkDown("""
            # Tables supported, but bit more JS loaded
            
            | First Header  | Second Header |
            | ------------- | ------------- |
            | Content Cell  | Content Cell  |
            | Content Cell  | Content Cell  |
            
            """));


        }));


        add(new Button("Subscribe to streaming", event -> {

            // This is default unless flexmark java is on classpath
            RichText.markdownStrategy = new RichText.MarkdownItStrategy();

            RichText richText = new RichText();
            add(richText);

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

                richText.appendMarkDownAsync(token);

                int currentlySent = sent.addAndGet(charsToSend);
                if(currentlySent > md.length()) {
                    scheduledFuture.cancel(true);
                }
            }, 500, 500, TimeUnit.MILLISECONDS);

        }));

    }

    private static String generateRandomAlphabetic(int length) {
        Random random = new Random();
        return random.ints('a', 'z' + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
