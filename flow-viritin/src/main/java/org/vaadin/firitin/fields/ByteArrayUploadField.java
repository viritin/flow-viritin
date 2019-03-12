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

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.shared.Registration;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

/**
 *
 * @author mstahv
 */
public class ByteArrayUploadField extends Composite<Upload> implements HasValue<HasValue.ValueChangeEvent<byte[]>, byte[]> {

    private byte[] value;

    private ByteArrayOutputStream bout;

    private Receiver receiver = new Receiver() {
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
            value = bout.toByteArray();
            // TODO fire value change event
            getContent().getChildren().forEach(c -> System.out.println(c.getElement().getTag()));
        }
        );

        getContent().addSucceededListener(e -> {
            Notification.show("Finished");
            value = bout.toByteArray();
            // TODO fire value change event
            getContent().getChildren().forEach(c -> System.out.println(c.getElement().getTag()));
        }
        );

    }

    @Override
    public Registration addValueChangeListener(ValueChangeListener<? super ValueChangeEvent<byte[]>> vl) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setReadOnly(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isReadOnly() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRequiredIndicatorVisible(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isRequiredIndicatorVisible() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setValue(byte[] v) {
        value = v;
    }

    @Override
    public byte[] getValue() {
        return value;
    }

}
