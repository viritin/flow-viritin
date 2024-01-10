/*
 * Copyright 2024 Viritin.
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
import com.vaadin.flow.component.shared.SlotUtils;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.internal.JsonSerializer;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.RequestHandler;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import elemental.json.JsonObject;
import elemental.json.JsonType;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasSize;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * A vaadin-upload component that just passes the input stream (and name and mime
 * type) of the uploaded file for the developer to handle (constructor parameter).
 * This is essentially Upload component as it should be implemented. More context from
 * https://vaadin.com/blog/uploads-and-downloads-inputs-and-outputs
 * <p>
 * Note, that the FileHandler you write is not executed in the UI thread. If you
 * want to modify the UI from it, by sure to use UI.access to handle locking
 * properly.
 *
 * @author mstahv
 */
@Tag("vaadin-upload")
public class UploadFileHandler extends Component implements FluentComponent<UploadFileHandler>, FluentHasStyle<UploadFileHandler>, FluentHasSize<UploadFileHandler> {

    private UploadI18N i18n;

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
    
    @FunctionalInterface
    public interface CallbackFileHandler {

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
         * @return a task to be executed later in UI thread once the file upload
         * is completed
         */
        public Command handleFile(InputStream content, String fileName, String mimeType);
    }


    protected final CallbackFileHandler fileHandler;
    private FileRequestHandler frh;
    private boolean clearAutomatically = true;
    private UI ui;

    private int maxConcurrentUploads = 1;

    public UploadFileHandler(FileHandler fileHandler) {
        this((InputStream content, String fileName, String mimeType) -> {
            fileHandler.handleFile(content, fileName, mimeType);
            return () -> {};
        });
    }

    public UploadFileHandler(CallbackFileHandler fileHandler) {
        this.fileHandler = fileHandler;
        withAllowMultiple(false);
        // dummy listener, makes component visit the server after upload,
        // in case no push configured
        addUploadSucceededListener(e -> {
        });
    }

    /**
     * Clears the uploaded files shown in the component.
     */
    public void clearFiles() {
        getElement().executeJs("this.files = [];");
    }

    public UploadFileHandler allowMultiple() {
        return withAllowMultiple(true);
    }

    /**
     * Configures the component to allow multiple files to be selected at once.
     *
     * @param allowMultiple true to allow multiple files
     * @return The component for further configuration
     */
    public UploadFileHandler withAllowMultiple(boolean allowMultiple) {
        if (allowMultiple) {
            withMaxFiles(Integer.MAX_VALUE);
        } else {
            withMaxFiles(1);
        }
        return this;
    }

    /**
     * Configures the component to allow drag and dropping of files on selected
     * devices (mainly desktop browsers).
     *
     * @param enableDragAndDrop true to allow DnD
     * @return The component for further configuration
     */
    public UploadFileHandler withDragAndDrop(boolean enableDragAndDrop) {
        if (enableDragAndDrop) {
            getElement().removeAttribute("nodrop");
        } else {
            getElement().setAttribute("nodrop", true);
        }
        return this;
    }

