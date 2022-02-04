/*
 * Copyright 2021 by Stefan Uebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.firitin.fields.internalhtmltable;

import com.vaadin.flow.component.HasElement;

/**
 * A mixing interface for components, that have a "colspan" attribute.
 * @author Stefan Uebe
 */
public interface HasColSpan extends HasElement {
    String ATTRIBUTE_COLSPAN = "colspan";

    /**
     * Set a colspan for this instance. Must be a non negative integer (further checks are delegated to the browser).
     * @param colSpan col span
     */
    default void setColSpan(int colSpan) {
        if (colSpan < 0) {
            throw new IllegalArgumentException("Colspan must be a positive number");
        }
        getElement().setAttribute(ATTRIBUTE_COLSPAN, String.valueOf(colSpan));
    }

    /**
     * Returns the colspan set for this instance. Defaults to 1 if none has been set before.
     * @return colspan
     */
    default int getColSpan() {
        String colspan = getElement().getAttribute(ATTRIBUTE_COLSPAN);
        if (colspan == null) {
            colspan = "1";
        }
        return Integer.parseInt(colspan);
    }

    /**
     * Resets the colspan to its default (1).
     */
    default void resetColSpan() {
        getElement().removeAttribute(ATTRIBUTE_COLSPAN);
    }
}
