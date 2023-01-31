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

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.dom.DomListenerRegistration;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.server.RequestHandler;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinSession;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import com.vaadin.flow.shared.Registration;
import org.vaadin.firitin.fluency.ui.FluentComponent;

/**
 * An anchor which content is produced dynamically.
 *
 * @author mstahv
 * @see #setFileName(java.lang.String)
 * @see #setFileHandler(com.vaadin.flow.function.SerializableConsumer)
 */
public class DynamicFileDownloader extends Anchor implements FluentComponent<DynamicFileDownloader> {

    private Button button;
    private DomListenerRegistration disableOnclick;


    public void setDisableOnClick(boolean disableOnClick) {
        if (disableOnclick != null) {
            disableOnclick.remove();
        }
        if (disableOnClick) {
            disableOnclick = getElement().addEventListener("click", e -> {
                setEnabled(false);
            });
        }
    }

    public static class DownloadFinishedEvent extends ComponentEvent<DynamicFileDownloader> {

        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source     the source component
         * @param fromClient <code>true</code> if the event originated from the client
         */
        public DownloadFinishedEvent(DynamicFileDownloader source, boolean fromClient) {
            super(source, fromClient);
        }

    }
    
    public static class DownlocadFailedEvent extends ComponentEvent<DynamicFileDownloader> {
        
        private final Exception exception;

        /**
         * Creates a new event using the given source and indicator whether the
         * event originated from the client side or the server side.
         *
         * @param source     the source component
         * @param fromClient <code>true</code> if the event originated from the client
         */
        public DownlocadFailedEvent(DynamicFileDownloader source, Exception e) {
            super(source, false);
            this.exception = e;
        }

        public Exception getException() {
            return exception;
        }
        
    }


    private final String identifier = UUID.randomUUID().toString();
    private String fileName;
    private SerializableConsumer<OutputStream> contentWriter;
    protected RequestHandler requestHandler;

    public DynamicFileDownloader(String text, String fileName, SerializableConsumer<OutputStream> contentWriter) {
        this();
        setText(text);
        this.fileName = fileName;
        this.contentWriter = contentWriter;

    }

    public DynamicFileDownloader() {
        runBeforeClientResponse(ui -> {
            requestHandler = new RequestHandler() {
                @Override
                public boolean handleRequest(VaadinSession session, VaadinRequest request, VaadinResponse response) throws IOException {
                    if (request.getPathInfo().endsWith(identifier)) {
                        response.setStatus(200);
                        response.setHeader("Content-Disposition", "attachment; filename=\"" + getFileName(session, request) + "\"");
                        try {
                            contentWriter.accept(response.getOutputStream());
                        } catch (Exception e) {
                            try {
                                response.setStatus(500);
                            } catch (Exception e2) {
                                // most likely header already sent
                            }
                            getUI().ifPresent(ui -> ui.access(() -> {
                                DynamicFileDownloader.this.getEventBus().fireEvent(new DownlocadFailedEvent(DynamicFileDownloader.this, e));
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

            setHref("./" + identifier);
            if (fileName != null) {
                getElement().setAttribute("download", fileName);
            } else {
                getElement().setAttribute("download", "");
            }
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        getUI().get().getSession().removeRequestHandler(requestHandler);
        super.onDetach(detachEvent);
    }

    /**
     * Adds a listener that is executed when the file content has been streamed.
     * Note that the UI changes done in the listener don't necessarily happen
     * live if you don't have @{@link com.vaadin.flow.component.page.Push}
     * in use or use {@link UI#setPollInterval(int)} method.
     *
     * @param listener the listener
     * @return the {@link Registration}  you can use to remove this listener.
     */
    public Registration addDownloadFinishedListener(ComponentEventListener<DownloadFinishedEvent> listener) {
        return addListener(DownloadFinishedEvent.class, listener);
    }
    
    /**
     * Adds a listener that is executed when the file content streaming has 
     * failed due to an exception.
     * Note that the UI changes done in the listener don't necessarily happen
     * live if you don't have @{@link com.vaadin.flow.component.page.Push}
     * in use or use {@link UI#setPollInterval(int)} method.
     *
     * @param listener the listener
     * @return the {@link Registration}  you can use to remove this listener.
     */
    public Registration addDownloadFailedListener(ComponentEventListener<DownlocadFailedEvent> listener) {
        return addListener(DownlocadFailedEvent.class, listener);
    }
    

    public void setFileHandler(SerializableConsumer<OutputStream> contentWriter) {
        this.contentWriter = contentWriter;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the filename of downloaded file. Override if you want to generate the
     * name dynamically.
     *
     * @param session the vaadin session
     * @param request the vaadin request
     * @return the file name
     */
    protected String getFileName(VaadinSession session, VaadinRequest request) {
        return fileName;
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
     * @return a Button component wrapped inside the file downloader, if configured as a Button
     */
    public Button getButton() {
        if (button == null) {
            throw new IllegalStateException("asButton() is not called!");
        }
        return button;
    }
}
