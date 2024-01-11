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
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.upload.UploadFileHandler;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mstahv
 */
@Route
public class UploadFileHandler2Example extends VerticalLayout {

    private static final String DELIMITER = ";";

    int count = 0;

    public UploadFileHandler2Example() {

        Paragraph liveLogger = new Paragraph("...");
        UI ui = UI.getCurrent();
        UploadFileHandler uploadFileHandler = new UploadFileHandler(
                (InputStream content, String fileName, String mimeType) -> {
                    System.out.println(LocalTime.now() + " UploadFileHandler passing the inputStream for developer. Thread id:" + Thread.currentThread().getId());
                    System.out.println("Filename: %s, MimeType: %s".formatted(fileName, mimeType));
                    try {
                        long lastUpdate = System.currentTimeMillis();
                        int b = 0;
                        while ((b = content.read()) != -1) {
                            if (b == "\n".getBytes()[0]) {
                                count++;
                                if((System.currentTimeMillis()-lastUpdate) > 200) {
                                    // Modifying UI during handling, to show this
                                    // is possible, see https://stackoverflow.com/questions/75165362/vaadin-flow-upload-component-streaming-upload
                                    int curcount = count;
                                    System.out.println(LocalTime.now() + " counting... (%s, %s)".formatted(curcount, fileName));
                                    ui.access(() -> liveLogger.setText("counting... (%s)".formatted(curcount)));
                                    lastUpdate = System.currentTimeMillis();
                                }
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(UploadFileHandler2Example.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    // You can optionally return a callback that will be executed
                    // in the UI thread
                    return () -> Notification.show("No more data on the stream!");
                }).withFullWidth();

        uploadFileHandler.addUploadSucceededListener(e -> {
            String fileName = e.getFileName();
            Notification.show("File " + fileName + " uploaded! Total count of lines: " + count);
            liveLogger.setText("...");
        });

        Checkbox allowMultiple = new Checkbox("Allow multiple at once");
        allowMultiple.addValueChangeListener(e -> {
            uploadFileHandler.withAllowMultiple(e.getValue());
        });

        Checkbox clearAutomatically = new Checkbox("Clear finished updates automatically");
        clearAutomatically.setValue(true);
        clearAutomatically.addValueChangeListener(e -> {
            uploadFileHandler.withClearAutomatically(e.getValue());
        });

        Checkbox dd = new Checkbox("Allow drag and drop (on selected devices)");
        dd.setValue(true);
        dd.addValueChangeListener(e -> {
            uploadFileHandler.withDragAndDrop(e.getValue());
        });
        Checkbox enabled = new Checkbox("Enabled");
        enabled.setValue(true);
        enabled.addValueChangeListener(e -> {
            uploadFileHandler.setEnabled(e.getValue());
        });

        Button toggleAttached = new VButton("Detach/attach upload")
                .withTooltip("Can be done even during upload 😎, but state of currently uploaded or previously uploaded files and events are lost");
        toggleAttached.addClickListener(e -> {
            if(uploadFileHandler.isAttached()) {
                uploadFileHandler.removeFromParent();
            } else {
                addComponentAtIndex(2, uploadFileHandler);
            }
        });

        add(
                new Paragraph("Counting lines as data streams in. Test the views with reasonably large files with network throttling (e.g. Chrome dev tools)"),
                liveLogger,
                uploadFileHandler,
                allowMultiple, clearAutomatically, dd, enabled, toggleAttached
        );

    }

}
