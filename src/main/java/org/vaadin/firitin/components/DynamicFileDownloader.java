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

import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.server.RequestHandler;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinSession;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import org.vaadin.firitin.fluency.ui.FluentComponent;

/**
 * An anchor which content is produced dynamically.
 *
 * @see #setFileName(java.lang.String)
 * @see #setFileHandler(com.vaadin.flow.function.SerializableConsumer)
 *
 * @author mstahv
 */
public class DynamicFileDownloader extends Anchor implements FluentComponent<DynamicFileDownloader> {

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
                        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                        contentWriter.accept(response.getOutputStream());
                        return true;
                    }
                    return false;
                }
            };

            ui.getSession().addRequestHandler(requestHandler);

            setHref("./" + identifier);
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        getUI().get().getSession().removeRequestHandler(requestHandler);
        super.onDetach(detachEvent);
    }

    public void setFileHandler(SerializableConsumer<OutputStream> contentWriter) {
        this.contentWriter = contentWriter;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    void runBeforeClientResponse(SerializableConsumer<UI> command) {
        getElement().getNode().runWhenAttached(ui -> ui
                .beforeClientResponse(this, context -> command.accept(ui)));
    }

}
