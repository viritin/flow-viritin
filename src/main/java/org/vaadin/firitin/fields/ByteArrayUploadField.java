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
package org.vaadin.firitin.fields;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.internal.AbstractFieldSupport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.StreamResourceWriter;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import elemental.json.Json;

/**
 *
 * @author mstahv
 */
@StyleSheet("context://frontend/org/vaadin/firitin/bytearrayuploadfield.css")
public class ByteArrayUploadField extends Composite<Div>
		implements HasValue<ComponentValueChangeEvent<ByteArrayUploadField, byte[]>, byte[]> {
	private static final long serialVersionUID = -188233227963143771L;

	@SuppressWarnings("unused")
	private byte[] value;

	private ByteArrayOutputStream bout;

	private final AbstractFieldSupport<ByteArrayUploadField, byte[]> fieldSupport;

	private Upload upload = new Upload();

	private Receiver receiver = new Receiver() {
		private static final long serialVersionUID = 5809889357612029151L;

		@Override
		public OutputStream receiveUpload(String filename, String mimetype) {
			uploadedFileName = filename;
			bout = new ByteArrayOutputStream();
			return bout;
		}
	};
	private Anchor presentation;
	private String downloadFileName;
	private String fileDownloadText = "download ( %s )";
	private String uploadedFileName;

	public ByteArrayUploadField() {
		getContent().addClassName("viritin-uploadfield");
		upload.setReceiver(receiver);
		upload.setMaxFiles(1);
		upload.setDropAllowed(false);

		upload.addSucceededListener(e -> {
			setValue(bout.toByteArray());
			// WTF Vaadin component developers, this is NOT cool :-(
			upload.getElement().setPropertyJson("files", Json.createArray());
		});
		getContent().add(upload);

		fieldSupport = createFieldSupport(new byte[0]);
	}
	
	private AbstractFieldSupport<ByteArrayUploadField, byte[]> createFieldSupport(byte[] defaultValue) {
        return new AbstractFieldSupport<>(this, defaultValue, this::valueEquals, this::setPresentatinoValue);
    }

	private boolean valueEquals(byte[] v1, byte[] v2) {
		if (v1 == null && v2 == null) {
			return true;
		} else if (v1 == null) {
			return false;
		}
		return v1.equals(v2);
	}
	
	private void setPresentatinoValue(byte[] value) {
		this.value = value;
		updatePresentation();
	}

	private void updatePresentation() {
		if(presentation == null) {
			presentation = new Anchor();
			presentation.setTitle("Download as file");
			getContent().add(presentation);
		}
		if(value == null) {
			presentation.setVisible(false);
		} else {
			presentation.setVisible(true);
			presentation.removeAll();
			Icon icon = VaadinIcon.DOWNLOAD_ALT.create();
			icon.addClassName("--lumo-icon-size-s");
			presentation.add(icon);
			presentation.add(new Span(String.format(getFileDownloadText(), readableFileSize(value.length))));
			String filename =
					getFileNameForDownload();
			presentation.getElement().setAttribute("download", filename);
			presentation.setHref(new StreamResource(filename, (stream, session) -> stream.write(value)));
		}
	}

	private String getFileNameForDownload() {
		return getDownloadFileName() == null ? uploadedFileName == null ? "file" : uploadedFileName : getDownloadFileName();
	}

	public String getDownloadFileName() {
		return downloadFileName;
	}

	/**
	 * Sets the name of the file that people download if they download the file directly
	 * @param downloadFileName the file name
	 */
	public void setDownloadFileName(String downloadFileName) {
		this.downloadFileName = downloadFileName;
	}

	/**
	 * Sets the (formatted text) that is shown in a link if the value is not null to download the contents of the byte array.
	 * String parameter for formatting is the human readable size of the byte array.
	 *
	 * @param fileDownloadText the text, default "download ( %s )"
	 */
	public void setFileDownloadText(String fileDownloadText) {
		this.fileDownloadText = fileDownloadText;
	}

	public String getFileDownloadText() {
		return fileDownloadText;
	}

	@Override
	public Registration addValueChangeListener(
			ValueChangeListener<? super ComponentValueChangeEvent<ByteArrayUploadField, byte[]>> listener) {
		return fieldSupport.addValueChangeListener(listener);
	}

	@Override
	public void setReadOnly(boolean bln) {
		upload.setVisible(!bln);
	}

	@Override
	public boolean isReadOnly() {
		return !upload.isVisible();
	}

	@Override
	public void setRequiredIndicatorVisible(boolean bln) {
		throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
																		// Tools | Templates.
	}

	@Override
	public boolean isRequiredIndicatorVisible() {
		throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
																		// Tools | Templates.
	}

	@Override
	public void setValue(byte[] v) {
		fieldSupport.setValue(v);
	}

	@Override
	public byte[] getValue() {
		return fieldSupport.getValue();
	}

	public static String readableFileSize(long size) {
		if(size <= 0) return "0";
		final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public Upload getUpload() {
		return upload;
	}

    public void setUploadCaption(String uploadCaption) {
		getUpload().setUploadButton(new Button(uploadCaption));
    }
}
