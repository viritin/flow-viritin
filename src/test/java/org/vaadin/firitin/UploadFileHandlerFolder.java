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
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.upload.UploadFileHandler;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mstahv
 */
@Route
public class UploadFileHandlerFolder extends VerticalLayout {

    private static final String DELIMITER = ";";

    public UploadFileHandlerFolder() {

        Paragraph liveLogger = new Paragraph("...");
        UI ui = UI.getCurrent();

        AtomicInteger lineCount = new AtomicInteger(0);

        UploadFileHandler multiUploadFileHandler = new UploadFileHandler( (content, metadata) ->{
                    try {
                        int b = 0;
                        int count = 0;
                        while ((b = content.read()) != -1) {
                            if (b == "\n".getBytes()[0]) {
                                count++;
                            }
                        }
                        lineCount.addAndGet(count);
                        String msg = "Counted " + lineCount + "lines so far. Last file name " + metadata.fileName() + " folderpath: " + metadata.folderPath();
                        return () -> Notification.show(msg);
                    } catch (IOException ex) {
                        Logger.getLogger(UploadFileHandlerFolder.class.getName()).log(Level.SEVERE, null, ex);
                        return () -> Notification.show(ex.getMessage());
                    }
                })
                .chooseFolders()
                .withUploadButton(new VButton(VaadinIcon.FOLDER, "Choose folder"));
        add(multiUploadFileHandler);
    }

}
