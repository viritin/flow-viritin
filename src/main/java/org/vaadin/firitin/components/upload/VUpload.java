package org.vaadin.firitin.components.upload;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
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
}
