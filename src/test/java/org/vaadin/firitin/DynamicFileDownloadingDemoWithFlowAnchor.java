/*
 * Copyright 2018 Viritin.
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

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AttachmentType;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.DownloadEvent;
import com.vaadin.flow.server.streams.DownloadHandler;
import org.vaadin.firitin.components.button.VButton;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * Vaadin 24.8 and later should make DynamicFileDownloader obsolete. This class
 * contains examples of how to use new core features instead of DynamicFileDownloader.
 * @author mstahv
 */
@Route
public class DynamicFileDownloadingDemoWithFlowAnchor extends VerticalLayout {

    private boolean cancelled = false;

    private Anchor actaulButtonLikeDownloadButton = null;

    private Anchor downloadThatNotifiesWhenReady;

    public DynamicFileDownloadingDemoWithFlowAnchor() {

        Anchor downloadButton = new Anchor((DownloadEvent event) -> {
            event.setFileName("foobar.txt");
            try (OutputStream outputStream = event.getOutputStream()) {
                // Write data to the output stream
                outputStream.write("HelloWorld".getBytes());
            }
            event.getUI().access(() -> { /* UI updates */});
        }, "Download foobar.txt");

        add(downloadButton);

        add(new Button("Toggle attached", e->{
            if(downloadButton.isAttached()) {
                remove(downloadButton);
            } else {
                add(downloadButton);
            }
        }));

        add(new Button("Toggle enabled", e->{
            downloadButton.setEnabled(!downloadButton.isEnabled());
        }));

        // This generates download with VaadinIcons.DOWNLOAD icon
        // Doable, but a bit nasty without constructors and button-like behavior
        Anchor veryBasic = new Anchor(){{
            setHref((DownloadEvent event) -> {
                event.getOutputStream().write("content".getBytes());
            }, AttachmentType.DOWNLOAD); // This should be the default!!
            add(new VButton(VaadinIcon.DOWNLOAD).withTooltip("Download a file..."));
        }};
        add(veryBasic);

        Anchor downloadFromIcon = new Anchor(){{
            setHref((DownloadEvent event) -> {
                event.setFileName("foobaröä.txt");
                event.getOutputStream().write("HelloWorld".getBytes());
            }, AttachmentType.DOWNLOAD);
            add(VaadinIcon.DROP.create());
        }};
        add(downloadFromIcon);

        Anchor downloadButton2 = new Anchor((DownloadEvent event) -> {
            event.setFileName(LocalDateTime.now() + "foobaröä.txt");
            event.getRequest().setAttribute("foo", "bar");
            event.setContentType("text/plain");
            event.getOutputStream().write("HelloWorld".getBytes());
        }, "Download file with timestamp in name");

        add(downloadButton2);

        add(new Button("Ping", e-> Notification.show("Pong")));

        downloadThatNotifiesWhenReady = new Anchor((DownloadEvent event) -> {
            event.getUI().access(() -> {
                // Note, this is not executed without Push or Polling, on by default in Viritin test app
                Notification.show("Download is now started, but it might take time (artificial slowness for demo).");
            });
            event.setFileName("foobar.txt");
            OutputStream outputStream = event.getOutputStream();
            outputStream.write("HelloWorld".getBytes());
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            outputStream.write("Remainings...".getBytes());
            event.getUI().access(() -> {
                // Note, this is not executed without Push or Polling
                Notification.show("Download is now finished");
                // you could do something else here as well, like removing the downloader
                remove(downloadThatNotifiesWhenReady);
            });
        }, "Download that notifies the UI when finished");

        /*
         * TODO, figure out if this can be down with Flow Anchor (without push)
         *  TransferProgressListener is kind of for this, but don't know how to hook it to
         *  dynamically generated download and does NOT work without push or polling.
         *  It also complicates usage quite a bit.

        downloadThatNotifiesWhenRead.addDownloadStartedListener(e-> {
            Notification.show("Download is now started, but it might take time (artificial slowness for demo).");
        });
        downloadThatNotifiesWhenReady.addDownloadFinishedListener(e->{
            Notification.show("Download is now finished");
            // you could do something else here as well, like removing the downloader
            remove(downloadThatNotifiesWhenReady);
        });

         */

        add(downloadThatNotifiesWhenReady);

        // No support for "disableOnClick" in Flow Anchor, so this is a workaround
        // Note, this only works with Push or Polling enabled, otherwise the UI is not updated
        var disableOnClick = new Anchor((DownloadHandler) event -> {
            event.getUI().access(() -> {
                // eh..., well, not a problem if one creats a proper custom class...
                ((Anchor) event.getOwningComponent()).setEnabled(false);
            });
            event.getOutputStream().write("HelloWorld".getBytes());
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    event.getUI().access(() -> {
                        // This is executed after 10 seconds, so the download button is enabled again
                        ((Anchor) event.getOwningComponent()).setEnabled(true);
                    });
                }
            }.start();
        }, "Allow just one download per 10 secs");

        add(disableOnClick);


        actaulButtonLikeDownloadButton = new Anchor() {{
            setHref((DownloadHandler) downloadEvent -> {
                OutputStream outputStream = downloadEvent.getOutputStream();
                outputStream.write("HelloWorld".getBytes());
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                outputStream.write("HelloWorld".getBytes());
                if(true)
                    throw new RuntimeException("Die");
                // TODO figure out what happens here. With DynamicFileDownloader on Safari
                // one gets half of the file, but with Flow Anchor it seems to get an error pages
                outputStream.write("HelloWorld".getBytes());
            }, AttachmentType.DOWNLOAD);
            add(new VButton(VaadinIcon.DOWNLOAD).withText("Download foobar.txt (should fail in Chrome)"));
        }};
        // Not supported in Flow Anchor easily
        /*
        actaulButtonLikeDownloadButton.setDisableOnClick(true);
        actaulButtonLikeDownloadButton.addDownloadFinishedListener(e -> {
            actaulButtonLikeDownloadButton.setEnabled(true);
        });
         */

        add(actaulButtonLikeDownloadButton);

        var interruptable = new Anchor((DownloadHandler) downloadEvent -> {
            downloadEvent.setFileName("foobar.txt");
            OutputStream outputStream = downloadEvent.getOutputStream();
            for (int i = 0; i < 10; i++) {
                if (cancelled) {
                    // TODO same as above, should return half generated content vs. error page !?
                    // Is something buffering the output stream? If so, it might eat up a lot of memory?
                    // Chrome handles this "correctly" just shows an error in the downloads dialog
                    // both with Flow Anchor and DynamicFileDownloader
                    throw new RuntimeException("Die");
                }
                outputStream.write("HelloWorld".getBytes());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "Download foobar.txt (interrupt-able)");

        Button b = new Button("Cancel file generation");
        b.addClickListener( e -> {
            cancelled = true;
        });
        add(interruptable, b);

        var withError = new Anchor((DownloadHandler) downloadEvent -> {
            downloadEvent.setFileName("foobar.txt");
            // This will throw an exception and the file will not be generated
            throw new RuntimeException("Fail on purpose!");
        }, "Download that fails");


        // No failed listener in Flow Anchor, so only option is to use Push or Polling
        // and override error handler.
        /*
        withError.addDownloadFailedListener(e -> {
            Notification.show(e.getException().getMessage() +" Note, file may have been generated on some browsers. Chrome should not do it.");
        });
         */
        add(withError);


        var inNewWindow = new Anchor(){{
            add(new Button("\"Download\" a txt file in a new window"));
            setHref((DownloadHandler) downloadEvent -> {
                downloadEvent.setContentType("text/plain");
                // INLINE only works with Firefox, so this is a workaround to make Safari and Chrome
                // to show content in the browser window...
                // When looking at the implementation, not reflected to headers at all, but only affects anchor attributs !??
                // And When looking at the implementation, it seems we don't use UTF-8 encoding for the filename, causes issues with soma non-ASCII characters in filename
                downloadEvent.getResponse().setHeader("Content-Disposition","filename*=UTF-8''" + URLEncoder.encode("generated.txt", StandardCharsets.UTF_8));
                OutputStream outputStream = downloadEvent.getOutputStream();
                // Override the content disposition header, only FF works with INLINE
                outputStream.write("HelloWorld".getBytes());
            }, AttachmentType.INLINE);
            setTarget("_blank");
        }};
        add(inNewWindow);

        var pdfInNewWindow = new Anchor(){{
            setText("\"Download\" a pdf file in a new window");
            setHref((DownloadHandler) downloadEvent -> {
                downloadEvent.setContentType("application/pdf");
                downloadEvent.getResponse().setHeader("Content-Disposition","filename*=UTF-8''" +
                        // NOTE, should do like encodeRfc5987Filename() in DynamicFileDownloader
                        URLEncoder.encode("inlöäöäineشريط.pdf", StandardCharsets.UTF_8));
                OutputStream outputStream = downloadEvent.getOutputStream();
                Files.copy(Path.of("src/test/resources/pdf.pdf"), outputStream);
            }, AttachmentType.INLINE);
            setTarget("_blank");
        }};
        add(pdfInNewWindow);

        if(true) {
            add(new Button("Test UI serialization", event -> {

                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                try {
                    new ObjectOutputStream(bout).writeObject(DynamicFileDownloadingDemoWithFlowAnchor.this);
                    // This seems to be broken in Vaadin 24.8 (maybe earlier), so not using it for now
                    // Throws some UI not found exception 🤯
                    //Notification.show("UI successfully serialized");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }));
        }
    }


}