    /**
     *
     * @param clear true if the upload queue should be cleaned once all uploads
     * are done.
     * @return The component for further conf
     */
    public UploadFileHandler withClearAutomatically(boolean clear) {
        this.clearAutomatically = clear;
        return this;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        this.frh = new FileRequestHandler();
        getElement().setAttribute("target", "./" + frh.id);
        attachEvent.getSession().addRequestHandler(frh);

        getElement().executeJs("""
            // override default dragover so that it works
            this.addEventListener("dragover", event => {
                event.stopPropagation();
                event.preventDefault();
                if (!this.nodrop && !this._dragover) {
                    let containsInvalid = false;
                    let numberOfFiles = 0;
                    const re = this.__acceptRegexp;
                    for (const item of event.dataTransfer.items) {
                        const acceptedType = (re == null) || re.test(item.type);
                        if(acceptedType && item.kind == "file") {
                            numberOfFiles++;
                        } else {
                            containsInvalid = true;
                        }
                    }
                    if(!containsInvalid && (this.files.length + numberOfFiles) <= this.maxFiles) {
                        this._dragoverValid = !this.maxFilesReached;
                        this._dragover = true;
                    }
                }
                event.dataTransfer.dropEffect = !this._dragoverValid || this.nodrop ? 'none' : 'copy';
            }, true); // bubling phase as no idea how to override default handler by default
            
            // avoid the default auto upload behaviour
            // that immediately opens xhr for each file
            this.noAuto = true;
            const CLEAR = $0;
            const MAX_CONNECTIONS = $1;
            this.queueNext = () => {
                const numConnections = this.files.filter(file => file.uploading).length;
                if(numConnections < MAX_CONNECTIONS) {
                // reverse to pick next in selection order
                    const nextFileToUpload = this.files.slice().reverse().find(file => file.held)
                    if (nextFileToUpload) {
                        this.uploadFiles(nextFileToUpload)
                    }
                }
            }
            
            // start uploading next file in queue when a file is successfully uploaded
            this.addEventListener('upload-success', e => {
                if(CLEAR) {
                    const index = this.files.indexOf(e.detail.file);
                    if (index > -1) {
                        this._removeFile(e.detail.file);
                    }
                }
                this.queueNext();
            });
    
            // start uploading next file in queue also when there is an error when uploading the file
            this.addEventListener('upload-error', () => {
                this.queueNext();
            });
    
            this.addEventListener('files-changed', (event) => {
                this.queueNext();
            });
            
            // This sends the request without obsolete and somewhat problematic multipart request
            this.addEventListener("upload-request", e => {
                e.preventDefault(true); // I'll send this instead!!
                const xhr = event.detail.xhr;
                const file = event.detail.file;
                const name = encodeURIComponent(file.name);
                xhr.setRequestHeader('Content-Type', file.type);
                xhr.setRequestHeader('Content-Disposition', 'attachment; filename="' + name + '"');
                xhr.send(file);
            });
        """, clearAutomatically, maxConcurrentUploads);

        this.ui = attachEvent.getUI();
        super.onAttach(attachEvent);

        // Element state is not persisted across attach/detach
        if (this.i18n != null) {
            setI18nWithJS();
        }

    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        detachEvent.getSession().removeRequestHandler(frh);
        ui = null;
        super.onDetach(detachEvent);
    }

    /**
     *
     * @param maxFiles the number of files allowed to be uploaded at once
     * @return The component for further configuration
     */
    public UploadFileHandler withMaxFiles(int maxFiles) {
        this.getElement().setProperty("maxFiles", (double) maxFiles);
        return this;
    }

    private class FileRequestHandler implements RequestHandler {

        private final String id;

        private List<String> files = new LinkedList<>();

        FileRequestHandler() {
            this.id = UUID.randomUUID().toString();
        }

        @Override
        public boolean handleRequest(VaadinSession session, VaadinRequest request, VaadinResponse response) throws IOException {
            String contextPath = request.getContextPath();
            String pathInfo = request.getPathInfo();
            if (pathInfo.endsWith(id)) {
                // Vaadin's StreamReceiver & friends has this odd
                // inversion of streams, thus handle here
                // TODO figure out if content type or name needs some sanitation
                String contentType = request.getHeader("Content-Type");
                String cd = request.getHeader("Content-Disposition");
                String name = cd.split(";")[1].split("=")[1].substring(1);
                name = name.substring(0, name.length() - 1);
                name = URLDecoder.decode(name, "UTF-8");
                Command cb = fileHandler.handleFile(request.getInputStream(), name, contentType);
                ui.access(cb);
                response.setStatus(200);
                response.getWriter().println("OK");  // Viritin approves
                return true;
            }
            return false;
        }

    }

    public Registration addUploadSucceededListener(ComponentEventListener<UploadSucceededEvent> listener) {
        return addListener(UploadSucceededEvent.class, listener);
    }

    /**
     * Event fired after succesful uploads.
     */
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

    /**
     * Sets the maximum number of server connections this upload uses
     * if multiple files are chosen.
     *
     * @param maxConcurrentUploads the number of maximum connections for upload
     */
    public void setMaxConcurrentUploads(int maxConcurrentUploads) {
        this.maxConcurrentUploads = maxConcurrentUploads;
    }

    /**
     * Set the component as the actionable button inside the upload component,
     * that opens the dialog for choosing the files to be upload.
     *
     * @param button the component to be clicked by the user to open the dialog,
     * or <code>null</code> to reset to the default button
     */
    public void setUploadButton(Component button) {
        SlotUtils.setSlot(this, "add-button", button);
    }
    

    /**
     * Set the component as the actionable button inside the upload component,
     * that opens the dialog for choosing the files to be upload.
     *
     * @param button the component to be clicked by the user to open the dialog,
     * or <code>null</code> to reset to the default button
     * @return this for further config
     */
    public UploadFileHandler withUploadButton(Component button) {
        setUploadButton(button);
        return this;
    }

