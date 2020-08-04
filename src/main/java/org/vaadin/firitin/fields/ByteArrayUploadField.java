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
import java.io.OutputStream;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.internal.AbstractFieldSupport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.shared.Registration;

/**
 *
 * @author mstahv
 */
public class ByteArrayUploadField extends Composite<Upload>
		implements HasValue<ComponentValueChangeEvent<ByteArrayUploadField, byte[]>, byte[]> {
	private static final long serialVersionUID = -188233227963143771L;

	@SuppressWarnings("unused")
	private byte[] value;

	private ByteArrayOutputStream bout;

	private final AbstractFieldSupport<ByteArrayUploadField, byte[]> fieldSupport;

	private Receiver receiver = new Receiver() {
		private static final long serialVersionUID = 5809889357612029151L;

		@Override
		public OutputStream receiveUpload(String filename, String mimetype) {
			bout = new ByteArrayOutputStream();
			return bout;
		}
	};

	public ByteArrayUploadField() {
		getContent().setReceiver(receiver);
		getContent().setMaxFiles(1);

		getContent().addFinishedListener(e -> {
			Notification.show("Finished");
			setValue(bout.toByteArray());
		});

		getContent().addSucceededListener(e -> {
			Notification.show("Finished");
			setValue(bout.toByteArray());
		});

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
	}

	@Override
	public Registration addValueChangeListener(
			ValueChangeListener<? super ComponentValueChangeEvent<ByteArrayUploadField, byte[]>> listener) {
		return fieldSupport.addValueChangeListener(listener);
	}

	@Override
	public void setReadOnly(boolean bln) {
		throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
																		// Tools | Templates.
	}

	@Override
	public boolean isReadOnly() {
		throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
																		// Tools | Templates.
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
}
