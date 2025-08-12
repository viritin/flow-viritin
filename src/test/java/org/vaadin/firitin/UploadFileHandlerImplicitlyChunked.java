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

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.mutable.MutableInt;
import org.vaadin.firitin.components.upload.UploadFileHandler;

import java.io.IOException;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mstahv
 */
@Route
public class UploadFileHandlerImplicitlyChunked extends VerticalLayout {

    public UploadFileHandlerImplicitlyChunked() {

        add("""
        If UploadFileHandler receives 413 Request Entity Too Large error
        from the server, it will automatically switch to chunked transfer mode. This is typically
        done by the front-proxy, e.g. nginx, which has a default limit of 1 MB. To apply
        chunked mode, use docker compose file from project root ./nginx-proxy/ and then
        access this app server via port 9997. If you for e.g. upload a 20MB file,
        you will see that the upload is done in chunks on the brosers "inspector", 
        but via direct port 9998 there is only one request. For the API user, there is no differrece.
        """);

        MutableInt lineCount = new MutableInt(0);

        UploadFileHandler multiUploadFileHandler = new UploadFileHandler( (content, metadata) -> {
            try {
                        int b = 0;
                        int count = 0;
                        while ((b = content.read()) != -1) {
                            if (b == "\n".getBytes()[0]) {
                                count++;
                                System.out.println("Found a line break in file " + metadata.fileName() + " at " + Instant.now());
                            }
                        }
                        lineCount.add(count);
                        String msg = "Counted " + lineCount + "lines. Last file name " + metadata.fileName() + " folderpath: " + metadata.folderPath();
                        lineCount.setValue(0);
                        return () -> Notification.show(msg);
                    } catch (IOException ex) {
                        Logger.getLogger(UploadFileHandlerImplicitlyChunked.class.getName()).log(Level.SEVERE, null, ex);
                        return () -> Notification.show(ex.getMessage());
                    }
                })
                .allowMultiple();
        add(multiUploadFileHandler);
    }

}
