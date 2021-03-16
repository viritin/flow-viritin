package org.vaadin.firitin.components.upload;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.upload.*;
import org.vaadin.firitin.fluency.ui.FluentComponent;
import org.vaadin.firitin.fluency.ui.FluentHasSize;
import org.vaadin.firitin.fluency.ui.FluentHasStyle;

public class VUpload extends Upload implements FluentComponent<VUpload>, FluentHasStyle<VUpload>, FluentHasSize<VUpload> {

    public VUpload() {
        super();
    }

    public VUpload(Receiver receiver) {
        super(receiver);
    }

    public VUpload withMaxFiles(int maxFiles) {
        setMaxFiles(maxFiles);
        return this;
    }

    public VUpload withMaxFileSize(int maxFileSize) {
        setMaxFileSize(maxFileSize);
        return this;
    }


    public VUpload withAutoUpload(boolean autoUpload) {
        setAutoUpload(autoUpload);
        return this;
    }


    public VUpload withDropAllowed(boolean dropAllowed) {
        setDropAllowed(dropAllowed);
        return this;
    }

    public VUpload withAcceptedFileTypes(String... acceptedFileTypes) {
        setAcceptedFileTypes(acceptedFileTypes);
        return this;
    }

    public VUpload withUploadButton(Component uploadButton) {
        setUploadButton(uploadButton);
        return this;
    }

    public VUpload withDropLabel(Component dropLabel) {
        setDropLabel(dropLabel);
        return this;
    }

    public VUpload withDropLabelIcon(Component dropLabelIcon) {
        setDropLabelIcon(dropLabelIcon);
        return this;
    }

    public VUpload withI18n(UploadI18N i18n) {
        setI18n(i18n);
        return this;
    }

    public VUpload withAllFinishedListener(ComponentEventListener<AllFinishedEvent> listener) {
        addAllFinishedListener(listener);
        return this;
    }

    public VUpload withProgressListener(ComponentEventListener<ProgressUpdateEvent> listener) {
        addProgressListener(listener);
        return this;
    }

    public VUpload withFailedListener(ComponentEventListener<FailedEvent> listener) {
        addFailedListener(listener);
        return this;
    }

    public VUpload withFinishedListener(ComponentEventListener<FinishedEvent> listener) {
        addFinishedListener(listener);
        return this;
    }

    public VUpload withStartedListener(ComponentEventListener<StartedEvent> listener) {
        addStartedListener(listener);
        return this;
    }

    public VUpload withSucceededListener(ComponentEventListener<SucceededEvent> listener) {
        addSucceededListener(listener);
        return this;
    }

    public VUpload withFileRejectedListener(ComponentEventListener<FileRejectedEvent> listener) {
        addFileRejectedListener(listener);
        return this;
    }
}
