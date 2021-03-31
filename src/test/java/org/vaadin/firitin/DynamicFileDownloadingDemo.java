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

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinRequest;
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

    public DynamicFileDownloadingDemo() {
        
        DynamicFileDownloader downloadButton = new DynamicFileDownloader("Download foobar.txt", "foobar.txt",
        outputStream -> {
            try {
                outputStream.write("HelloWorld".getBytes());
            } catch (IOException ex) {
                Logger.getLogger(DynamicFileDownloadingDemo.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        downloadButton.setTarget("_new");
        
        add(downloadButton);
        
        DynamicFileDownloader downloadButton2 = new DynamicFileDownloader("Downlload file with timestamp in name", "foobar/",
        outputStream -> {
            try {
                outputStream.write("HelloWorld".getBytes());
            } catch (IOException ex) {
                Logger.getLogger(DynamicFileDownloadingDemo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }) {
            @Override
            protected String getFileName(VaadinSession session, VaadinRequest request) {
                return LocalDateTime.now() + "foobar.txt";
            }
        };
        
        add(downloadButton2);

    }
    
    
}
