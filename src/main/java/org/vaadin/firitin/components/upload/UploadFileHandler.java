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
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.shared.SlotUtils;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.internal.JsonSerializer;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.streams.ElementRequestHandler;
import com.vaadin.flow.shared.Registration;
import elemental.json.JsonObject;
import elemental.json.JsonType;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasEnabled;
import org.vaadin.firitin.fluency.ui.FluentHasSize;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * A vaadin-upload component that just passes the input stream (and name and
 * mime type) of the uploaded file for the developer to handle (constructor
 * parameter). This is essentially Upload component as it should be implemented.
 * More context from
 * https://vaadin.com/blog/uploads-and-downloads-inputs-and-outputs
 * <p>
 * Note, that the FileHandler you write is not executed in the UI thread. If you
 * want to modify the UI from it, by sure to use UI.access to handle locking
 * properly.
 *
 * @author mstahv
 */
// TODO figure out how to get rid of this safely, to be able to exclude the non-used artifact
@Uses(Upload.class)
@Tag("vaadin-upload")
public class UploadFileHandler extends Component implements FluentComponent<UploadFileHandler>, FluentHasStyle<UploadFileHandler>, FluentHasSize<UploadFileHandler>, FluentHasEnabled<UploadFileHandler> {

    private UploadI18N i18n;
    private int maxFiles = 1;

    private boolean splitToChunks = false; // if true, the file is split to chunks of maxChunkSize bytes, and uploaded in multiple requests
    private int maxChunkSize = 1024 * 1024; // 1MB, default chunk size for uploads, this usually goes through front proxies etc

    /**
     * Configures the component to split the file to chunks of default size (1MB) by
     * default and upload them one by one. Chunks are combined on the server, so that
     * the API user does not need to care about the chunking at all. Combining chunsk
     * on server side is done with PipedInputStream and PipedOutputStream, so there is
     * a tiny buffer 1kb and possibly a thread utilizing it, but otherwise the resource
     * overhead ought to be minimal.
     * <p>
     *     Note, that the component will implicitly try sending in chunks if the upload
     *     fails with status code 413 (Request Entity Too Large), which is a common error
     *     when the front proxy (e.g. nginx) has a small limit.
     * </p>
     *
     * @return The component for further configuration
     * @deprecated This is currently very little tested feature. Please provide all feedback you can if there are issues,
     * so that I can improve this.
     */
    @Deprecated(forRemoval = false)
    public UploadFileHandler chunked() {
        this.splitToChunks = true;
        return this;
    }

    /**
     * Configures the component to split the file to chunks of given size and
     * upload them one by one.
     *
     * @param maxChunkSize the maximum size of a chunk in bytes, default is 1MB
     * @return The component for further configuration
     * @see #chunked()
     *
     * @deprecated This is currently very little tested feature.
     */
    @Deprecated(forRemoval = false)
    public UploadFileHandler withChunkSize(int maxChunkSize) {
        this.splitToChunks = true;
        this.maxChunkSize = maxChunkSize;
        return this;
    }

    @FunctionalInterface
    public interface FileHandler extends Serializable {

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

    /**
     * A collection of metadata about the uploaded files.
     *
     * @param fileName the name of the file in users device
     * @param mimeType the mime type parsed from the file name
     * @param contentLenght the length of the file in bytes
     * @param folderPath the full path and filename within the dropped folder,
     *                   if available (only when a folder is dropped or
     *                   {@link #chooseFolders()} is used). The path starts with
     *                   a slash and is relative to the dropped folder.
     */
    public record FileDetails(String fileName, String mimeType, long contentLenght, String folderPath) {

    }

    /**
     * An interface accepting file uploads.
     */
    @FunctionalInterface
    public interface CallbackFileHandler extends Serializable {

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
         * @param metaData details about the file being updated
         * @return a task to be executed later in UI thread once the file upload
         * is completed
         * @throws java.io.IOException like you always do with streams
         */
        public Command handleFile(InputStream content, FileDetails metaData) throws IOException;
    }

    protected final CallbackFileHandler fileHandler;
    private FileRequestHandler frh;
    private boolean clearAutomatically = true;
    private UI ui;

    private int maxConcurrentUploads = 1;

    /**
     * Creates a basic FileUploadHandler with provided FileHandler
     * 
     * @param fileHandler the handler that does something with the uploaded file
     */
    public UploadFileHandler(FileHandler fileHandler) {
        this((InputStream content, FileDetails fmd) -> {
            fileHandler.handleFile(content, fmd.fileName(), fmd.mimeType());
            return () -> {
            };
        });
    }

