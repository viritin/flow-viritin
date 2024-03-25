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
package org.vaadin.firitin.components;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.shared.HasTooltip;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.dom.DomListenerRegistration;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.RequestHandler;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinSession;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.vaadin.flow.shared.Registration;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasEnabled;
import org.vaadin.firitin.fluency.ui.FluentHasTooltip;

/**
 * An anchor which links to a file whose content is produced dynamically.
 *
 * @author mstahv
 * @see #setFileName(java.lang.String)
 * @see #setFileHandler(com.vaadin.flow.function.SerializableConsumer)
 */
public class DynamicFileDownloader extends Anchor implements 
        FluentComponent<DynamicFileDownloader>, FluentHasEnabled<DynamicFileDownloader>,
        FluentHasTooltip<DynamicFileDownloader> {

    /**
     * Writes the content of the downloaded file to the given output stream.
     */
    @FunctionalInterface
    public interface ContentWriter {

        /**
         * Writes the content of the downloaded file to the given output
         * stream (~ output stream of there response).
         *
         * @param out the output stream to write to
         * @throws IOException if an IO error occurs
         */
        void writeContent(OutputStream out) throws IOException;

    }

    /**
     * Generates name dynamically per request. Override for example to add
     * timestamps to the names of the downloaded files or to configure response
     * headers (executed during download, but before writing the actual response
     * body).
     */
    @FunctionalInterface
    public interface FileNameGenerator {

        /**
         * Creates the filename for the downloaded files.
         *
         * Called by the framework when download is requested by browser, just
         * before the file body is generated.
         *
         * @param request the request object
         * @return the file name to be used in the Content-Disposition header
         */
        String getFileName(VaadinRequest request);
    }

    /**
     * Generates the content of HTTP response header 'Content-Type'.
     * If known, should be set to the MIME type of the content.
     * Otherwise, the 'Content-Type' defaults to 'application/octet-stream'.
     * This indicates content as "arbitrary binary data".
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc2046#section-4.5.1">RFC2046</a>
     */
    @FunctionalInterface
    public interface ContentTypeGenerator {

        /**
         * Used as 'Content-Type' HTTP response header.
         *
         * @return MIME type of the content.
         */
        String getContentType();
    }

    private Button button;
    private DomListenerRegistration disableOnclick;

    /**
     * Makes the download link to be disabled after the first click.
     *
     * @param disableOnClick true to disable the link after the first click
     */
    public void setDisableOnClick(boolean disableOnClick) {
        if (disableOnclick != null) {
            disableOnclick.remove();
        }
        if (disableOnClick) {
            getElement().executeJs("""
            const el = this;
            this.addEventListener("click", e => {
                setTimeout(() => el.removeAttribute("href"), 0);
            });
            """);
            disableOnclick = getElement().addEventListener("click", e -> {
                setEnabled(false);
            });
        }
    }

    /**
     * Event fired when the file download has been streamed to the client.
     */
    public static class DownloadFinishedEvent extends ComponentEvent<DynamicFileDownloader> {

        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source the source component
         * @param fromClient <code>true</code> if the event originated from the
         * client
         */
        public DownloadFinishedEvent(DynamicFileDownloader source, boolean fromClient) {
            super(source, fromClient);
        }

    }

    /**
     * Event fired when the file download fails.
     */
    public static class DownloadFailedEvent extends ComponentEvent<DynamicFileDownloader> {

        private final Exception exception;

        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source the source component
         * @param e the exception
         */
        public DownloadFailedEvent(DynamicFileDownloader source, Exception e) {
            super(source, false);
            this.exception = e;
        }

        /**
         * Gets the exception that caused the download to fail.
         *
         * @return the exception
         */
        public Exception getException() {
            return exception;
        }

    }

    StreamResource resource = new StreamResource("dummy", (InputStreamFactory) () -> new ByteArrayInputStream(new byte[0]));
    FileNameGenerator fileNameGenerator = (r) -> "downloadedFile";
    private ContentTypeGenerator contentTypeGenerator = () -> "application/octet-stream";
    private SerializableConsumer<OutputStream> contentWriter;
    /**
     * The request handler that handles the download request.
     */
    protected RequestHandler requestHandler;

    /**
     * Constructs a basic download link with DOWNLOAD icon from
     * {@link VaadinIcon} as the "text" and default file name.
     *
     * @param writer the callback to generate the contents of the file
     */
    public DynamicFileDownloader(ContentWriter writer) {
        add(new VButton(VaadinIcon.DOWNLOAD.create()));
        setWriter(writer);
    }

    /**
     * Constructs a new download link with given text, static file name and
     * writer.
     *
     * @param linkText the text inside the link
     * @param contentWriter the content writer that generates the actual
     * content.
     */
    public DynamicFileDownloader(String linkText, ContentWriter contentWriter) {
        this();
        setText(linkText);
        setWriter(contentWriter);
    }

    /**
     * Constructs a new download link with given text, static file name and
     * writer.
     *
     * @param linkText the text inside the link
     * @param fileName the file name of produced files
     * @param contentWriter the content writer that generates the actual
     * content.
     */
    public DynamicFileDownloader(String linkText, String fileName, ContentWriter contentWriter) {
        this();
        setText(linkText);
        this.fileNameGenerator = r -> fileName;
        setWriter(contentWriter);
    }

    /**
     * Constructs a download link with given component as the content that
     * ignites the download.
     *
     * @param downloadComponent the component to be clicked by the user to start
     * the download
     * @param fileName the filename of the generated files
     * @param contentWriter the content writer of the generated file
     */
    public DynamicFileDownloader(Component downloadComponent, String fileName, ContentWriter contentWriter) {
        this();
        add(downloadComponent);
        fileNameGenerator = r -> fileName;
        setWriter(contentWriter);
    }

    /**
     * Constructs a download link with given component as the content that
     * ignites the download.
     *
     * @param downloadComponent the component to be clicked by the user to start
     * the download
     * @param contentWriter the content writer of the generated file
     */
    public DynamicFileDownloader(Component downloadComponent, ContentWriter contentWriter) {
        this();
        add(downloadComponent);
        setWriter(contentWriter);
    }

    private void setWriter(ContentWriter contentWriter) {
        this.contentWriter = out -> {
            try {
                contentWriter.writeContent(out);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Empty constructor file downloader. Be sure to call setFileHandler
     * before the component is attached.
     */
    public DynamicFileDownloader() {
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        getElement().setAttribute("fakesr", resource);
        String identifier = resource.getId();
        getElement().executeJs("""
            this.setAttribute("href",
                    this.getAttribute("fakesr").substring(0, this.getAttribute("fakesr").indexOf("VAADIN"))
                            + "?v-r=dfd&id=%s");
            """.formatted(identifier));

        runBeforeClientResponse(ui -> {
            requestHandler = new RequestHandler() {
                @Override
                public boolean handleRequest(VaadinSession session, VaadinRequest request, VaadinResponse response) throws IOException {
                    String id = request.getParameter("id");
                    if (id != null && id.equals(identifier)) {
                        response.setStatus(200);
                        String filename = getFileName(session, request);
                        if (filename == null) {
                            filename = fileNameGenerator.getFileName(request);
                        }
                        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
                        response.setHeader("Content-Type", contentTypeGenerator.getContentType());
                        try {
                            contentWriter.accept(response.getOutputStream());
                        } catch (Exception e) {
                            try {
                                response.setStatus(500);
                            } catch (Exception e2) {
                                // most likely header already sent
                            }
                            getUI().ifPresent(ui -> ui.access(() -> {
                                DynamicFileDownloader.this.getEventBus().fireEvent(new DownloadFailedEvent(DynamicFileDownloader.this, e));
                            }));
                            e.printStackTrace();
                            return true;
                        }
                        getUI().ifPresent(ui -> ui.access(() -> {
                            DynamicFileDownloader.this.getEventBus().fireEvent(new DownloadFinishedEvent(DynamicFileDownloader.this, false));
                        }));
                        return true;
                    }
                    return false;
                }
            };

            ui.getSession().addRequestHandler(requestHandler);

            getElement().setAttribute("download", "");
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        detachEvent.getSession().removeRequestHandler(requestHandler);
        super.onDetach(detachEvent);
    }

    /**
     * Adds a listener that is executed when the file content has been streamed.
     * Note that the UI changes done in the listener don't necessarily happen
     * live if you don't have @{@link com.vaadin.flow.component.page.Push} in
     * use or use {@link UI#setPollInterval(int)} method.
     *
     * @param listener the listener
     * @return the {@link Registration} you can use to remove this listener.
     */
    public Registration addDownloadFinishedListener(ComponentEventListener<DownloadFinishedEvent> listener) {
        return addListener(DownloadFinishedEvent.class, listener);
    }

    /**
     * Adds a listener that is executed when the file content streaming has
     * failed due to an exception. Note that the UI changes done in the listener
     * don't necessarily happen live if you don't have
     * {@link com.vaadin.flow.component.page.Push} in use or use
     * {@link UI#setPollInterval(int)} method.
     *
     * @param listener the listener
     * @return the {@link Registration} you can use to remove this listener.
     */
    public Registration addDownloadFailedListener(ComponentEventListener<DownloadFailedEvent> listener) {
        return addListener(DownloadFailedEvent.class, listener);
    }

    /**
     * Sets the file handler that generates the file content.
     * @param contentWriter the file handler
     */
    public void setFileHandler(SerializableConsumer<OutputStream> contentWriter) {
        this.contentWriter = contentWriter;
    }

    /**
     * Sets the file name of downloaded file.
     * @param fileName the file name
     */
    public void setFileName(String fileName) {
        this.fileNameGenerator = r -> fileName;
    }

    /**
     * Gets the filename of downloaded file. Override if you want to generate
     * the name dynamically.
     *
     * @param session the vaadin session
     * @param request the vaadin request
     * @return the file name
     * @deprecated provide FileNameGenerator instead
     */
    @Deprecated
    protected String getFileName(VaadinSession session, VaadinRequest request) {
        return null;
    }

    void runBeforeClientResponse(SerializableConsumer<UI> command) {
        getElement().getNode().runWhenAttached(ui -> ui
                .beforeClientResponse(this, context -> command.accept(ui)));
    }

    /**
     * Makes the download look like a button instead of a normal link.
     *
     * @return the same instance, fluent method
     */
    public DynamicFileDownloader asButton() {
        String text = getText();
        setText(null);
        this.button = new Button(text);
        add(button);
        return this;
    }

    /**
     * @return a Button component wrapped inside the file downloader, if
     * configured as a Button
     */
    public Button getButton() {
        if (button == null) {
            throw new IllegalStateException("asButton() is not called!");
        }
        return button;
    }

    /**
     * Sets the strategy to creates the name of the downloaded file.
     *
     * @param fileNameGenerator the generator
     */
    public void setFileNameGenerator(FileNameGenerator fileNameGenerator) {
        this.fileNameGenerator = fileNameGenerator;
    }

    /**
     * Fluent method to set the strategy to creates the name of the downloaded.
     * @param fileNameGenerator the generator
     * @return the same instance, fluent method
     */
    public DynamicFileDownloader withFileNameGenerator(FileNameGenerator fileNameGenerator) {
        setFileNameGenerator(fileNameGenerator);
        return this;
    }

    public void setContentTypeGenerator(ContentTypeGenerator contentTypeGenerator) {
        this.contentTypeGenerator = contentTypeGenerator;
    }

    public DynamicFileDownloader withContentTypeGenerator(ContentTypeGenerator contentTypeGenerator) {
        setContentTypeGenerator(contentTypeGenerator);
        return this;
    }

    /**
     * @see HasTooltip#setTooltipText(String)
     *
     * Note, that tooltips are only supported if the content of the link
     * supports them. For example, tooltips are supported if the
     * {@link #asButton()} method is called.
     *
     * @param text the tooltip text
     */
    @Override
    public Tooltip setTooltipText(String text) {
        // Anchor does not implement HasTooltip, hack to content
        // (often a Button) -> works
        HasTooltip component = (HasTooltip) getChildren().findFirst().get();
        return component.setTooltipText(text);
    }

    @Override
    public Tooltip getTooltip() {
        HasTooltip component = (HasTooltip) getChildren().findFirst().get();
        return component.getTooltip();
    }
}
