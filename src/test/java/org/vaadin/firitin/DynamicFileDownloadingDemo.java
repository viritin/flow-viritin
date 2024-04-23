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

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinSession;
import org.vaadin.firitin.components.DynamicFileDownloader;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mstahv
 */
@Route
public class DynamicFileDownloadingDemo extends VerticalLayout {

    private boolean cancelled = false;

    private DynamicFileDownloader actaulButtonLikeDownloadButton = null;

    private DynamicFileDownloader downloadThatNotifiesWhenReady;

    public DynamicFileDownloadingDemo() {

        DynamicFileDownloader downloadButton = new DynamicFileDownloader("Download foobar.txt",
        outputStream -> {
                outputStream.write("HelloWorld".getBytes());
        });
        
        downloadButton.setTarget("_new");
        
        add(downloadButton);

        add(new Button("Toggle attached", e->{
            if(downloadButton.isAttached()) {
                remove(downloadButton);
            } else {
                add(downloadButton);
            }
        }));

        // This generates download with VaadinIcons.DOWNLOAD icon
        DynamicFileDownloader veryBasic = new DynamicFileDownloader(
                outputStream -> outputStream.write("content".getBytes()));
        veryBasic.withTooltip("Download a file...");
        add(veryBasic);

        DynamicFileDownloader downloadFromIcon = new DynamicFileDownloader(
                // in theory any component should do here, but button/plain icon etc are appropriate
                VaadinIcon.DROP.create(),
                "foobaröä.txt", // static default name for generated files
                outputStream -> {
                    try {
                        outputStream.write("HelloWorld".getBytes());
                    } catch (IOException ex) {
                        Logger.getLogger(DynamicFileDownloadingDemo.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
        add(downloadFromIcon);
        
        DynamicFileDownloader downloadButton2 = new DynamicFileDownloader("Download file with timestamp in name",
            outputStream -> {
                outputStream.write("HelloWorld".getBytes());
        }).withFileNameGenerator(r -> {
            // This is called from Vaadin RequestHandler, before the
            // request body is written. Request is the only parameter, but
            // you can also access e.g. VaadinSession with getCurrent()
            // or add custom headers to file download like here
            VaadinRequest.getCurrent().setAttribute("foo", "bar");
            // and do the actual task, return the filename
            return LocalDateTime.now() + "foobaröä.txt";
        }).withContentTypeGenerator(() -> "text/plain");
        
        add(downloadButton2);


        UI ui = UI.getCurrent();
        ui.setPollInterval(500); // simulate Push, not needed if using Push
        downloadThatNotifiesWhenReady = new DynamicFileDownloader("Download that notifies the UI when finished", "foobar/",
                outputStream -> {
                    outputStream.write("HelloWorld".getBytes());
                }
        );
        downloadThatNotifiesWhenReady.addDownloadFinishedListener(e->{
            Notification.show("Download is now finished");
            // you could do something else here as well, like removing the downloader
            remove(downloadThatNotifiesWhenReady);
        });

        add(downloadThatNotifiesWhenReady);

        // Note, the styling of disabled download (read anchor) is currently broken,
        // ought to be fixed in Vaadin 23
        DynamicFileDownloader disableOnClick = new DynamicFileDownloader("Allow just one download per 10 secs", "foobar.txt",
                outputStream -> {
                    outputStream.write("HelloWorld".getBytes());
                });
        disableOnClick.setDisableOnClick(true);
        disableOnClick.addDownloadFinishedListener(e-> {
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    disableOnClick.getUI().ifPresent(ui -> {
                           ui.access(() -> disableOnClick.setEnabled(true));
                    });
                }
            }.start();
        });
        add(disableOnClick);


        UI.getCurrent().setPollInterval(500);

        actaulButtonLikeDownloadButton = new DynamicFileDownloader("Download foobar.txt (should fail in Chrome)", "foobar.txt",
                outputStream -> {
                        outputStream.write("HelloWorld".getBytes());
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        outputStream.write("HelloWorld".getBytes());
                        if(true)
                            throw new RuntimeException("Die");
                        outputStream.write("HelloWorld".getBytes());

                }).asButton();
        actaulButtonLikeDownloadButton.setDisableOnClick(true);
        actaulButtonLikeDownloadButton.addDownloadFinishedListener(e -> {
            actaulButtonLikeDownloadButton.setEnabled(true);
        });
        actaulButtonLikeDownloadButton.getButton().setIcon(VaadinIcon.DOWNLOAD.create());

        add(actaulButtonLikeDownloadButton);

        DynamicFileDownloader interruptable = new DynamicFileDownloader("Download foobar.txt (interrupt-able)", "foobar.txt",
                outputStream -> {
                    for (int i = 0; i < 10; i++) {
                        if (cancelled) {
                            throw new RuntimeException("Die");
                        }
                        outputStream.write("HelloWorld".getBytes());
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }).asButton();

        Button b = new Button("Cancel file generation");
        b.addClickListener( e -> {
            cancelled = true;
        });
        add(interruptable, b);

        DynamicFileDownloader withError = new DynamicFileDownloader("Download that fails", "foobar.txt",
                outputStream -> {
                    throw new RuntimeException("Fail on purpose!");
                }).asButton();
        
        withError.addDownloadFailedListener(e -> {
            Notification.show(e.getException().getMessage() +" Note, file may have been generated on some browsers. Chrome should not do it.");
        });
        add(withError);
    }


}