    /**
     * Creates a FileUploadHandler with provided CallbackFileHandler. The
     * Command returned by the handler is executed in UI after the file has been
     * handled.
     * 
     * @param fileHandler the handler that does something with the uploaded file
     */
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
        getElement().setAttribute("target", frh);
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
                    const SEND_AS_CHUNKS = $2;
                    const MAX_CHUNK_SIZE = $3;
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
                    this.addEventListener('upload-error', e => {
                        console.error("Upload error for file: " + e.detail.file.name, e.detail.error);
                        const isSafari = /^((?!chrome|android).)*safari/i.test(navigator.userAgent);
                        // http2, safari && xhr, status codes not available for 413 (although one sees it via inspector) !!!
                        const safariIsWeirdWithHttp2AndXhr = isSafari && e.detail.xhr.status === 0 && e.detail.xhr.responseText === "";
                        // if e.g. front proxy rejects too large file (std error code 413), try sending as chunks
                        if(e.detail.xhr.status === 413 || safariIsWeirdWithHttp2AndXhr) {
                            event.preventDefault(); // prevent the default upload error handling
                            event.stopPropagation();
                            console.warn("Upload failed with status 413, trying to upload as chunks instead.");
                            const file = event.detail.file;
                            const name = encodeURIComponent(file.name);
                            const folderPath = encodeURIComponent(file.webkitRelativePath ? ("/" + file.webkitRelativePath) : file.__folderPath);
                            const cd = 'name=upload;attachment;filename="'+ name + '"' + ';folderPath="' + folderPath + '"';
                            this.__sendAsChunks(file, cd);
                            return;
                        } else {
                            this.queueNext();
                        }
                    });
                    
                    this.addEventListener('files-changed', (event) => {
                        this.queueNext();
                    });
                    
                    // This sends the request without obsolete and somewhat problematic multipart request
                    this.addEventListener("upload-request", e => {
                        e.preventDefault(true); // I'll send this instead!!
                        const file = event.detail.file;
                        const name = encodeURIComponent(file.name);
                        const folderPath = encodeURIComponent(file.webkitRelativePath ? ("/" + file.webkitRelativePath) : file.__folderPath);
                        const cd = 'name=upload;attachment;filename="'+ name + '"' + ';folderPath="' + folderPath + '"';
                        if(SEND_AS_CHUNKS) {
                            // This splits the file to chunks and uploads them one by one
                            this.__sendAsChunks(file, cd);
                        } else {
                            // This mosly relies the default behaviour, just not using multipart request
                            const xhr = event.detail.xhr;
                            xhr.setRequestHeader('Content-Type', file.type);
                            xhr.setRequestHeader('Content-Disposition', cd);
                            xhr.send(file);
                        }
                    });
                    
                    async function uploadChunk(url, chunk, offset, total, cd, retries = 3) {
                      try {
                        return await fetch(url, {
                          method: 'POST',
                          headers: {
                            "Chunk-Offset": offset,
                            "Total-File-Size": total,
                            "Content-Disposition": cd,
                          },
                          body: chunk,
                        });
                      } catch (error) {
                        if (retries > 0) {
                          return await uploadChunk(chunk, retries - 1);
                        } else {
                          console.error('Failed to upload chunk: ', error);
                        }
                      }
                    }
                    
