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
package org.vaadin.firitin.components.upload;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.upload.MultiFileReceiver;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.RequestHandler;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload2.core.FileItemInputIterator;
import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasSize;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * An upload implementation that just pass the input stream (and name and mime
 * type) of the uploaded file for developer to handle.
 * <p>
 * Note, then FileHandler you pass in is not executed in the UI thread. If you
 * want to modify the UI from it, by sure to use UI.access to handle locking
 * properly.
 * <p>
 * Note, all Upload features are not supported (but the lazy developer is not
 * throwing exceptions on all those methods).
 *
 * @author mstahv
 */
@Tag("vaadin-upload")
public class UploadFileHandler extends Component implements FluentComponent<UploadFileHandler>, FluentHasStyle<UploadFileHandler>, FluentHasSize<UploadFileHandler> {

    public UploadFileHandler allowMultiple() {
        return withAllowMultiple(true);
    }

    @FunctionalInterface
    public interface FileHandler {

        /**
         * This method is called by the framework when a new file is being
         * received.
         * <p>
         * You can read the file contents from the given InputStream.
         * <p>
         * Note, that this method is not executed in the UI thread. If you want
         * to modify the UI from it, by sure to use UI.access (and possibly Push
         * annotation) to handle locking properly.
         *
         * @param content the file content
         * @param fileName the name of the file in users device
         * @param mimeType the mime type parsed from the file name
         */
        public void handleFile(InputStream content, String fileName, String mimeType);
    }


    protected final FileHandler fileHandler;
    private FileRequestHandler frh;
    private boolean clearAutomatically = true;
    private UI ui;

    public UploadFileHandler(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
        withAllowMultiple(false);
        // dummy listener, makes component visit the server after upload,
        // in case no push configured
        addUploadSucceededListener(e -> {
            if(clearAutomatically) {
                frh.files.remove(e.getFileName());
                if(frh.files.isEmpty()) {
                    clearFiles();
                }
            }
        });

    }

    public void clearFiles() {
        getElement().executeJs("this.files = [];");
    }

    public UploadFileHandler withAllowMultiple(boolean allowMultiple) {
        if (allowMultiple) {
            withMaxFiles(Integer.MAX_VALUE);
        } else {
            withMaxFiles(1);
        }
        return this;
    }

    public UploadFileHandler withDragAndDrop(boolean enableDragAndDrop) {
        if(enableDragAndDrop) {
            getElement().removeAttribute("nodrop");
        } else {
            getElement().setAttribute("nodrop", true);
        }
        return this;
    }

    public UploadFileHandler withClearAutomatically(boolean clear) {
        this.clearAutomatically = clear;
        return this;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        this.frh = new FileRequestHandler();
        getElement().setAttribute("target", "./" + frh.id);
        attachEvent.getSession().addRequestHandler(frh);
        this.ui = attachEvent.getUI();
        super.onAttach(attachEvent);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        detachEvent.getSession().removeRequestHandler(frh);
        ui = null;
        super.onDetach(detachEvent);
    }

    public UploadFileHandler withMaxFiles(int maxFiles) {
        this.getElement().setProperty("maxFiles", (double) maxFiles);
        return this;
    }

    public class FileRequestHandler implements RequestHandler {

        private final String id;

        private List<String> files = new LinkedList<>();

        public FileRequestHandler() {
            this.id = UUID.randomUUID().toString();
        }


        @Override
        public boolean handleRequest(VaadinSession session, VaadinRequest request, VaadinResponse response) throws IOException {
            if (request.getPathInfo().endsWith(id) && JakartaServletFileUpload.isMultipartContent((HttpServletRequest) request)) {
                // Vaadin's StreamReceiver & friends has this odd
                // inversion of streams, thus handle byself using commons-fileupload
                // TODO handle multipart request and pass to handler

                // Create a new file upload handler
                JakartaServletFileUpload upload = new JakartaServletFileUpload();

                // Parse the request
                FileItemInputIterator itemIterator = upload.getItemIterator((HttpServletRequest) request);
                itemIterator.forEachRemaining(item -> {
                    String name = item.getFieldName();
                    InputStream stream = item.getInputStream();
                    String filename = item.getName();
                    // TODO this should in theory be synchronized 🤔
                    files.add(filename);
                    // Process the input stream
                    fileHandler.handleFile(item.getInputStream(), filename, item.getContentType());
                });

                return true;
            }
            return false;
        }

    }

    public Registration addUploadSucceededListener(ComponentEventListener<UploadSucceededEvent> listener) {
        return addListener(UploadSucceededEvent.class, listener);
    }

    @DomEvent("upload-success")
    public static class UploadSucceededEvent
            extends ComponentEvent<UploadFileHandler> {
        private final String fileName;

        public UploadSucceededEvent(UploadFileHandler source,
                                    boolean fromClient, @EventData("event.detail.file.name") String fileName) {
            super(source, fromClient);
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }
    }

}