    /**
     * Specify the types of files that the server accepts. Syntax: a MIME type
     * pattern (wildcards are allowed) or file extensions. Notice that MIME
     * types are widely supported, while file extensions are only implemented in
     * certain browsers, so it should be avoided.
     * <p>
     * Example: <code>"video/*","image/tiff"</code> or
     * <code>".pdf","audio/mp3"</code>
     *
     * @param acceptedFileTypes
     *            the allowed file types to be uploaded, or <code>null</code> to
     *            clear any restrictions
     */
    public void setAcceptedFileTypes(String... acceptedFileTypes) {
        String accepted = "";
        if (acceptedFileTypes != null) {
            accepted = String.join(",", acceptedFileTypes);
        }
        getElement().setProperty("accept", accepted);
    }

    public UploadFileHandler withAcceptedFileTypes(String... acceptedFileTypes) {
        setAcceptedFileTypes(acceptedFileTypes);
        return this;
    }

    /**
     * Set the component to show as a message to the user to drop files in the
     * upload component. Despite of the name, the label can be any component.
     *
     * @param label
     *            the label to show for the users when it's possible drop files,
     *            or <code>null</code> to reset to the default label
     */
    public void setDropLabel(Component label) {
        SlotUtils.setSlot(this, "drop-label", label);
    }

    public UploadFileHandler withDropLabel(Component label) {
        setDropLabel(label);
        return this;
    }

    /**
     * Set the component to show as the drop label icon. The icon is visible
     * when the user can drop files to this upload component. Despite of the
     * name, the drop label icon can be any component.
     *
     * @param icon
     *            the label icon to show for the users when it's possible to
     *            drop files, or <code>null</code> to reset to the default icon
     */
    public void setDropLabelIcon(Component icon) {
        SlotUtils.setSlot(this, "drop-label-icon", icon);
    }

    public UploadFileHandler withDropLabelIcon(Component icon) {
        setDropLabelIcon(icon);
        return this;
    }

    /**
     * Set the internationalization properties for this component.
     *
     * @param i18n
     *            the internationalized properties, not <code>null</code>
     */
    public void setI18n(UploadI18N i18n) {
        Objects.requireNonNull(i18n,
                "The I18N properties object should not be null");
        this.i18n = i18n;

        runBeforeClientResponse(ui -> {
            if (i18n == this.i18n) {
                setI18nWithJS();
            }
        });
    }

    private void setI18nWithJS() {
        JsonObject i18nJson = (JsonObject) JsonSerializer.toJson(this.i18n);

        // Remove null values so that we don't overwrite existing WC
        // translations with empty ones
        deeplyRemoveNullValuesFromJsonObject(i18nJson);

        // Assign new I18N object to WC, by deeply merging the existing
        // WC I18N, and the values from the new UploadI18N instance,
        // into an empty object
        getElement().executeJs(
                "const dropFiles = Object.assign({}, this.i18n.dropFiles, $0.dropFiles);"
                        + "const addFiles = Object.assign({}, this.i18n.addFiles, $0.addFiles);"
                        + "const error = Object.assign({}, this.i18n.error, $0.error);"
                        + "const uploadingStatus = Object.assign({}, this.i18n.uploading.status, $0.uploading && $0.uploading.status);"
                        + "const uploadingRemainingTime = Object.assign({}, this.i18n.uploading.remainingTime, $0.uploading && $0.uploading.remainingTime);"
                        + "const uploadingError = Object.assign({}, this.i18n.uploading.error, $0.uploading && $0.uploading.error);"
                        + "const uploading = {status: uploadingStatus,"
                        + "  remainingTime: uploadingRemainingTime,"
                        + "  error: uploadingError};"
                        + "const units = $0.units || this.i18n.units;"
                        + "this.i18n = Object.assign({}, this.i18n, $0, {"
                        + "  addFiles: addFiles,  dropFiles: dropFiles,"
                        + "  uploading: uploading, units: units});",
                i18nJson);
    }

    private void deeplyRemoveNullValuesFromJsonObject(JsonObject jsonObject) {
        for (String key : jsonObject.keys()) {
            if (jsonObject.get(key).getType() == JsonType.OBJECT) {
                deeplyRemoveNullValuesFromJsonObject(jsonObject.get(key));
            } else if (jsonObject.get(key).getType() == JsonType.NULL) {
                jsonObject.remove(key);
            }
        }
    }

    void runBeforeClientResponse(SerializableConsumer<UI> command) {
        getElement().getNode().runWhenAttached(ui -> ui
                .beforeClientResponse(this, context -> command.accept(ui)));
    }

}