                    this.__sendAsChunks = (file, cd) => {
                        const chunkSize = Math.min(file.size, MAX_CHUNK_SIZE);
                        let offset = 0;
                        file.status = this.__effectiveI18n.uploading.status.connecting;
                        file.uploading = file.indeterminate = true;
                        file.complete = file.abort = file.error = file.held = false;
                        this._renderFileList();
    
                        const ini = Date.now();
                        let stalledId, last;
                
                        const sendNextChunk = () => {
                            if (offset < file.size) {
                                const chunk = file.slice(offset, offset + chunkSize);
                                console.debug("Uploading chunk of size " + chunk.size + " at offset " + offset);
                                uploadChunk(file.uploadTarget, chunk, offset, file.size, cd).then(r => {
                                    if(r.ok) {
                                        console.debug("Chunk uploaded successfully");
                                    } else {
                                        // stop uploading this file
                                        file.error = "Server error: " + r.status + " " + r.statusText;
                                        file.indeterminate = file.status = undefined;
                                        this._renderFileList();
                                        return;
                                    }
                                    offset += chunkSize;

                                    clearTimeout(stalledId);

                                    last = Date.now();
                                    const elapsed = (last - ini) / 1000;
                                    const loaded = offset,
                                      total = file.size,
                                      progress = ~~((loaded / total) * 100);
                                    file.loaded = loaded;
                                    file.progress = progress;
                                    file.indeterminate = loaded <= 0 || loaded >= total;

                                    if (file.error) {
                                      file.indeterminate = file.status = undefined;
                                    } else if (!file.abort) {
                                      if (progress < 100) {
                                        this._setStatus(file, total, loaded, elapsed);
                                        stalledId = setTimeout(() => {
                                          file.status = this.__effectiveI18n.uploading.status.stalled;
                                          this._renderFileList();
                                        }, 2000);
                                      } else {
                                        file.loadedStr = file.totalStr;
                                        file.status = this.__effectiveI18n.uploading.status.processing;
                                      }
                                    }

                                    this._renderFileList();
                                    this.dispatchEvent(new CustomEvent('upload-progress', { detail: { file } }));

                                    sendNextChunk();
                                }).catch(error => {
                                    console.error('Error uploading chunk:', error);
                                    this.dispatchEvent(new CustomEvent('upload-error', {
                                        detail: { file: file, error: error }
                                    }));
                                    this._renderFileList();
                                });
                            } else {
                                // All chunks uploaded, notify the server
                                console.debug("All chunks uploaded for file: " + file.name);
                                file.complete = true; // mark the file as complete
                                this.dispatchEvent(new CustomEvent('upload-success', {
                                    detail: { file: file }
                                }));
                            }
                        };
                        sendNextChunk();
                    }
                    
