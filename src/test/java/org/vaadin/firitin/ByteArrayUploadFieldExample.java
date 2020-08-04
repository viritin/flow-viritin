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
package org.vaadin.firitin;

import org.vaadin.firitin.fields.ByteArrayUploadField;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;

/**
 *
 * @author mstahv
 */
@Route
public class ByteArrayUploadFieldExample extends VerticalLayout {
	private static final long serialVersionUID = -2994002839356529922L;

	private final Binder<Entity> binder = new Binder<>();;

    public ByteArrayUploadFieldExample() {
        final ByteArrayUploadField byteArrayUploadField = new ByteArrayUploadField();
        
        add(byteArrayUploadField);
        
        binder.forField(byteArrayUploadField).bind(Entity::getContent, Entity::setContent);
    }
    
    private static class Entity {
    	private byte[] content;
    	
    	public byte[] getContent() {
			return content;
		}
    	
    	public void setContent(byte[] content) {
			this.content = content;
		}
    }
    
}
