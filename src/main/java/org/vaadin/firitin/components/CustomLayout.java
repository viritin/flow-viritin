/*
 * Copyright 2022 Viritin.
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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;

import java.util.HashMap;
import java.util.Map;

/**
 * A replacement for V7/8 era custom layout component for which one can
 * provide a template dynamically and still place Vaadin components into
 * it.
 *
 * Slots where you want to put component(s), must be identified with id, e.g.
 *
 * <pre>
 * &lt;div&gt;&lt;h1&gt;Hello!&lt;h1&gt;&lt;div id=&quot;tmpl-slot&quot;&gt;&lt;/div&gt;&lt;/div&gt;
 * </pre>
 */
public class CustomLayout extends Composite<Div> {

    Map<String,Component> slotToComponent = new HashMap<>();

    public CustomLayout() {
        this.getElement().getNode().runWhenAttached((ui) -> {
            ui.beforeClientResponse(this, (context) -> {
                attachComponents();
            });
        });
    }

    private void attachComponents() {
        slotToComponent.forEach((slotId, c) -> {
            getElement().executeJs("document.getElementById(\"" + slotId + "\").appendChild($0);", c.getElement());
        });
        slotToComponent.clear();
    }

    /**
     * Creates a new CustomLayout with given html template. Note, that html
     * is handled as raw, so be sure to sanitize it if you can't trust the
     * source.
     *
     * @param htmlTemplate the html template.
     */
    public CustomLayout(String htmlTemplate) {
        this();
        setTemplate(htmlTemplate);
    }

    /**
     * Adds component to an element identified by an id.
     *
     * @param slotId the id of the slot into the component should be added
     * @param c  the component to add
     */
    public void addComponent(String slotId, Component c) {
        // Establish parent-child relationship, but leave DOM attaching to us
        getElement().appendVirtualChild(c.getElement());
        slotToComponent.put(slotId,c);
    }

    public void remove(Component child) {
        child.getElement().executeJs("this.remove();").then(j -> {
            getElement().removeVirtualChild(child.getElement());
        });
    }

    /**
     * Sets the html template. Note, that html is handled as raw, so be sure
     * to sanitize it if you can't trust the source.
     *
     * @param htmlTemplate the html template.
     */
    public void setTemplate(String htmlTemplate) {
        getElement().executeJs("this.innerHTML = $0", htmlTemplate);
    }
    
}