                    this.__getFilesFromDropEvent = (dropEvent) => {
                      async function getFilesFromEntry(entry) {
                        if (entry.isFile) {
                          return new Promise((resolve) => {
                            // In case of an error, resolve without any files
                            entry.file(resolve, () => resolve([]));
                          });
                        } else if (entry.isDirectory) {
                          const reader = entry.createReader();
                          const entries = await new Promise((resolve) => {
                            // In case of an error, resolve without any files
                            reader.readEntries(resolve, () => resolve([]));
                          });
                          const files = await Promise.all(entries.map(getFilesFromEntry));
                          for (let i = 0; i < files.length; i++) {
                            files[i].__folderPath = entry.fullPath + '/' + files[i].name;
                          }
                          return files.flat();
                        }
                      }
            
                      // In some cases (like dragging attachments from Outlook on Windows), "webkitGetAsEntry"
                      // can return null for "dataTransfer" items. Also, there is no reason to check for
                      // "webkitGetAsEntry" when there are no folders. Therefore, "dataTransfer.files" is used
                      // to handle such cases.
                      const containsFolders = Array.from(dropEvent.dataTransfer.items)
                        .filter((item) => !!item)
                        .filter((item) => typeof item.webkitGetAsEntry === 'function')
                        .map((item) => item.webkitGetAsEntry())
                        .some((entry) => !!entry && entry.isDirectory);
                      if (!containsFolders) {
                        return Promise.resolve(dropEvent.dataTransfer.files ? Array.from(dropEvent.dataTransfer.files) : []);
                      }
            
                      const filePromises = Array.from(dropEvent.dataTransfer.items)
                        .map((item) => item.webkitGetAsEntry())
                        .filter((entry) => !!entry)
                        .map(getFilesFromEntry);
            
                      return Promise.all(filePromises).then((files) => files.flat());
                    };
                """, clearAutomatically, maxConcurrentUploads, splitToChunks, maxChunkSize);

        this.ui = attachEvent.getUI();
        super.onAttach(attachEvent);

        // Element state is not persisted across attach/detach
        if (this.i18n != null) {
            setI18nWithJS();
        }

    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        ui = null;
        super.onDetach(detachEvent);
    }

    /**
     *
     * @param maxFiles the number of files allowed to be uploaded at once
     * @return The component for further configuration
     */
    public UploadFileHandler withMaxFiles(int maxFiles) {
        this.maxFiles = maxFiles;
        this.getElement().setProperty("maxFiles", (double) maxFiles);
        return this;
    }

    private FileDetails activeUpload;
    private long bytesRead = 0;

    PipedOutputStream pos;
    PipedInputStream pis;

    private class FileRequestHandler implements ElementRequestHandler {
        @Override
        public void handleRequest(VaadinRequest request, VaadinResponse response, VaadinSession session, Element owner) throws IOException {
            String cl = request.getHeader("Content-Length");
            String cd = request.getHeader("Content-Disposition");
            String contentType = request.getHeader("Content-Type");
            String chunkOffset = request.getHeader("Chunk-Offset");
            String totalSize = request.getHeader("Total-File-Size");
            String folderPath = null;
            // name=upload;attachment;filename="text-on-level1.txt";folderPath="/folder to upload/text-on-level1.txt"
            String name = cd.split(";")[2].split("=")[1].substring(1);
            name = name.substring(0, name.indexOf("\""));
            name = URLDecoder.decode(name, "UTF-8");
            // if folderPath is provided, we can use it to the full path within the dropped folder
            if (cd.contains("folderPath")) {
                folderPath = cd.split(";")[3].split("=")[1].substring(1);
                folderPath = URLDecoder.decode(folderPath.substring(0, folderPath.indexOf("\"")), "UTF-8");
                if("undefined".equals(folderPath)) { // paths start with / anyways, so even undefined as foldername is ok
                    folderPath = null; // no folder path provided
                }
            }
            long fileSize = (totalSize == null ) ? Long.parseLong(cl) : Long.parseLong(totalSize);
            FileDetails metaData = new FileDetails(name, contentType, fileSize, folderPath);
            if(chunkOffset != null) {
                // This is a chunked upload, so we need to handle it differently
                long offset = Long.parseLong(chunkOffset);
                if(offset == 0) {
                    // Prepare the file handler for a new file and save for later
                    if(activeUpload != null) {
                        throw new IllegalStateException("Already uploading a file, cannot start a new one!");
                    }
                    activeUpload = metaData;
                    bytesRead = 0l;
                    pos = new PipedOutputStream();
                    pis = new PipedInputStream(pos);

                    // start streaming the file to the handler in a separate thread

                    // Using Executor provided by Vaadin Service, e.g. in Spring Boot this ends
                    // up using the Spring Boot's AsyncExecutor, which can be easily configured
                    // utilizing Virtual Threads, etc. 🤓
                    Executor executor = session.getService().getExecutor();
                    executor.execute(() -> {
                        try {
                            Command command = fileHandler.handleFile(pis, metaData);
                            ui.access(command);
                            pis.close();
                        } catch (Exception e) {
                            try {
                                pis.close();
                                pos.close();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    // verify that we are still uploading the same file
                    if(!metaData.equals(activeUpload)) {
                        throw new IllegalStateException("Cannot upload chunk for a different file than the one started earlier! " +
                                "Expected: " + activeUpload + ", but got: " + metaData);
                    }
                    if(offset != bytesRead) {
                        throw new IllegalStateException("Chunk offset is not correct! Expected: " + bytesRead + ", but got: " + offset);
                    }
                }

                // continue streaming...
                InputStream content = request.getInputStream();
                content.transferTo(pos);
                bytesRead += Long.parseLong(cl);

                // close if this is the last chunk
                if(bytesRead < fileSize) {
                    // TODO could e.g. fire some middle event here if needed 🤷‍♂️
                } else {
                    // this is the last chunk, so we close the streams
                    activeUpload = null; // reset the active upload
                    pos.close();
                }
            } else {
                Command cb = fileHandler.handleFile(request.getInputStream(), metaData);
                if (cb != null) {
                    ui.access(cb);
                }
            }
            response.setStatus(200);
            response.getWriter().println("OK");  // Viritin approves
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
     * Sets the maximum number of server connections this upload uses if
     * multiple files are chosen.
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
     * @param acceptedFileTypes the allowed file types to be uploaded, or
     * <code>null</code> to clear any restrictions
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
     * @param label the label to show for the users when it's possible drop
     * files, or <code>null</code> to reset to the default label
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
     * @param icon the label icon to show for the users when it's possible to
     * drop files, or <code>null</code> to reset to the default icon
     */
    public void setDropLabelIcon(Component icon) {
        SlotUtils.setSlot(this, "drop-label-icon", icon);
    }

    public UploadFileHandler withDropLabelIcon(Component icon) {
        setDropLabelIcon(icon);
        return this;
    }

    /**
     * Clicking on the upload component opeens a dialog for choosing folders instead of files.
     * Note, that with drag and drop, you can still drop both files and folders.
     *
     * @return this for further configuration
     */
    public UploadFileHandler chooseFolders() {
        allowMultiple();
        getElement().executeJs("""
            this.shadowRoot.querySelector("input").webkitdirectory = true;
        """);
        return this;
    }

    /**
     * Set the internationalization properties for this component.
     *
     * @param i18n the internationalized properties, not <code>null</code>
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

    @Override
    public void onEnabledStateChanged(boolean enabled) {
        super.onEnabledStateChanged(enabled);
        if (!enabled) {
            int origMax = maxFiles;
            withMaxFiles(0);
            maxFiles = origMax;
        } else {
            withMaxFiles(maxFiles);
        }

    }
}
