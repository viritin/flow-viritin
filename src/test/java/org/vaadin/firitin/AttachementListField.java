package org.vaadin.firitin;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.MultiFileReceiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;
import elemental.json.Json;
import org.vaadin.firitin.components.button.DeleteButton;
import org.vaadin.firitin.components.button.VButton;
import org.vaadin.firitin.components.orderedlayout.VHorizontalLayout;
import org.vaadin.firitin.components.orderedlayout.VVerticalLayout;
import org.vaadin.firitin.components.textfield.VTextField;
import org.vaadin.firitin.form.AbstractForm;
import org.vaadin.firitin.testdomain.Attachment;

import java.io.*;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AttachementListField extends Composite<VerticalLayout>
        implements HasValue<HasValue.ValueChangeEvent<List<Attachment>>, List<Attachment>>, HasSize {

    private static final Path rootDirectory = Path.of(System.getProperty("user.dir"), "target", "testuploads");

    static {
        rootDirectory.toFile().mkdir();
    }

    class RowEditor extends AbstractForm<Attachment> {

        Anchor download = new Anchor();
        Span size = new Span();
        TextField name = new VTextField().withPlaceholder("Name");

        public RowEditor() {
            super(Attachment.class);
        }

        @Override
        protected Component createContent() {
            download.add(VaadinIcon.DOWNLOAD.create());
            download.getElement().setAttribute("download", "");
            return new VHorizontalLayout(download, size, name).alignAll(FlexComponent.Alignment.CENTER).withPadding(false);
        }

        @Override
        public void setEntity(Attachment entity) {
            super.setEntity(entity);
            download.setHref(new StreamResource(entity.getPath().getFileName().toString(), () -> {
                try {
                    return new FileInputStream(entity.getPath().toFile());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }));
            size.setText(readableFileSize(entity.getSize()));
        }
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private List<Attachment> value;

    VerticalLayout existingValues = new VVerticalLayout()
            .withMargin(false)
            .withPadding(false)
            .withSpacing(false);

    public AttachementListField() {
        value = new ArrayList<>();

        getContent().setPadding(false);

        MultiFileReceiver multiFileReceiver = new MultiFileReceiver() {

            @Override
            public OutputStream receiveUpload(String filename, String mimetype) {
                FileOutputStream fout = null;
                try {
                    File file = rootDirectory.resolve(filename).toFile();
                    fout = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                return fout;
            }
        };
        Upload multiFileUpload = new Upload(multiFileReceiver);

        multiFileUpload.addSucceededListener(event -> {
            // Determine which file was uploaded
            String fileName = event.getFileName();
            long contentLength = event.getContentLength();
            Attachment attachment = new Attachment();
            attachment.setName(fileName);
            attachment.setPath(rootDirectory.resolve(fileName));
            attachment.setSize(contentLength);
            getValue().add(attachment);
            addRow(attachment);
            fireValueChange();
            // WTF Vaadin component developers, this is NOT cool :-(
            multiFileUpload.getElement().setPropertyJson("files", Json.createArray());
        });


        getContent().add(
                new H3("Attachments"),
                existingValues,
                multiFileUpload);

    }

    private void addRow(Attachment newValue) {
        VHorizontalLayout row = new VHorizontalLayout();
        RowEditor editor = new RowEditor();
        editor.getBinder().addValueChangeListener(e -> {
            // mutable object, just fire value change on next level
            fireValueChange();
        });

        DeleteButton deleteButton = new DeleteButton()
                .withConfirmHandler(() -> {
                    value.remove(editor.getEntity());
                    editor.getEntity().getPath().toFile().delete();
                    existingValues.remove(row);
                    fireValueChange();
                })
                .withIcon(VaadinIcon.TRASH.create())
                .withPromptText("Are you sure you want to delete file " + newValue.getName());
        row.add(editor, deleteButton);
        editor.setEntity(newValue);
        existingValues.add(row);
    }

    private void fireValueChange() {
        fireEvent(new AbstractField.ComponentValueChangeEvent<AttachementListField, List<Attachment>>(this, this, null, true));
    }

    @Override
    public void setValue(List<Attachment> value) {
        this.value = value;
        existingValues.removeAll();
        value.forEach(this::addRow);
    }

    @Override
    public List<Attachment> getValue() {
        return value;
    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<? super ValueChangeEvent<List<Attachment>>> listener) {
        @SuppressWarnings("rawtypes")
        ComponentEventListener componentListener = event -> {
            AbstractField.ComponentValueChangeEvent<AttachementListField, List<Attachment>> valueChangeEvent = (AbstractField.ComponentValueChangeEvent<AttachementListField, List<Attachment>>) event;
            listener.valueChanged(valueChangeEvent);
        };
        return addListener(AbstractField.ComponentValueChangeEvent.class,
                componentListener);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        getContent().setEnabled(false);
    }

    @Override
    public boolean isReadOnly() {
        return getContent().isEnabled();
    }

    @Override
    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {

    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        return false;
    }
}
